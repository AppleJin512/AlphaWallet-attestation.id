<script lang="ts">
  import { onMount, onDestroy } from "svelte";
  import FlowStatus from "./component/FlowStatus.svelte";
  import CurrentStep from "./component/CurrentStep.svelte";
  import * as walletService from "./common/WalletService";
  import { createAndReturnAttestationFromMagicLink, setMagicLinkData } from "./common/MagicLink";
  import * as flow from "./common/Flow";
  import { current } from "./common/Flow";
  import {bigintToHex, hexToBigint} from "bigint-conversion";
  import {createAttestationRquestAndSecret, parseAttestation} from "./attestation/AttesationUtils";
  import {clearAttestation, getRawAttestation, getRawCurrentAccount, saveAttestation} from "./common/AppState";

  import {
    STEP_CONNECT_WALLET
  } from "./common/Flow";

  flow.loadCurrentStep();

  let attestation;
  let attestationExpired;

  onMount(async () => {
    if (await walletService.isEnabled()) {
      await walletService.connect($current);
      if ($current === flow.start) {
        current.set(flow.transition[flow.start].nextStep());
      }
    } else {
      current.set(flow.start);
    }
    gotoFirstStepWhenAttestationExpired();

    registerEventListenerWhenCreatedByIntegration();
  });

  onDestroy(() => {
    removeEventListenerAnyway();
  });

  function gotoFirstStepWhenAttestationExpired() {
    attestation = getRawAttestation();
    if (attestation) {
      const myAttestation = parseAttestation(attestation.attestation);

      console.log(myAttestation);

      attestationExpired = expired(myAttestation);
      if (attestationExpired) {
        console.error("attestation is expired, please apply a new one.");
        clearAttestation();
        current.set(flow.start);
      }
    }
  }

  function registerEventListenerWhenCreatedByIntegration() {
    if (window.location !== window.parent.location) {
      window.addEventListener(
        "message",
        (event) => {
          if (event.data.force) {
            current.set(flow.start);
            reply({
              display: true,
            });
          } else if (event.data.force === false) {
            tryToReturnAttestation(event.data);
          }
        },
        false
      );

      reply({
        ready: true,
      });
    }
  }

  const tryToReturnAttestation = async function (data) {
    if (!attestation || attestationExpired) {
      console.log('!attestation || attestationExpired');
      if (data.magicLink && data.email) {

        setMagicLinkData(data.email, data.magicLink);

        let rawCurrentAccount = getRawCurrentAccount();
        if (rawCurrentAccount) {
          createAndReturnAttestationFromMagicLink();
        } else {
          if (await walletService.isEnabled()) {
            await walletService.connect(STEP_CONNECT_WALLET);
          }
          current.set(STEP_CONNECT_WALLET);
          reply({
            display: true,
          });
        }


      } else {
        reply({
          display: true,
        });
      }
    } else {
      reply({
        attestation: attestation.attestation,
        requestSecret: hexToBigint(attestation.requestSecret),
        display: false,
      });
    }
  };

  function expired(attestation) {
    return (
      attestation.signedInfo.validity.value.notAfter.generalizedTime.getTime() <
      new Date().getTime()
    );
  }

  function reply(message) {
    parent.postMessage(message, "*");
  }

  function removeEventListenerAnyway() {
    window.removeEventListener("message", null);
  }
</script>

<main>
  <FlowStatus />
  <CurrentStep />
</main>

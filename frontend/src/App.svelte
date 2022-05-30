<script lang="ts">
  import { hexToBigint } from "bigint-conversion";
  import { onDestroy, onMount } from "svelte";
  import { parseAttestation } from "./attestation/AttesationUtils";
  import {
    clearAll,
    clearAttestation,
    getCurrentEmail,
    getRawAttestation,
    getRawCurrentAccount,
    requestEmail,
    sameEmail,
  } from "./common/AppState";
  import { initAuth } from "./common/AuthService";
  import * as flow from "./common/Flow";
  import { current } from "./common/Flow";
  import {
    createAndReturnAttestationFromMagicLink,
    setMagicLinkData,
  } from "./common/MagicLink";
  import CurrentStep from "./component/CurrentStep.svelte";
  import FlowStatus from "./component/FlowStatus.svelte";

  let attestation;
  let attestationExpired;

  onMount(() => {
    clearWhenAttestationExpired();
    registerEventListenerWhenCreatedByIntegration();
    initAuth();
  });

  onDestroy(() => {
    removeEventListenerAnyway();
  });

  function clearWhenAttestationExpired() {
    attestation = getRawAttestation();
    if (attestation) {
      const myAttestation = parseAttestation(attestation.attestation);

      console.log(myAttestation);

      attestationExpired = expired(myAttestation);
      if (attestationExpired) {
        console.error("attestation is expired, please apply a new one.");
        clearAttestation();
      }
    }
  }

  function registerEventListenerWhenCreatedByIntegration() {
    if (window.location !== window.parent.location) {
      window.addEventListener(
        "message",
        (event) => {
          if (event.data.force) {
            clearAll();
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
    if (data.email) {
      const savedEmail = await getCurrentEmail();
      if (data.email != savedEmail) {
        $requestEmail = data.email;
        clearAttestation();
        reply({
          display: true,
        });
        return;
      } else {
        $sameEmail = true;
      }
    }

    if (!attestation || attestationExpired) {
      console.log("!attestation || attestationExpired");
      if (data.magicLink && data.email) {
        setMagicLinkData(data.email, data.magicLink);

        let rawCurrentAccount = getRawCurrentAccount();
        if (rawCurrentAccount) {
          createAndReturnAttestationFromMagicLink();
        } else {
          current.set(flow.start);
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
      attestation.signedInfo.validity.notAfter.generalizedTime.getTime() <
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

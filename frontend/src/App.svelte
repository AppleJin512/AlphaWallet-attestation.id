<script lang="ts">
  import { hexToBigint } from "bigint-conversion";
  import { onDestroy, onMount } from "svelte";
  import { parseAttestation, expired } from "./attestation/AttesationUtils";
  import {
    clearAll,
    clearAttestation,
    getCurrentEmail,
    getRawAttestation,
    providerName,
    requestEmail,
    sameEmail,
  } from "./common/AppState";
  import { initAuth } from "./common/AuthService";
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
    if (data.debug) {
      console.log("attestation.id postMessage data received: ", data);
    }

    if (data.providerName) {
      $providerName = data.providerName;
    }

    if (data.email) {
      // moved up to always set $requestEmail
      // we have to set $requestEmail because if attestation expired then we require filled email
      $requestEmail = data.email;
      const savedEmail = await getCurrentEmail();
      if (data.email != savedEmail) {
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

      reply({
        display: true,
      });
    } else {
      reply({
        attestation: attestation.attestation,
        requestSecret: hexToBigint(attestation.requestSecret),
        display: false,
      });
    }
  };

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

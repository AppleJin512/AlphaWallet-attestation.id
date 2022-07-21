<script lang="ts">
  import { metatags, goto } from '@roxi/routify';
  import { hexToBigint } from "bigint-conversion";
  import { onDestroy, onMount } from "svelte";
  import { parseAttestation, expired } from "../attestation/AttesationUtils";
  import {
      saveType,
      providerName,
      getType,
      getEmail,
      requestEmail,
      attestationDb,
      keccak256,
      testValidity,
      auth0AccessToken,
      isVerified,
      confirmErrorMsg
  } from "../common/AppState";
  import { initAuth } from "../common/AuthService";
  import * as flow from "../common/Flow";
  import {
    current,
    STEP_CONFIRMATION,
    STEP_ENTER_EMAIL,
    STEP_CONNECT_WALLET
  } from "../common/Flow";
  import { errorMsgPipe } from "../common/Utils";
  
  metatags.title = 'Attestation.id';
  metatags.description = 'v1.0.0';

  let attestation;
  let attestationExpired;

  const submit = async function () {
    if (window.location.hash) {
      parseUrl(window.location.href, async (err, authResult) => {
        if (err) {
          console.log(err);
          $confirmErrorMsg = err.message || err.errorDescription;
          if (history.replaceState) {
            history.replaceState({}, "", "/");
          }
          $goto("/confirm");
        } else {
          if (authResult) {
            try {
              $isVerified = true;
              $auth0AccessToken = authResult.accessToken;

              if (history.replaceState) {
                history.replaceState({}, "", "/");
              }

              flow.saveCurrentStep(flow.STEP_CONNECT_WALLET);
            } catch (error) {
              $confirmErrorMsg = errorMsgPipe(error.message);
              $goto("/confirm");
            }
          }
        }
      });
    }
  };

  function parseUrl(href, parseHandler) {
    const access_token = href.match(/\#(?:access_token)\=([\S\s]*?)\&/)[1];
    parseHandler(null, { accessToken: access_token });
  }

  onMount(() => {
    saveType("email");
    registerEventListenerWhenCreatedByIntegration();
    initAuth();

    window.addEventListener(
      "hashchange",
      function () {
        console.log("The hash has changed!");
        submit();
      },
      false
    );

    if ($current === STEP_ENTER_EMAIL) {
      $goto("/email");
    } else if ($current === STEP_CONFIRMATION) {
      submit();
      $goto("/confirm");
    } else if ($current === STEP_CONNECT_WALLET) {
      $goto("/connect");
    }
  });
  
  onDestroy(() => {
    removeEventListenerAnyway();
  });

  function registerEventListenerWhenCreatedByIntegration() {
    if (window.location !== window.parent.location) {
      window.addEventListener(
        "message",
        (event) => {
          if (event.data.type !== undefined)
            saveType(event.data.type);
          if (event.data.force) {
            
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
    if (data.validity !== undefined && typeof data.validity === "number" && data.validity > 0) {
      $testValidity = data.validity;
    }
    if (getType() === "email" && (!data.value && !data.account)) {
      if (data.providerName) {
        $providerName = data.providerName;
      }
      if (data.email) {
        if (data.address) {
        attestation = await attestationDb.getAttestation(getType(), keccak256(data.email.toLowerCase()), keccak256(data.address.toLowerCase()));
        } else {
        attestation = await attestationDb.getFirstAttestationByEmail(getType(), keccak256(data.email.toLowerCase()));  
        }
        returnAttestation(data.email);
        return;
      }
      const currentEmail = await getEmail();
      attestation = await attestationDb.getFirstAttestationByEmail(getType(), keccak256(currentEmail.toLowerCase()));      
      returnAttestation("");
      return;
    }

    if (data.value === undefined || data.account === undefined) {
      reply({
        display: true,
      })
      return;
    }
    
    attestation = await attestationDb.getAttestation(getType(), keccak256(data.value.toLowerCase()), keccak256(data.account.toLowerCase()));
    returnAttestation(data.value);
      
  };

  function returnAttestation(requestValue) {
    if (attestation) {
      const myAttestation = parseAttestation(attestation.attestation);
      console.log("myAttestation->", myAttestation);
      attestationExpired = expired(myAttestation);
      if (attestationExpired) {
        console.error("attestation is expired, please apply a new one");
      }
    }
  
    if (!attestation || attestationExpired) {
      console.log("!attestation || attestationExpired");
      reply({
        display: true,
      });
      $requestEmail = "";
      $requestEmail = requestValue;
    } else {
      reply({
        attestation: attestation.attestation,
        requestSecret: hexToBigint(attestation.requestSecret),
        display: false,
      });
    }
  }

  function reply(message) {
    parent.postMessage(message, "*");
  }

  function removeEventListenerAnyway() {
    window.removeEventListener("message", null);
    window.removeEventListener("hashchange", null);
  }
    
</script>

<main>
</main>
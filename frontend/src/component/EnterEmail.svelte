<script>
  import { onMount, onDestroy } from "svelte";
  import * as cryptoUtils from "../common/CryptoUtils";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import {
    getRawPair,
    saveEmail,
    savePair,
    requestEmail,
    getRawAttestation,
  } from "../common/AppState";

  import { authHandler } from "../common/AuthService";

  import { parseAttestation, expired } from "../attestation/AttesationUtils";

  let disabled;
  let isLoading = false;
  let email = "";
  let inputDisabled = "";
  let inIframe = window.location !== window.parent.location;

  let attestationValid = false;
  let autoSubmitFired = false;

  async function onSubmit(token) {
    if (email) {
      if (token) {
        disableSubmitBtn();
        isLoading = true;
        try {
          $authHandler.sendemail(email, async (err, res) => {
            if (!err) {
              cryptoUtils.generateKey().then(async (result) => {
                savePair({
                  publicKey: result.publicKey,
                  privateKey: result.privateKey,
                });
                saveEmail(
                  await cryptoUtils.encrypt(getRawPair().publicKey, email)
                );

                flow.saveCurrentStep(flow.transition[$current].nextStep);
              });
            } else {
              isLoading = false;
            }
          });
        } catch (e) {
          console.error(e);
          isLoading = false;
          disableSubmitBtn();
        }
      }
    }
  }

  const validateEmail = (value) => {
    email = value;
    if (email && /\S+@\S+\.\S+/.test(email)) {
      enableSubmitBtn();
    } else {
      disableSubmitBtn();
    }
  };

  function disableSubmitBtn() {
    disabled = "disabled";
  }

  function enableSubmitBtn() {
    disabled = "";
  }

  onMount(async () => {
    validateEmail(email);

    let attestation = getRawAttestation();
    let parsedAttestation;
    if (attestation) {
      parsedAttestation = parseAttestation(attestation.attestation);
      if (!expired(parsedAttestation)) {
        attestationValid = true;
      }
    }
  });

  $: if ($requestEmail && inIframe) {
    email = $requestEmail;
    validateEmail(email);

    if ($requestEmail) {
      inputDisabled = "disabled";
    }

    if (!attestationValid) {
      // if input disabled then auto-click "Submit"
      if (!disabled && !autoSubmitFired) {
        autoSubmitFired = true;
        onSubmit(1);
      }
    }
  }
</script>

<div class="title">Request Email Attestation</div>
<div class="content">
  Enter your email address which you want to request an attestation for. You
  will have to copy and paste the code that you will find in the email we will
  be sending you.
</div>
<div class="input-div">
  <div class="label">Enter Email Address</div>
  <input
    id="value"
    name="value"
    type="text"
    autocapitalize="off"
    autocorrect="off"
    value={email}
    disabled={inputDisabled}
    on:keyup={({ target: { value } }) => validateEmail(value)}
  />

  <button on:click={onSubmit} id="submitBtn" {disabled}>
    {#if isLoading}
      <div class="loading" />
    {:else}
      Submit
    {/if}
  </button>
</div>

<style>
  .content {
    max-width: 396px;
  }
</style>

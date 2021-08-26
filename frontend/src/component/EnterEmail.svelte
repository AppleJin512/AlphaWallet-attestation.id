<script>
  import { onMount, onDestroy } from "svelte";
  import * as cryptoUtils from "../common/CryptoUtils";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import { getRawPair, saveEmail, saveOTP, savePair } from "../common/AppState";

  let disabled;
  let isLoading = false;
  let email = "";

  const BASE_BACKEND_URL = __myapp.env.BASE_BACKEND_URL;
  const RECAPTCHA_CLIENT_KEY = __myapp.env.RECAPTCHA_CLIENT_KEY;

  async function onSubmit(token) {
    if (email) {
      document.getElementById("captcha-form").submit();

      if (token) {
        disableSubmitBtn();
        isLoading = true;
        fetch(BASE_BACKEND_URL + "/api/otp", {
          method: "POST",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            type: "mail",
            value: email,
            publicKey: getRawPair().publicKey,
            "g-recaptcha-response": token,
          }),
        })
          .then(async (response) => {
            isLoading = false;
            if (response.status === 201) {
              let result = await response.json();
              if (result.otpEncrypted) {
                flow.saveCurrentStep(flow.transition[$current].nextStep);
                saveOTP(result.otpEncrypted);
                saveEmail(
                  await cryptoUtils.encrypt(getRawPair().publicKey, email)
                );
              }
            }
          })
          .catch((error) => {
            console.log(error);
            isLoading = false;
            disableSubmitBtn();
          });
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
    window.onSubmit = onSubmit;
    window.onload = () => {
      validateEmail(email);
    };
    cryptoUtils.generateKey().then((result) => {
      savePair({
        publicKey: result.publicKey,
        privateKey: result.privateKey,
      });
    });
  });

  onDestroy(() => {
    window.onSubmit = null;
    window.onload = null;
  });
</script>

<svelte:head
  ><script
    src="https://www.google.com/recaptcha/api.js?onload=onload"
    async
    defer>
  </script></svelte:head
>

<div class="title">Request Email Attestation</div>
<div class="content">
  Enter your email address you want to request an attestation. You will have to
  copy and paste the code, that you will find in the email.
</div>
<div class="input-div">
  <form id="captcha-form" action="javascript:" method="POST" target="_self">
    <div class="label">Enter Email Address</div>
    <input
      id="value"
      name="value"
      type="text"
      value={email}
      on:keyup={({ target: { value } }) => validateEmail(value)}
    />

    <button
      class="g-recaptcha"
      data-sitekey={RECAPTCHA_CLIENT_KEY}
      data-callback="onSubmit"
      data-size="invisible"
      id="submitBtn"
      {disabled}
    >
      {#if isLoading}
        <div class="loading" />
      {:else}
        Submit
      {/if}
    </button>
  </form>
</div>

<style>
  .content {
    max-width: 396px;
  }
</style>

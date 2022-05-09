<script>
  import { onMount } from "svelte";
  import * as cryptoUtils from "../common/CryptoUtils";
  import { createAttestationRequestAndSecret } from "../attestation/AttesationUtils";
  import { bigintToHex } from "bigint-conversion";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import {
    getRawEmail,
    getRawOTP,
    getRawPair,
    saveAttestation,
    getRawCurrentAccount,
  } from "../common/AppState";

  import {
    createAndReturnAttestationFromMagicLink,
    hasMagicLink,
  } from "../common/MagicLink";

  let disabled = true;
  let isLoading = false;

  let email;

  const BASE_BACKEND_URL = __myapp.env.BASE_BACKEND_URL;
  const VALIDITY = __myapp.env.VALIDITY;
  const ATTESTOR = __myapp.env.ATTESTOR;

  const submit = async function () {
    disabled = true;

    const requestAndSecret = await createAttestationRequestAndSecret(
      "mail",
      email,
      getRawCurrentAccount()
    );

    console.log(requestAndSecret);
    console.log(requestAndSecret.result.request);

    isLoading = true;

    fetch(BASE_BACKEND_URL + "/api/attestation", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        validity: VALIDITY,
        attestor: ATTESTOR,
        publicRequest: requestAndSecret.result.request,
      }),
    })
      .then(async (response) => {
        isLoading = false;
        if (response.status === 201) {
          let result = await response.json();
          saveAttestation({
            attestation: result.attestation,
            requestSecret: bigintToHex(requestAndSecret.secret),
          });
          flow.saveCurrentStep(flow.transition[$current].nextStep);
        }
      })
      .catch((error) => {
        console.log(error);
        isLoading = false;
      });
  };

  const resend = function () {
    flow.saveCurrentStep(flow.transition[$current].previousStep);
  };

  const OTP_SIZE = 6;
  let codes = new Array(OTP_SIZE);
  let otpCode = "";

  function digestKeyPressed(key) {
    return /\d/.test(key);
  }

  function clearKeyPressed(keyCode) {
    const BACKSPACE = 8;
    const DELETE = 46;
    const ANDROID_BACKSPACE = 229;
    return (
      keyCode === BACKSPACE ||
      keyCode === DELETE ||
      keyCode === ANDROID_BACKSPACE
    );
  }

  function leftKeyPressed(keyCode) {
    return keyCode === 37;
  }

  function rightKeyPressed(keyCode) {
    return keyCode === 39;
  }

  function focusNext(e) {
    const nextvPin = e.target.nextElementSibling;
    if (nextvPin) {
      nextvPin.focus();
    }
  }

  function focusPrevious(e) {
    const prevPin = e.target.previousElementSibling;
    if (prevPin) {
      prevPin.focus();
    }
  }

  const changeHandler = function (e, i) {
    if (clearKeyPressed(e.keyCode)) {
      codes[i] = "";
      focusPrevious(e);
    } else if (leftKeyPressed(e.keyCode)) {
      focusPrevious(e);
    } else if (rightKeyPressed(e.keyCode)) {
      focusNext(e);
    } else if (digestKeyPressed(e.key)) {
      codes[i] = e.key;
      focusNext(e);
    }

    tryToEnableComfirmButton();
  };

  onMount(async () => {
    if (hasMagicLink()) {
      createAndReturnAttestationFromMagicLink();
    } else {
      document.getElementById("code0").focus();
      if (getRawPair()) {
        otpCode = await cryptoUtils.decrypt(
          getRawPair().privateKey,
          getRawOTP()
        );
        console.log(otpCode);
        email = await cryptoUtils.decrypt(
          getRawPair().privateKey,
          getRawEmail()
        );
      } else {
        console.log("key pair missed...");
      }
      document.addEventListener("paste", pasteListener);
    }
  });

  function tryToEnableComfirmButton() {
    if (otpCode && codes.join("") === otpCode) {
      disabled = false;
    } else {
      disabled = true;
    }
  }

  async function pasteListener(e) {
    e.preventDefault();
    const text = await navigator.clipboard.readText();
    if (/\d{6}/.test(text)) {
      codes = text.split("");
      tryToEnableComfirmButton();
    }
  }

  function isMobileChrome() {
    const ua = navigator.userAgent;
    if (/Android/.test(ua)) {
      return true;
    }
    return false;
  }

  $: {
    for (const i in codes) {
      if (!/\d/.test(codes[i])) {
        codes[i] = "";
      }
    }
  }
</script>

{#if hasMagicLink()}
  <div class="title">Creating Attestation</div>
{:else}
  <div class="title">Enter Code</div>

  <div class="content">
    We sent you an attestation code to
    {email}
    <br />
    Copy the code and paste below.
  </div>

  <div class="input-div">
    {#each codes as code, i}
      {#if !isMobileChrome()}
        <input
          type="value"
          autocomplete="off"
          inputmode="numeric"
          bind:value={code}
          id={`code${i}`}
          maxlength="1"
          on:keyup={(event) => changeHandler(event, i)}
          class={i + 1 === OTP_SIZE ? "no-margin" : ""}
        />
      {:else}
        <input
          type="value"
          autocomplete="off"
          inputmode="numeric"
          bind:value={code}
          id={`code${i}`}
          maxlength="1"
          on:input={(event) => {
            codes[i] = event.data;
            if (event.inputType === "insertText") {
              focusNext(event);
            } else if (event.inputType === "deleteContentBackward") {
              focusPrevious(event);
            }
            tryToEnableComfirmButton();
          }}
          class={i + 1 === OTP_SIZE ? "no-margin" : ""}
        />
      {/if}
    {/each}

    <button on:click={submit} {disabled}>
      {#if isLoading}
        <div class="loading" />
      {:else}
        Confirm
      {/if}</button
    >
  </div>

  <div class="note">
    Did not receive the email? Check your spam,
    <br />
    <span on:click={resend} href="" class="resend"> or resend now.</span>
  </div>
{/if}

<style>
  input {
    width: 14%;
    padding: 0;
    margin: 3px 8px 0 0;
    font-size: 20px;
    font-weight: 600;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.7;
    letter-spacing: normal;
    text-align: center;
    color: #000000;
  }

  .no-margin {
    margin-right: 0;
  }

  .note {
    max-width: 375px;
    height: 58px;
    margin: 46px auto;
    font-size: 17px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.71;
    letter-spacing: normal;
    text-align: center;
    color: #2f2f2f;
  }

  .note span {
    font-weight: 600;
    color: #007fed;
  }

  .resend {
    cursor: pointer;
  }
</style>

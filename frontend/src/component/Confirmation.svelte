<script>
  import { onMount } from "svelte";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import {
    auth0AccessToken,
    getEmail,
  } from "../common/AppState";

  import { authHandler, initAuth } from "../common/AuthService";
  import { errorMsgPipe } from "../common/Utils";

  let disabled = true;
  let isLoading = false;

  let email;
  let errorMsg;
  let isVerfiied = false;
  let supportPaste = true;

  const submit = async function () {
    if (window.location.hash) {
      if (!$authHandler) {
        await initAuth();
      }
      $authHandler.parseUrl(window.location.href, async (err, authResult) => {
        if (err) {
          console.log(err);
          errorMsg = err.message || err.errorDescription;
          if (history.replaceState) {
            history.replaceState({}, "", "/");
          }
        } else {
          if (authResult) {
            try {
              isVerfiied = true;
              $auth0AccessToken = authResult.accessToken;

              if (history.replaceState) {
                history.replaceState({}, "", "/");
              }

              flow.saveCurrentStep(flow.STEP_CONNECT_WALLET);
            } catch (error) {
              errorMsg = errorMsgPipe(error.message);
              isLoading = false;
              disabled = false;
            }
          }
        }
      });
    }
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

  const adnroidInputHandler = function (e, i) {
    if (e.inputType === "insertText") {
      if (["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"].includes(e.data)) {
        codes[i] = e.data;
        focusNext(e);
      } else {
        codes[i] = "";
      }
    } else if (e.inputType === "deleteContentBackward") {
      codes[i] = e.data;
      focusPrevious(e);
    }
    tryToEnableComfirmButton();
  };

  onMount(async () => {
    if (!(navigator.clipboard && navigator.clipboard.readText)) {
      supportPaste = false;
    } else {
      supportPaste = true;
    }
    document.getElementById("code0")?.focus();
      
    email = await getEmail();
    document.addEventListener("paste", pasteListener);

    submit();
  });

  const confirm = () => {
    isLoading = true;
    disabled = true;
    $authHandler.login(email, codes.join(""), async (err, res) => {
      console.log("login--", err, res);
      if (err) {
        if (
          err.description.indexOf("expired") > -1 ||
          err.description.indexOf("maximum number") > -1
        ) {
          errorMsg =
            err.description.split(".")[0] + ". Please try to resend again.";
        } else {
          errorMsg = err.description;
        }
        disabled = false;
        isLoading = false;
        otpCode = "";
      }
    });
  };

  async function tryToEnableComfirmButton() {
    if (codes.join("").length === 6) {
      disabled = false;
    } else {
      disabled = true;
    }
  }

  async function pasteListener(e) {
    if (navigator.clipboard && navigator.clipboard.readText) {
      supportPaste = true;
      e.preventDefault();
      const text = (await navigator.clipboard.readText()).trim().slice(0, 6);
      if (/\d{6}/.test(text)) {
        codes = text.split("");
        tryToEnableComfirmButton();
      }
    } else {
      supportPaste = false;
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

{#if !isVerfiied}
  <div class="title">Enter Code</div>

  <div class="content">
    We sent you an attestation code to
    {email}
    <br />
    {#if supportPaste}Copy the code and paste below.
    {:else}Please enter the One-time Passcode.{/if}
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
          on:input={(event) => adnroidInputHandler(event, i)}
          class={i + 1 === OTP_SIZE ? "no-margin" : ""}
        />
      {/if}
    {/each}

    <button on:click={confirm} {disabled}>
      {#if isLoading}
        <div class="loading" />
      {:else}
        Confirm
      {/if}</button
    >
  </div>
  {#if errorMsg}
    <div class="error">{errorMsg}</div>
  {/if}
  <div class="note">
    Did not receive the email? Check your spam,
    <br />
    <span on:click={resend} href="" class="resend"> or resend now.</span>
  </div>
{:else if errorMsg}
  <div class="error margin-top">{errorMsg}</div>
  <div class="note">
    <span on:click={resend} href="" class="resend">resend now.</span>
  </div>
{:else}
  <div class="verifiedLoading" />
  <div class="mt-8 content">
    Please confirm the message in your 'wallet' to complete verification.
  </div>
{/if}

<style>
  .margin-top {
    margin-top: 40px;
  }

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

  .error {
    color: red;
  }

  .resend {
    cursor: pointer;
  }

  .verifiedLoading {
    width: 32px;
    height: 32px;
    background: transparent;
    z-index: 9999;
    border-radius: 50%;
    animation: loadingmove 1.2s linear infinite;
    -webkit-animation: loadingmove 1.2s linear infinite;
    margin-top: 40px;
    border: 3px solid #cccccc;
    border-bottom: 3px solid transparent;
    margin-left: auto;
    margin-right: auto;
  }

  .content {
    max-width: 396px;
  }
</style>

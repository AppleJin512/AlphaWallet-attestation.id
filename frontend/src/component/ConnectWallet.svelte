<script>
  import * as walletService from "../common/WalletService";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import {
    auth0AccessToken,
    currentWallet,
    getRawEmail,
    getRawPair,
    saveAttestation,
  } from "../common/AppState";
  import { createAttestationRequestAndSecret } from "../attestation/AttesationUtils";
  import * as cryptoUtils from "../common/CryptoUtils";
  import { bigintToHex } from "bigint-conversion";

  const BASE_BACKEND_URL = __myapp.env.BASE_BACKEND_URL;
  const VALIDITY = __myapp.env.VALIDITY;
  const ATTESTOR = __myapp.env.ATTESTOR;

  let isLoading = false;
  let canTry = false;

  $: if ($auth0AccessToken) {
    if ($currentWallet) {
      gotoSign();
    } else {
      walletService.connect();
    }
  }

  const gotoSign = async () => {
    try {
      if (!getRawPair()) {
        return;
      }

      const email = await cryptoUtils.decrypt(
        getRawPair().privateKey,
        getRawEmail()
      );

      isLoading = true;
      const requestAndSecret = await createAttestationRequestAndSecret(
        "mail",
        email,
        $currentWallet
      );
      if (requestAndSecret.result.request) {
        try {
          fetch(BASE_BACKEND_URL + "/api/attestation", {
            method: "POST",
            headers: {
              Accept: "application/json",
              "Content-Type": "application/json",
              "x-pap-ac": $auth0AccessToken,
              "x-pap-id-provider": "auth0",
            },
            body: JSON.stringify({
              validity: VALIDITY,
              attestor: ATTESTOR,
              publicRequest: requestAndSecret.result.request,
            }),
          })
            .then(async (response) => {
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
              isLoading = false;
              console.error(error);
            });
        } catch (error) {
          isLoading = false;
          canTry = true;
          canTry = true;
          console.error(error);
        }
      }
    } catch (error) {
      console.error(error);
      canTry = true;
      isLoading = false;
    }
  };
</script>

{#if isLoading}
  <div class="verifiedLoading" />
  <div class="content">
    Please confirm the message in your 'wallet' to complete verification.
  </div>
{:else if canTry}
  <div class="retry-desc">Something wrong, please try again.</div>
  <button on:click={gotoSign} class="retry">Try again</button>
{/if}

<style>
  .retry {
    width: 128px;
  }
  .retry-desc {
    text-align: center;
    margin-top: 32px;
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
</style>

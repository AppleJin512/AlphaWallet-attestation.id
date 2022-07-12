<script>
	import * as walletService from "../common/WalletService";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import {
    attestationDb,
    auth0AccessToken,
    currentWallet,
    currentEmail,
    type,
    getEmail,
    getType,
    providerName,
    keccak256,
    testValidity,
  } from "../common/AppState";
  import { createAttestationRequestAndSecret } from "../attestation/AttesationUtils";
  import { bigintToHex } from "bigint-conversion";
  import { onMount } from "svelte";

  const BASE_BACKEND_URL = __myapp.env.BASE_BACKEND_URL;
  const ATTESTOR = __myapp.env.ATTESTOR;

  let isLoading = false;
  let canTry = false;
  let showBtn = false;

  $: if ($auth0AccessToken) {
    if ($currentWallet) {
      gotoSign();
    } else {
      walletService.connect($providerName);
    }
  }

  const gotoSign = async () => {
    try {
      $currentEmail = await getEmail();
      $type = getType();
      
      if (!$currentEmail || !$type) {
        return;
      }

      isLoading = true;
      const requestAndSecret = await createAttestationRequestAndSecret(
        "mail",
        $currentEmail,
        $currentWallet,
        $providerName
      );
      if (requestAndSecret.result.request) {
        try {
          if ($type === "email") {
            fetch(BASE_BACKEND_URL + "/api/attestation", {
              method: "POST",
              headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
                "x-pap-ac": $auth0AccessToken,
                "x-pap-id-provider": "auth0",
              },
              body: JSON.stringify({
                validity: getValidity(),
                attestor: ATTESTOR,
                publicRequest: requestAndSecret.result.request,
              }),
            })
              .then(async (response) => {
                if (response.status === 201) {
                  let result = await response.json();
                  await attestationDb.insertAttestation($type, keccak256($currentEmail.toLowerCase()), keccak256($currentWallet.toLowerCase()), {
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
          } else if ($type === "twitter") {
            await getPublicAttestation(BASE_BACKEND_URL + "/api/attestation/public", {
              method: "POST",
              headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
                "x-pap-ac": $auth0AccessToken,
                "x-pap-id-provider": "auth0",
              },
              body: JSON.stringify({
                id: "id",
                message: "message",
                signature: "signature",
                ethAddress: "ethAdress",
                identifier: "identifier",
              }),
            })
              .then(async (response) => {
                if (response.status === 201) {
                  console.log("getting a public attestation");
                  flow.saveCurrentStep(flow.transition[$current].nextStep);
                }
              })
              .catch((error) => {
                isLoading = false;
                console.error(error);
              });
          }
        } catch (error) {
          isLoading = false;
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

  // This function is only used to test the second API (attestation/public).
  async function getPublicAttestation(url, withParameter) {
    return {
      status: 201,
    };
  }

  function getValidity() {
    return $testValidity > 0 ? Math.min( $testValidity, __myapp.env.VALIDITY ) : __myapp.env.VALIDITY;    
  }

  function connectWalletClick() {
    walletService.connect($providerName);
  }

  onMount(async () => {
    setTimeout(() => {
      showBtn = true;
    }, 2000);
  });
</script>

{#if isLoading}
  <div class="verifiedLoading" />
  <div class="content">
    Please confirm the message in your 'wallet' to complete verification.
  </div>
{:else if canTry}
  <div class="retry-desc">Something wrong, please try again.</div>
  <button on:click={gotoSign} class="retry">Try again</button>
{:else if showBtn}
  <button class="connect" on:click={connectWalletClick}
    >Connect to Wallet</button
  >
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

  .connect {
    width: 40%;
    margin-top: calc((100vh - 400px) / 2);
  }
</style>

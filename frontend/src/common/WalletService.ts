import WalletConnectProvider from "@walletconnect/web3-provider";
import { ethers } from "ethers";
import { _TypedDataEncoder } from "ethers/lib/utils";
import { keccak256 } from "js-sha3";
import Web3Modal from "web3modal";

import * as flow from "../common/Flow";
import { currentWallet, saveCurrentAccount } from "./AppState";
import { current, saveCurrentStep } from "./Flow";

let provider: ethers.providers.Web3Provider;

const ATTESTOR_DOMAIN = "http://wwww.attestation.id";

const providerOptions = {
  walletconnect: {
    package: WalletConnectProvider,
    options: {
      infuraId: "795587fc9545486b8a5e190a44e3ae7d",
    },
  },
};

const web3Modal = new Web3Modal({
  network: "mainnet",
  cacheProvider: true,
  providerOptions,
});

if (!window.location.href.includes("access_token")) {
  saveCurrentStep(flow.STEP_ENTER_EMAIL);
  web3Modal.clearCachedProvider();
  localStorage.removeItem("walletconnect");
} else {
  saveCurrentStep(flow.STEP_CONFIRMATION);
}

export async function isEnabled() {
  return web3Modal.cachedProvider;
}

export async function connect() {
  try {
    const web3ModalProvider = await web3Modal.connect();
    provider = new ethers.providers.Web3Provider(web3ModalProvider);
    registerEthListener(web3ModalProvider);
    updateCurrentStatus(await provider.listAccounts());
  } catch (err) {
    console.log(err);
  }
}

export async function signatureAndPublicKey(userData) {
  if (!provider) {
    const web3ModalProvider = await web3Modal.connect();
    provider = new ethers.providers.Web3Provider(web3ModalProvider);
  }

  const domain = {
    name: ATTESTOR_DOMAIN,
    version: "0.1",
  };

  const primaryTypes = {
    AttestationRequest: [
      { name: "payload", type: "string" },
      { name: "description", type: "string" },
      { name: "timestamp", type: "string" },
      { name: "identifier", type: "string" },
    ],
  };

  const types = {
    EIP712Domain: [
      { name: "name", type: "string" },
      { name: "version", type: "string" },
    ],
    ...primaryTypes,
  };

  let userDataValuesWithHashedPayload = Object.assign({}, userData);
  userDataValuesWithHashedPayload.payload = keccak256(
    userDataValuesWithHashedPayload.payload
  );

  const signature = await provider
    .getSigner()
    ._signTypedData(domain, primaryTypes, userDataValuesWithHashedPayload);

  const digest = ethers.utils.arrayify(
    _TypedDataEncoder.hash(
      domain,
      primaryTypes,
      userDataValuesWithHashedPayload
    )
  );

  const publicKey = ethers.utils.recoverPublicKey(digest, signature);
  console.log(ethers.utils.computeAddress(publicKey));
  console.log(
    ethers.utils.verifyTypedData(
      domain,
      primaryTypes,
      userDataValuesWithHashedPayload,
      signature
    )
  );

  let completeData = {
    types,
    primaryType: "AttestationRequest",
    message: userData,
    domain,
  };

  const externalAuthenticationData = {
    signatureInHex: signature,
    jsonSigned: JSON.stringify(completeData),
  };

  return { request: JSON.stringify(externalAuthenticationData), publicKey };
}

function updateCurrentStatus(accounts) {
  if (accounts.length === 0) {
    console.log("no account");
  } else {
    currentWallet.set(accounts[0]);
    saveCurrentAccount(accounts[0]);
  }
}

function registerEthListener(web3ModalProvider) {
  web3ModalProvider
    .on("disconnect", (error) => {
      reset();
      // From metamask document:
      // ********************************
      // Once disconnect has been emitted, the provider will not accept any new requests
      // until the connection to the chain has been re-restablished,
      // which requires reloading the page.
      // ********************************
      // https://docs.metamask.io/guide/ethereum-provider.html#disconnect
      location.reload();
    })
    .on("accountsChanged", (accounts) => {
      if (accounts.length === 0) {
        console.log("Connection disconnected, go to the first step.");
        reset();
      } else {
        saveCurrentAccount(accounts[0]);
      }
    })
    .on("chainChanged", (chainId) => {
      console.log("chainChanged", chainId);
    });
}

function reset() {
  current.set(flow.start);
  web3Modal.clearCachedProvider();
  localStorage.removeItem("walletconnect");
}

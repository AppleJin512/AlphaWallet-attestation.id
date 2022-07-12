import * as flow from "../common/Flow";
import { currentWallet } from "./AppState";
import { current, saveCurrentStep } from "./Flow";

declare let window: any;

const Web3Modal = window.Web3Modal.default;
const ethers = window.ethers;
const keccak256 = window.keccak256;
const WalletConnectProvider = window.WalletConnectProvider.default;
const _TypedDataEncoder = ethers.utils._TypedDataEncoder;

let provider: any;

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
  reset();
} else {
  saveCurrentStep(flow.STEP_CONFIRMATION);
}

export async function isEnabled() {
  return web3Modal.cachedProvider;
}

export async function connect(providerName: string) {
  try {
    const web3ModalProvider = providerName
      ? await web3Modal.connectTo(providerName)
      : await web3Modal.connect();

    if (web3ModalProvider && web3ModalProvider._blockTracker) {
      web3ModalProvider._blockTracker._pollingInterval = 60000;
      web3ModalProvider._blockTracker._retryTimeout = 60000;
    }

    provider = new ethers.providers.Web3Provider(web3ModalProvider);
    registerEthListener(web3ModalProvider);
    updateCurrentStatus(await provider.listAccounts());
  } catch (err) {
    console.log(err);
  }
}

export async function signatureAndPublicKey(userData, providerName) {
  if (!provider) {
    const web3ModalProvider = providerName
      ? await web3Modal.connectTo(providerName)
      : await web3Modal.connect();
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
        //saveCurrentAccount(accounts[0]);
        currentWallet.set(accounts[0]);
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

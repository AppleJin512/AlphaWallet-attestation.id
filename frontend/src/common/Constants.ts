export const conf = {
  AUTH0: {
    domain: import.meta.env.VITE_AUTH0_DOMAIN,
    clientID: import.meta.env.VITE_AUTH0_CLIENTID,
    redirectUri: window.location.origin,
    responseType: "token id_token",
  },
};

export const BASE_BACKEND_URL =
  import.meta.env.VITE_BASE_BACKEND_URL ||
  "https://backend-stage.attestation.id";
export const ATTESTOR = import.meta.env.VITE_ATTESTOR || "AlphaWallet";

export const conf = {
  AUTH0: {
    domain: import.meta.env.VITE_AUTH0_DOMAIN,
    clientID: import.meta.env.VITE_AUTH0_CLIENTID,
    redirectUri: window.location.origin,
    responseType: "token id_token",
  },
};

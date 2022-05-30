export const conf = {
  AUTH0: {
    domain: __myapp.env.AUTH0_DOMAIN,
    clientID: __myapp.env.AUTH0_CLIENTID,
    redirectUri: window.location.origin,
    responseType: "token id_token",
  },
};

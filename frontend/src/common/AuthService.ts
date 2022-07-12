import auth0 from "auth0-js";
import { writable } from "svelte/store";
import { conf } from "./Constants";

export const authHandler = writable<any>(null);

export async function initAuth() {
  const auth0Js = new auth0.WebAuth(conf.AUTH0);
  authHandler.set({
    sendemail: async (email: String, sendHandler) => {
      auth0Js.passwordlessStart(
        {
          connection: "email",
          send: "code",
          email: email,
        },
        function (err, res) {
          sendHandler(err, res);
        }
      );
    },
    login: async (email, otp, loginHandler) => {
      console.log("passwordlessLogin", email, otp);

      auth0Js.passwordlessLogin(
        {
          connection: "email",
          email: email,
          verificationCode: otp,
        },
        function (err, res) {
          loginHandler(err, res);
        }
      );
    },
    parseUrl: (href, parseHandler) => {
      const access_token = href.match(/\#(?:access_token)\=([\S\s]*?)\&/)[1];
      parseHandler(null, { accessToken: access_token });
    },
  });
}

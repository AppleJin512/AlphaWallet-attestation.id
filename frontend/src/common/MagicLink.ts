import { createAttestationRquestAndSecret } from "../attestation/AttesationUtils";
import { getRawCurrentAccount, saveAttestation } from "./AppState";
import { bigintToHex } from "bigint-conversion";

let magicLinkEmail = "";
let magicLink = "";

export function setMagicLinkData(email, link) {
  magicLink = link;
  magicLinkEmail = email;
}

export function hasMagicLink() {
  return magicLink != "";
}

function reply(message) {
  parent.postMessage(message, "*");
}

export async function createAndReturnAttestationFromMagicLink() {

  let requestAndSecret: any;
  try {
    requestAndSecret = await createAttestationRquestAndSecret(
      "mail",
      magicLinkEmail,
      getRawCurrentAccount()
    );
  } catch (e) {
    console.log(e);
    reply({
      error: e.message,
      display: false
    });
    return;
  }

  fetch(__myapp.env.BASE_BACKEND_URL + "/api/attestation/magic-link", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      validity: __myapp.env.VALIDITY,
      attestor: __myapp.env.ATTESTOR,
      magicLink: magicLink,
      publicRequest: requestAndSecret.result.request,
    }),
  })
    .then(async (response) => {
      if (response.status === 201) {
        let result = await response.json();
        console.log('attestation received. result:');
        console.log(result);
        console.log(result.attestation);
        saveAttestation({
          attestation: result.attestation,
          requestSecret: bigintToHex(requestAndSecret.secret),
        });

        reply({
          attestation: result.attestation,
          requestSecret: requestAndSecret.secret,
          display: false,
        });
      }
    })
    .catch((error) => {
      console.log(error);
      reply({
        error,
        display: false,
      });
    });
}

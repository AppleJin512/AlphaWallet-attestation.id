import { ab2str, base64toBase64Url, str2ab } from "../common/Utils";
import { signatureAndPublicKey } from "../common/WalletService";

import { AttestationCrypto } from "@tokenscript/attestation";
import { Nonce } from "@tokenscript/attestation/dist/libs/Nonce";
import { AsnParser } from "@tokenscript/attestation/dist/libs/AsnSchemaUtil";
import { AttestationRequest } from "@tokenscript/attestation/dist/libs/AttestationRequest";
import { MyAttestationDecoded } from "@tokenscript/attestation/dist/asn1/shemas/AttestationFramework";

// TODO: Use "Attestation.ID" instead of domain? Domain shows underneath this in metamask request,
//  which could cause confusion to users. Needs to be changed in ApiController.java at the same time.
const ATTESTOR_DOMAIN = "http://wwww.attestation.id";
const Eip712UserDataDescription = "Creating email attestation";

const attestationCrypto = new AttestationCrypto();

export async function createAttestationRequestAndSecret(
  type: string,
  identifier: string,
  address: string
) {
  const secret = attestationCrypto.makeSecret();

  const fpoe = attestationCrypto.computeAttestationProof(
      secret,
      await Nonce.makeNonce(
          address,
          ATTESTOR_DOMAIN,
          new Uint8Array(),
          new Date().getTime()
      )
  );

  const attRequest = AttestationRequest.fromData(type === "mail" ? 1 : 0, fpoe);

  const userData = {
    payload: base64toBase64Url(btoa(ab2str(attRequest.getAsnEncoded()))),
    description: Eip712UserDataDescription,
    timestamp: currentTimestampString(),
    identifier,
  };

  const result = await signatureAndPublicKey(userData);
  return { result, secret };
}

function currentTimestampString() {
  const timestamp = new Date();
  // return example: Sat Jan 29 2022 12:55:37 GMT+0800
  return timestamp.toString().replace(/\s*\(.+\)/, "");
}

export function parseAttestation(attestation: string) {
  return AsnParser.parse(str2ab(atob(attestation)), MyAttestationDecoded);
}

export function expired(attestation) {
  return (
    attestation.signedInfo.validity.notAfter.generalizedTime.getTime() <
    new Date().getTime()
  );
}

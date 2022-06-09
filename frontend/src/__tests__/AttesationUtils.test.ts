import { AsnParser } from "@peculiar/asn1-schema";
import {
  createAttestationRequestAndSecret,
  parseAttestation,
} from "../attestation/AttesationUtils";
import { str2ab } from "../common/Utils";
import { Identifier } from "@tokenscript/attestation/dist/asn1/shemas/AttestationRequest";

xtest("createAttestationRquestAndSecret should work", async () => {
  const result = await createAttestationRequestAndSecret(
    "mail",
    "test@mail.com",
    "0x23f1cdF5200dxae7d07c1522f19242A722D93981",
    undefined
  );
  expect(result.result).toBeDefined();
  expect(result.secret).toBeDefined();

  const request = AsnParser.parse(
    str2ab(atob(result.result.request)),
    Identifier
  );

  expect(request.type).toBe(1);
  expect(request.proof).toBe("test@mail.com");
});

test("parseAttestation should work", () => {
  const attestation = parseAttestation(
    "MIICdjCCAh2gAwIBEgIIKHjhXUYRDf0wCQYHKoZIzj0CATAWMRQwEgYDVQQDDAtBbHBoYVdhbGxldDAiGA8yMDIxMDIxODA5NDE0OFoYDzIwMjEwMjE4MTA0MTQ4WjA1MTMwMQYDVQQDDCoweDRFNzE0MDBBMjMyNzk3MzY5RDE4MEFEQzZFRUQyODI3RkFBNzMwRkMwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAEBDGo7b2LIMb7VT7z411kfdlAU8nrFwxf4lp0zS/gZJKpgriOIDTPVjMQQgFGuIt51KKagsRZ0G1l27/LD2fbZaNXMFUwUwYLKwYBBAGLOnN5ASgBAf8EQQQnbFwGLvu2OaR+dM/WIftstzeVpW8Qx96d60iidINGaQIzUn5pFk7u1KpLT0FIgGVLbsVeIN+Pid5A1LNTtx9tMAkGByqGSM49AgEDSAAwRQIhAOeHois2gAhBsuoWaOPyBN86p8ifF68SxTEAgR+wvD4IAiAIzjcSy+t9+/E0vTSxWGXNcZlBqUTtLPjA/9O505TNCA=="
  );
  const notAfter =
    attestation.signedInfo.validity.notAfter.generalizedTime.getTime();
  const notBefore =
    attestation.signedInfo.validity.notBefore.generalizedTime.getTime();
  expect(notBefore).toBeDefined();
  expect(notAfter).toBeDefined();
  expect(notAfter - notBefore).toBe(3600 * 1000);
});

import { AsnParser, AsnSerializer } from "@peculiar/asn1-schema";
import * as bigintConversion from "bigint-conversion";
import { keccak256 } from "js-sha3";

import { ab2str, base64toBase64Url, str2ab } from "../common/Utils";
import { signatureAndPublicKey } from "../common/WalletService";
import { Identity, MyAttestation, Proof } from "./ASN1Schemas";
import { CURVE_BN256, mod, Point } from "./Point";

declare let window: any;

const curveOrderBitLength = 254;
const ATTESTOR_DOMAIN = "http://wwww.attestation.id";
const Eip712UserDataDescription = "Linking Ethereum address to phone or email";

export const G = new Point(
  21282764439311451829394129092047993080259557426320933158672611067687630484067n,
  3813889942691430704369624600187664845713336792511424430006907067499686345744n,
  CURVE_BN256
);

export const H = new Point(
  10844896013696871595893151490650636250667003995871483372134187278207473369077n,
  9393217696329481319187854592386054938412168121447413803797200472841959383227n,
  CURVE_BN256
);

export class AttestationCrypto {
  constructor() {}

  makeSecret() {
    return mod(
      bigintConversion.bufToBigint(
        window.crypto.getRandomValues(new Uint8Array(48))
      ),
      CURVE_BN256.n
    );
  }

  makeNonce(
    address: string,
    receiverIdentifier: string,
    timestampInMs: number
  ) {
    const tsBytes = this.long2Bytes(timestampInMs);
    const addressBytes = new Uint8Array(
      // bigintConversion.textToBuf(`0x${address.slice(2).toUpperCase()}`)
      bigintConversion.textToBuf(address.toUpperCase())
    );
    const receiverIdentifierBytes = new Uint8Array(
      bigintConversion.hexToBuf(keccak256(receiverIdentifier))
    );
    const otherDataBytes = new Uint8Array(0);

    return new Uint8Array([
      ...addressBytes,
      ...receiverIdentifierBytes,
      ...tsBytes,
      ...otherDataBytes,
    ]);
  }

  computeAttestationProof(randomness: bigint, nonce: ArrayBuffer) {
    const riddle = H.multiplyDA(randomness);
    return this.constructSchnorrPOK(riddle, randomness, [H, riddle], nonce);
  }

  constructSchnorrPOK(
    riddle: Point,
    randomness: bigint,
    challengeList: Point[],
    nonce: ArrayBuffer
  ) {
    let t: Point;
    let c: bigint;
    let d: bigint;
    let hiding: bigint;

    do {
      hiding = this.makeSecret();
      t = H.multiplyDA(hiding);
      c = this.mapToInteger(
        new Uint8Array([
          ...this.makeArray([...challengeList, t]),
          ...new Uint8Array(nonce),
        ])
      );
    } while (c >= CURVE_BN256.n);
    d = mod(hiding + c * randomness, CURVE_BN256.n);

    return new Proof({
      riddle: riddle.getEncoded(false),
      responseValue: t.getEncoded(false),
      challengePoint: bigintConversion.bigintToBuf(d),
      nonce,
    });
  }

  mapToInteger(value: Uint8Array) {
    const hasher = keccak256.create();
    hasher.update(value);
    const hash = hasher.digest();
    return (
      bigintConversion.bufToBigint(new Uint8Array(hash)) >>
      BigInt(256 - curveOrderBitLength)
    );
  }

  makeArray(points: Point[]) {
    return new Uint8Array(
      bigintConversion.hexToBuf(
        points
          .map((point) => bigintConversion.bufToHex(point.getEncoded(false)))
          .join("")
      )
    );
  }

  long2Bytes(value: number) {
    let tsBytes = new Uint8Array(8);
    tsBytes.set(
      new Uint8Array(bigintConversion.hexToBuf(value.toString(16))).reverse()
    );
    tsBytes = tsBytes.reverse();
    return tsBytes;
  }
}

const attestationCrypto = new AttestationCrypto();

export async function createAttestationRquestAndSecret(
  type: string,
  identifier: string,
  address: string
) {
  const secret = attestationCrypto.makeSecret();

  const identity = new Identity({
    type: type === "mail" ? 1 : 0,
    proof: attestationCrypto.computeAttestationProof(
      secret,
      attestationCrypto.makeNonce(
        address,
        ATTESTOR_DOMAIN,
        new Date().getTime()
      )
    ),
  });

  const userData = {
    payload: base64toBase64Url(btoa(ab2str(AsnSerializer.serialize(identity)))),
    description: Eip712UserDataDescription,
    timestamp: currentTimestampString(),
    identifier,
  };

  const result = await signatureAndPublicKey(userData);
  return { result, secret };
}

function currentTimestampString() {
  const timestamp = new Date();
  return timestamp.toUTCString();
}

export function parseAttestation(attestation: string) {
  return AsnParser.parse(str2ab(atob(attestation)), MyAttestation);
}

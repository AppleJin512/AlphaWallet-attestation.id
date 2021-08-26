import {
  AsnArray,
  AsnProp,
  AsnPropTypes,
  AsnType,
  AsnTypeTypes,
} from "@peculiar/asn1-schema";

export class Proof {
  @AsnProp({ type: AsnPropTypes.OctetString })
  riddle: ArrayBuffer;

  @AsnProp({ type: AsnPropTypes.OctetString })
  challengePoint: ArrayBuffer;

  @AsnProp({ type: AsnPropTypes.OctetString })
  responseValue: ArrayBuffer;

  @AsnProp({ type: AsnPropTypes.OctetString })
  nonce: ArrayBuffer;

  constructor(params: Partial<Proof> = {}) {
    Object.assign(this, params);
  }
}

export class AlgorithmIdentifier {
  @AsnProp({ type: AsnPropTypes.ObjectIdentifier })
  algorithm: string;

  @AsnProp({ type: AsnPropTypes.Any, optional: true })
  parameters?: ArrayBuffer;

  constructor(params: Partial<AlgorithmIdentifier> = {}) {
    Object.assign(this, params);
  }
}

export class SubjectPublicKeyInfoValue {
  @AsnProp({ type: AlgorithmIdentifier })
  algorithm: AlgorithmIdentifier;

  @AsnProp({ type: AsnPropTypes.BitString })
  subjectPublicKey: ArrayBuffer;

  constructor(params: Partial<SubjectPublicKeyInfoValue> = {}) {
    Object.assign(this, params);
  }
}

@AsnType({ type: AsnTypeTypes.Choice })
export class SubjectPublicKeyInfo {
  @AsnProp({ type: SubjectPublicKeyInfoValue })
  value?: SubjectPublicKeyInfoValue;

  @AsnProp({ type: AsnPropTypes.Null })
  null?: any;

  constructor(params: Partial<SubjectPublicKeyInfo> = {}) {
    Object.assign(this, params);
  }
}

export class Identity {
  @AsnProp({ type: AsnPropTypes.Integer })
  type: number;

  @AsnProp({ type: Proof })
  proof: Proof;

  constructor(params: Partial<Identity> = {}) {
    Object.assign(this, params);
  }
}

class AttributeTypeAndValue {
  @AsnProp({ type: AsnPropTypes.ObjectIdentifier })
  type: string;

  @AsnProp({ type: AsnPropTypes.Any })
  value: ArrayBuffer;
}

@AsnType({ type: AsnTypeTypes.Set, itemType: AttributeTypeAndValue })
class RelativeDistinguishedName extends AsnArray<AttributeTypeAndValue> {
  constructor(items?: AttributeTypeAndValue[]) {
    super(items);
    Object.setPrototypeOf(this, RelativeDistinguishedName.prototype);
  }
}

@AsnType({ type: AsnTypeTypes.Sequence, itemType: RelativeDistinguishedName })
class RDNSequence extends AsnArray<RelativeDistinguishedName> {
  constructor(items?: RelativeDistinguishedName[]) {
    super(items);
    Object.setPrototypeOf(this, RDNSequence.prototype);
  }
}

@AsnType({ type: AsnTypeTypes.Choice })
class Name {
  @AsnProp({ type: RDNSequence })
  rdnSequence?: RDNSequence;

  @AsnProp({ type: AsnPropTypes.Null })
  null?: any;
}

@AsnType({ type: AsnTypeTypes.Choice })
class Time {
  @AsnProp({ type: AsnPropTypes.UTCTime })
  utcTime?: Date;

  @AsnProp({ type: AsnPropTypes.GeneralizedTime })
  generalizedTime?: Date;
}

export class ValidityValue {
  @AsnProp({ type: Time })
  notBefore: Time;

  @AsnProp({ type: Time })
  notAfter: Time;
}

@AsnType({ type: AsnTypeTypes.Choice })
class Validity {
  @AsnProp({ type: ValidityValue })
  value?: ValidityValue;

  @AsnProp({ type: AsnPropTypes.Null })
  null?: any;
}

export class Extension {
  @AsnProp({ type: AsnPropTypes.ObjectIdentifier })
  extnId: string;

  @AsnProp({ type: AsnPropTypes.Boolean })
  critical: boolean;

  @AsnProp({ type: AsnPropTypes.OctetString })
  extnValue: ArrayBuffer;
}

@AsnType({ type: AsnTypeTypes.Sequence, itemType: Extension })
class Extensions extends AsnArray<Extension> {
  constructor(items?: Extension[]) {
    super(items);
    Object.setPrototypeOf(this, Extensions.prototype);
  }
}

@AsnType({ type: AsnTypeTypes.Choice })
export class AttestsTo {
  @AsnProp({ type: Extensions, context: 3 })
  extensions?: Extensions;

  @AsnProp({ type: AsnPropTypes.Any, context: 4 })
  dataObject?: ArrayBuffer;
}

@AsnType({ type: AsnTypeTypes.Sequence, itemType: AsnPropTypes.Integer })
export class SmartContract extends AsnArray<AsnPropTypes.Integer> {
  constructor(items?: AsnPropTypes.Integer[]) {
    super(items);
    Object.setPrototypeOf(this, SmartContract.prototype);
  }
}

export class AttestationSignedInfo {
  @AsnProp({ type: AsnPropTypes.Integer, context: 0 })
  version: number;

  @AsnProp({ type: AsnPropTypes.Integer })
  serialNumber: number;

  @AsnProp({ type: AlgorithmIdentifier })
  signature: AlgorithmIdentifier;

  @AsnProp({ type: Name })
  issuer: Name;

  @AsnProp({ type: Validity })
  validity: Validity;

  @AsnProp({ type: Name })
  subject: Name;

  @AsnProp({ type: SubjectPublicKeyInfo })
  subjectPublicKeyInfo: SubjectPublicKeyInfo;

  @AsnProp({ type: SmartContract, optional: true })
  contract?: SmartContract;

  @AsnProp({ type: AttestsTo })
  attestsTo: AttestsTo;
}

export class MyAttestation {
  @AsnProp({ type: AttestationSignedInfo })
  signedInfo: AttestationSignedInfo;

  @AsnProp({ type: AlgorithmIdentifier })
  signatureAlgorithm: AlgorithmIdentifier;

  @AsnProp({ type: AsnPropTypes.BitString })
  signatureValue: ArrayBuffer;

  constructor(params: Partial<MyAttestation> = {}) {
    Object.assign(this, params);
  }
}

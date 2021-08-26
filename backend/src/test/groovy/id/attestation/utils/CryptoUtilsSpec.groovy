package id.attestation.utils

import spock.lang.Specification

class CryptoUtilsSpec extends Specification {

    void "should encrypt a data with rsa oaep public key"() {
        expect:
        TestUtils.decryptWithRSAOAEP(TestUtils.privateKey, CryptoUtils.encryptWithRSAOAEP(TestUtils.publicKey, 'hello')) == 'hello'
    }

    void "should generate 6-character otp"() {
        expect:
        10000.times {
            assert CryptoUtils.generateOtp().length() == 6
        }
    }

    void "should get ECPublicKeyParameters from EC public key hex"() {
        expect:
        CryptoUtils.getECPublicKeyParameters(TestUtils.ecPubkeyHex) != null
    }
}

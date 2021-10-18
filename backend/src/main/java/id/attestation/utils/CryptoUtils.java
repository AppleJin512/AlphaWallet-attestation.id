package id.attestation.utils;

import org.tokenscript.attestation.IdentifierAttestation;
import org.tokenscript.attestation.SignedIdentifierAttestation;
import org.tokenscript.attestation.core.ASNEncodable;
import org.tokenscript.attestation.core.AttestationCrypto;
import org.tokenscript.attestation.core.SignatureUtility;
import org.tokenscript.attestation.eip712.Eip712AttestationRequest;
import org.tokenscript.attestation.ERC721Token;
import id.attestation.data.AttestationWebResponse;
import id.attestation.data.PublicAttestationWebResponse;
import id.attestation.exception.EncryptOtpFailedException;
import id.attestation.exception.IllegalAttestationRequestException;
import id.attestation.exception.PublicKeyCreationException;
import io.alchemynft.attestation.NFTAttestation;
import io.alchemynft.attestation.SignedNFTAttestation;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittertip.CoSignedIdentifierAttestation;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Clock;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

public class CryptoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtils.class);

    public static void generateKeys() {
        AsymmetricCipherKeyPair keys = SignatureUtility.constructECKeysWithSmallestY(new SecureRandom());
        try {
            System.out.println("---- Public Key  ----");
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(keys.getPublic());
            System.out.println(Base64.getEncoder().encodeToString(spki.getEncoded()));
            System.out.println("---- Public Key  ----");

            System.out.println("---- Private Key ----");
            PrivateKeyInfo privInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(keys.getPrivate());
            System.out.println(Base64.getEncoder().encodeToString(privInfo.getEncoded()));
            System.out.println("---- Private Key ----");
        } catch (IOException e) {
            System.err.printf("Key generation is failed, caused by: %s%n", e.getMessage());
            LOGGER.error("Key generation is failed, caused by:", e);
        }
    }

    // for encrypting data with public key, web crypto api only supports `ras-oaep`:
    // https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/encrypt
    public static String encryptWithRSAOAEP(String publicKey, String data) {
        try {
            // refer to:
            // https://stackoverflow.com/questions/55525628/rsa-encryption-with-oaep-between-java-and-javascript
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1"
                    , new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey), oaepParams);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (PublicKeyCreationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Can't encrypt otp({}) with key {}, caused by: {}", data, publicKey, e);
            throw new EncryptOtpFailedException("Cannot generate an encrypted otp.");
        }
    }

    private static PublicKey getPublicKey(String publicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey result = keyFactory.generatePublic(keySpec);
            return result;
        } catch (Exception e) {
            LOGGER.error("Can't create a public key from {}, caused by: {}", publicKey, e);
            throw new PublicKeyCreationException("Cannot create a public key for encrypting otp.");
        }
    }

    public static String generateOtp() {
        final int bound = 1000000;
        final int fixedLength = 6;
        SecureRandom secureRandom = new java.security.SecureRandom();
        int randInt = secureRandom.nextInt(bound);
        String result = String.valueOf(randInt);
        if (result.length() < fixedLength) {
            randInt = randInt == 0 ? 1 : randInt;
            // ensure returned otp is a 6-character string
            return String.valueOf(bound - randInt);
        } else {
            return result;
        }
    }

    public static AttestationWebResponse constructAttest(long validity, String attestor
            , Eip712AttestationRequest attestationRequest
            , AsymmetricCipherKeyPair keys) {
        try {
            byte[] commitment = AttestationCrypto.makeCommitment(attestationRequest.getIdentifier(),
                    attestationRequest.getType(), attestationRequest.getPok().getRiddle());
            IdentifierAttestation att = new IdentifierAttestation(commitment, attestationRequest.getUserPublicKey());
            att.setIssuer("CN=" + attestor);
            att.setSerialNumber(new Random().nextLong());
            Date now = new Date();
            att.setNotValidBefore(now);
            att.setNotValidAfter(new Date(Clock.systemUTC().millis() + validity * 1000));
            SignedIdentifierAttestation signed = new SignedIdentifierAttestation(att, keys);
            return new AttestationWebResponse(derString(signed), encodePublicKey(keys.getPublic()));
        } catch (IllegalArgumentException e) {
            throw new IllegalAttestationRequestException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Something wrong", e);
        }
    }

    public static PublicAttestationWebResponse constructPublicAttest(long id, String identifier
            , String message, String signature, String ethAddress
            , AsymmetricCipherKeyPair attestorKeys) {
        try {
            AsymmetricKeyParameter publicKey = getECCPublicKey(message, signature, ethAddress);
            IdentifierAttestation att = new IdentifierAttestation(String.valueOf(id), identifier, publicKey);
            SignedIdentifierAttestation signed = new SignedIdentifierAttestation(att, attestorKeys);
            return new PublicAttestationWebResponse(derString(signed), encodePublicKey(attestorKeys.getPublic()), encodePublicKey(publicKey));
        } catch (SignatureException e) {
            LOGGER.error("Can't create a public key with ({}, {}, {}), caused by: {}", id, message, signature, e);
            throw new PublicKeyCreationException("Invalid signature");
        } catch (IllegalArgumentException e) {
            throw new IllegalAttestationRequestException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Something wrong", e);
        }
    }

    public static AttestationWebResponse constructNftAttest(AsymmetricKeyParameter pk, String publicAttestation, ERC721Token[] tokens) {
        try {
            SignedIdentifierAttestation att = new SignedIdentifierAttestation(Base64.getDecoder().decode(publicAttestation.getBytes(StandardCharsets.UTF_8)), pk);
            NFTAttestation nftAttestation = new NFTAttestation(att, tokens);
            return new AttestationWebResponse(Numeric.toHexString(nftAttestation.getDerEncoding()), encodePublicKey(pk));
        } catch (IOException e) {
            LOGGER.error("Can't create a public attestation with ({}), caused by: {}", publicAttestation, e);
            throw new IllegalAttestationRequestException("Cannot create a public attestation.");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Can't create a signed identifier attestation, caused by: {}", e.getMessage(), e);
            throw new IllegalAttestationRequestException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Something wrong", e);
        }
    }

    public static AttestationWebResponse constructSignedNftAttest(String publicKey, AsymmetricKeyParameter attestorPublicKey
            , String nftAttestation, String signature) {
        AsymmetricKeyParameter pk;
        try {
            pk = SignatureUtility.restoreKeyFromSPKI(Base64.getDecoder().decode(publicKey.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.error("Can't create a public key with ({}), caused by: {}", publicKey, e);
            throw new PublicKeyCreationException("Cannot create a public key.");
        }

        try {
            NFTAttestation att = new NFTAttestation(Numeric.hexStringToByteArray(nftAttestation), attestorPublicKey);
            SignedNFTAttestation signed = new SignedNFTAttestation(att, pk, Numeric.hexStringToByteArray(signature));
            return new AttestationWebResponse(derString(signed), encodePublicKey(attestorPublicKey));
        } catch (IOException e) {
            LOGGER.error("Can't create a nft attestation with ({}), caused by: {}", nftAttestation, e);
            throw new IllegalAttestationRequestException("Cannot create a nft attestation.");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Can't create a signed nft attestation, caused by: {}", e.getMessage(), e);
            throw new IllegalAttestationRequestException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Something wrong", e);
        }
    }

    public static AttestationWebResponse constructCoSignedIdentifierAttest(String publicKey, AsymmetricKeyParameter attestorPublicKey
            , String publicAttestation, String signature) {
        AsymmetricKeyParameter pk;
        try {
            pk = SignatureUtility.restoreKeyFromSPKI(Base64.getDecoder().decode(publicKey.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.error("Can't create a public key with ({}), caused by: {}", publicKey, e);
            throw new PublicKeyCreationException("Cannot create a public key.");
        }

        try {
            SignedIdentifierAttestation signedIdentifier = new SignedIdentifierAttestation(Base64.getDecoder().decode(publicAttestation.getBytes(StandardCharsets.UTF_8)), attestorPublicKey);
            CoSignedIdentifierAttestation coSigned = new CoSignedIdentifierAttestation(signedIdentifier, pk, Numeric.hexStringToByteArray(signature));
            return new AttestationWebResponse(derString(coSigned), encodePublicKey(attestorPublicKey));
        } catch (IOException e) {
            LOGGER.error("Can't create a public attestation with ({}), caused by: {}", publicAttestation, e);
            throw new IllegalAttestationRequestException("Cannot create a public attestation.");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Can't create a cosigned identifier attestation, caused by: {}", e.getMessage(), e);
            throw new IllegalAttestationRequestException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Something wrong", e);
        }
    }

    public static ECPublicKeyParameters getECPublicKeyParameters(String ecPubkeyHex) {
        byte[] ecPubkeyBytes = Hex.decodeStrict(ecPubkeyHex);
        try {
            ECPublicKey ecPubkey = generateP256PublicKeyFromFlatW(ecPubkeyBytes);
            return convert(ecPubkey);
        } catch (InvalidKeySpecException e) {
            throw new PublicKeyCreationException(e.getMessage());
        }
    }

    private static ECPublicKey generateP256PublicKeyFromFlatW(byte[] w) throws InvalidKeySpecException {
        byte[] P256_HEAD = Base64.getDecoder().decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE");
        byte[] encodedKey = new byte[P256_HEAD.length + w.length];
        System.arraycopy(P256_HEAD, 0, encodedKey, 0, P256_HEAD.length);
        System.arraycopy(w, 0, encodedKey, P256_HEAD.length, w.length);
        KeyFactory eckf;
        try {
            eckf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("EC key factory not present in runtime");
        }
        X509EncodedKeySpec ecpks = new X509EncodedKeySpec(encodedKey);
        return (ECPublicKey) eckf.generatePublic(ecpks);
    }

    private static ECPublicKeyParameters convert(ECPublicKey publicKey) {
        final String name = "secp256k1";
        X9ECParameters x9 = ECNamedCurveTable.getByName(name);
        ASN1ObjectIdentifier oid = ECNamedCurveTable.getOID(name);
        byte[] x = publicKey.getW().getAffineX().toByteArray();
        byte[] y = publicKey.getW().getAffineY().toByteArray();
        BigInteger xbi = new BigInteger(1, x);
        BigInteger ybi = new BigInteger(1, y);
        ECPoint point = x9.getCurve().createPoint(xbi, ybi);
        ECNamedDomainParameters dParams = new ECNamedDomainParameters(oid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        return new ECPublicKeyParameters(point, dParams);
    }

    private static AsymmetricKeyParameter getECCPublicKey(String message, String signature, String ethAddress) throws SignatureException {
        byte[] encodedMessage = message.getBytes();
        byte[] compatibilityEncodedMessage = Numeric.hexStringToByteArray(message);
        byte[] sigBytes = Numeric.hexStringToByteArray(signature);
        Sign.SignatureData sd = sigFromByteArray(sigBytes);

        String addressRecovered = "";
        BigInteger publicKey = Sign.signedPrefixedMessageToKey(encodedMessage, sd);
        addressRecovered = "0x" + Keys.getAddress(publicKey);
        System.out.println("Recovered: " + addressRecovered);

        AsymmetricKeyParameter subjectPublicKey = SignatureUtility.recoverEthPublicKeyFromPersonalSignature(encodedMessage, sigBytes);

        //Check if we need MM compatibility mode
        if (!addressRecovered.equalsIgnoreCase(ethAddress)) {
            publicKey = Sign.signedPrefixedMessageToKey(compatibilityEncodedMessage, sd); // <-- recover sign personal message
            addressRecovered = "0x" + Keys.getAddress(publicKey);
            System.out.println("Recovered: " + addressRecovered);
            subjectPublicKey = SignatureUtility.recoverEthPublicKeyFromPersonalSignature(compatibilityEncodedMessage, sigBytes);
        }

        return subjectPublicKey;
    }

    private static Sign.SignatureData sigFromByteArray(byte[] sig) {
        byte subv = (byte) (sig[64]);
        if (subv < 27) subv += 27;

        byte[] subrRev = Arrays.copyOfRange(sig, 0, 32);
        byte[] subsRev = Arrays.copyOfRange(sig, 32, 64);

        BigInteger r = new BigInteger(1, subrRev);
        BigInteger s = new BigInteger(1, subsRev);

        Sign.SignatureData ecSig = new Sign.SignatureData(subv, subrRev, subsRev);

        return ecSig;
    }

    private static String encodePublicKey(AsymmetricKeyParameter publicKey) throws IOException {
        SubjectPublicKeyInfo attestorSpki = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey);
        byte[] attestorPub = attestorSpki.getEncoded();
        return Base64.getEncoder().encodeToString(attestorPub);
    }

    private static String derString(ASNEncodable asnEncodable) throws InvalidObjectException {
        return Base64.getEncoder().encodeToString(asnEncodable.getDerEncoding());
    }

}

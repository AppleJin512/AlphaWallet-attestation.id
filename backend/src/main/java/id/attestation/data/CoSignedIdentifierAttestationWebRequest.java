package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class CoSignedIdentifierAttestationWebRequest {

    @NotNull
    @NotBlank
    private String publicKey;

    @NotNull
    @NotBlank
    private String publicAttestation;

    @NotNull
    @NotBlank
    private String signature;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicAttestation() {
        return publicAttestation;
    }

    public void setPublicAttestation(String publicAttestation) {
        this.publicAttestation = publicAttestation;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public CoSignedIdentifierAttestationWebRequest() {
    }

    public CoSignedIdentifierAttestationWebRequest(String publicKey, String publicAttestation, String signature) {
        this.publicKey = publicKey;
        this.publicAttestation = publicAttestation;
        this.signature = signature;
    }
}

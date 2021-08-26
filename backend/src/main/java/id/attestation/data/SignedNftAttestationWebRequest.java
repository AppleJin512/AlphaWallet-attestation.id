package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class SignedNftAttestationWebRequest {

    @NotNull
    @NotBlank
    private String publicKey;

    @NotNull
    @NotBlank
    private String nftAttestation;

    @NotNull
    @NotBlank
    private String signature;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNftAttestation() {
        return nftAttestation;
    }

    public void setNftAttestation(String nftAttestation) {
        this.nftAttestation = nftAttestation;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public SignedNftAttestationWebRequest() {
    }

    public SignedNftAttestationWebRequest(String publicKey, String nftAttestation, String signature) {
        this.publicKey = publicKey;
        this.nftAttestation = nftAttestation;
        this.signature = signature;
    }
}

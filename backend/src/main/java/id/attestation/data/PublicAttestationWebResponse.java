package id.attestation.data;

public class PublicAttestationWebResponse {
    private String attestation;
    private String attestorPublicKey;
    private String publicKey;

    public PublicAttestationWebResponse() {
    }

    public PublicAttestationWebResponse(String attestation, String attestorPublicKey, String publicKey) {
        this.attestation = attestation;
        this.attestorPublicKey = attestorPublicKey;
        this.publicKey = publicKey;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getAttestorPublicKey() {
        return attestorPublicKey;
    }

    public void setAttestorPublicKey(String attestorPublicKey) {
        this.attestorPublicKey = attestorPublicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}

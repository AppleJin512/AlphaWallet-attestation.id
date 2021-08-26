package id.attestation.data;

public class AttestationWebResponse {
    private String attestation;
    private String attestorPublicKey;

    public AttestationWebResponse() {
    }

    public AttestationWebResponse(String attestation, String attestorPublicKey) {
        this.attestation = attestation;
        this.attestorPublicKey = attestorPublicKey;
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
}

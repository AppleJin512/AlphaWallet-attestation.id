package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Introspected
public class AttestationWebRequest {

    @NotNull
    @NotBlank
    @Positive
    protected long validity;

    @NotNull
    @NotBlank
    protected String attestor;

    @NotNull
    @NotBlank
    protected String publicRequest;

    public AttestationWebRequest() {
    }

    public AttestationWebRequest(long validity, String attestor, String publicRequest) {
        this.validity = validity;
        this.attestor = attestor;
        this.publicRequest = publicRequest;
    }

    public long getValidity() {
        return validity;
    }

    public void setValidity(long validity) {
        this.validity = validity;
    }

    public String getAttestor() {
        return attestor;
    }

    public void setAttestor(String attestor) {
        this.attestor = attestor;
    }

    public String getPublicRequest() {
        return publicRequest;
    }

    public void setPublicRequest(String publicRequest) {
        this.publicRequest = publicRequest;
    }
}

package id.attestation.data;

import io.micronaut.core.annotation.Introspected;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class MagicLinkAttestationWebRequest extends AttestationWebRequest {

    @NotNull
    @NotBlank
    private String magicLink;

    public MagicLinkAttestationWebRequest(long validity, String attestor, String publicRequest, String magicLink) {
        super(validity, attestor, publicRequest);
        this.magicLink = magicLink;
    }

    public String getMagicLink() {
        return magicLink;
    }

    public void setMagicLink(String magicLink) {
        this.magicLink = magicLink;
    }
}

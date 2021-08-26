package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class OtpRequest {

    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @NotBlank
    private String value;

    @NotNull
    @NotBlank
    private String publicKey;

    public OtpRequest(String type, String value, String publicKey) {
        this.type = type;
        this.value = value;
        this.publicKey = publicKey;
    }

    public OtpRequest(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}

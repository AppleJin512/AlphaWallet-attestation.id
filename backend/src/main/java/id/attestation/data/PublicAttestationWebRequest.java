package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Introspected
public class PublicAttestationWebRequest {

    @NotNull
    @NotBlank
    @Positive
    private long id;

    @NotNull
    @NotBlank
    private String message;

    @NotNull
    @NotBlank
    private String signature;

    @NotNull
    @NotBlank
    private String ethAddress;

    @NotNull
    @NotBlank
    private String identifier;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PublicAttestationWebRequest() {
    }

    public PublicAttestationWebRequest(long id, String message, String signature, String ethAddress, String identifier) {
        this.id = id;
        this.message = message;
        this.signature = signature;
        this.ethAddress = ethAddress;
        this.identifier = identifier;
    }
}

package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class OtpResponse {
    private String otpEncrypted;

    public OtpResponse() {
    }

    public OtpResponse(String otpEncrypted) {
        this.otpEncrypted = otpEncrypted;
    }

    public String getOtpEncrypted() {
        return otpEncrypted;
    }

    public void setOtpEncrypted(String otpEncrypted) {
        this.otpEncrypted = otpEncrypted;
    }
}

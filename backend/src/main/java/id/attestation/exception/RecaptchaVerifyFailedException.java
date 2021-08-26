package id.attestation.exception;

public class RecaptchaVerifyFailedException extends RuntimeException{
    public RecaptchaVerifyFailedException(String message) {
        super(message);
    }
}

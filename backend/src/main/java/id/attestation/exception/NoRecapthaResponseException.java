package id.attestation.exception;

public class NoRecapthaResponseException extends RuntimeException{
    public NoRecapthaResponseException(String message) {
        super(message);
    }
}

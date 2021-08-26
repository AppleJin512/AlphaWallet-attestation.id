package id.attestation.service.auth;

public interface AuthenticationService {

    public boolean verify(String accessToken, long userId);

}

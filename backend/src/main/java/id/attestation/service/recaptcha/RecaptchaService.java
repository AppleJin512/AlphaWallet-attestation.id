package id.attestation.service.recaptcha;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

import java.util.Map;

// check document of Recaptha:
// https://developers.google.com/recaptcha/docs/verify#api_request
@Client("https://www.google.com/recaptcha/api/siteverify")
public interface RecaptchaService {

    @Produces({MediaType.APPLICATION_FORM_URLENCODED})
    @Post
    Single<Map> verify(String secret, String response);

}

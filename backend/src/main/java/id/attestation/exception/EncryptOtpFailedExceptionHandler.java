package id.attestation.exception;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {EncryptOtpFailedException.class, HttpResponse.class})
public class EncryptOtpFailedExceptionHandler implements ExceptionHandler<EncryptOtpFailedException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, EncryptOtpFailedException exception) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
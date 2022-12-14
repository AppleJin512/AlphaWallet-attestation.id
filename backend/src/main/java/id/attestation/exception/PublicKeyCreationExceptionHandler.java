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
@Requires(classes = {PublicKeyCreationException.class, HttpResponse.class})
public class PublicKeyCreationExceptionHandler implements ExceptionHandler<PublicKeyCreationException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, PublicKeyCreationException exception) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}

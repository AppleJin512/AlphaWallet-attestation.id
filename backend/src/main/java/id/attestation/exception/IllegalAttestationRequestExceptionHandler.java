package id.attestation.exception;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {IllegalAttestationRequestException.class, HttpResponse.class})
public class IllegalAttestationRequestExceptionHandler implements ExceptionHandler<IllegalAttestationRequestException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, IllegalAttestationRequestException exception) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
package id.attestation.service.auth


import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class Auth0ConditionSpec extends Specification {

    void "should not load Auth0Service without correct env configured"() {
        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        !ctx.containsBean(Auth0Service)

        cleanup:
        ctx.close()
    }

    void "should load Auth0Service with correct env configured"() {
        given:
        System.setProperty("auth0.domain", "XXXX");
        System.setProperty("auth0.client_id", "XXXX");
        System.setProperty("auth0.client_secret", "XXXX");

        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        ctx.containsBean(Auth0Service)

        cleanup:
        ctx.close()
    }

}

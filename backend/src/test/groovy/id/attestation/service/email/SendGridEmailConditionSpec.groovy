package id.attestation.service.email

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class SendGridEmailConditionSpec extends Specification {

    void "should not load SendGridEmailService without correct env configured"() {
        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        !ctx.containsBean(SendGridEmailService)

        cleanup:
        ctx.close()
    }

    void "should load SendGridEmailService with correct env configured"() {
        given:
        System.setProperty("sendgrid.apikey", "XXXX");
        System.setProperty("sendgrid.fromemail", "me@micronaut.example");

        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        SendGridEmailService bean = ctx.getBean(SendGridEmailService);
        "me@micronaut.example" == bean.fromEmail

        cleanup:
        ctx.close()
    }

}

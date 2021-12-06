package id.attestation.service.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
@Requires(condition = SendGridEmailCondition.class)
@Primary
public class SendGridEmailService implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);
    private final String apiKey;
    private final String fromEmail;

    public SendGridEmailService(@Value("${SENDGRID_APIKEY:none}") String apiKeyEnv,
                                @Value("${SENDGRID_FROM_EMAIL:none}") String fromEmailEnv,
                                @Value("${sendgrid.apikey:none}") String apiKeyProp,
                                @Value("${sendgrid.fromemail:none}") String fromEmailProp) {
        this.apiKey = apiKeyEnv != null && !apiKeyEnv.equals("none") ? apiKeyEnv : apiKeyProp;
        this.fromEmail = fromEmailEnv != null && !fromEmailEnv.equals("none") ? fromEmailEnv : fromEmailProp;
    }

    @Override
    public void send(EmailData email) {
        Email from = new Email(fromEmail);
        String subject = email.getSubject();
        Email to = new Email(email.getRecipient());
        Content content = new Content("text/html", email.getHtmlBody());
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        com.sendgrid.Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                LOGGER.error("Mail sent failed, response: status({}), body({})",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Mail sent failed, response: status("
                        + response.getStatusCode() + "), body(" + response.getBody() + ").");
            }

            LOGGER.info("Mail sent successfully, {}", response.getStatusCode());
        } catch (IOException e) {
            LOGGER.error("Mail sent failed", e);
            throw new RuntimeException("Mail sent failed, caused by", e);
        }
    }
}

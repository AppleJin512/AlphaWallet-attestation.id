package id.attestation.service.email;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class EmailTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplate.class);

    static final String eamilTemplate;

    static {
        eamilTemplate = loadTemplate();
    }

    private static String loadTemplate() {
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        try (InputStream inputStream = loader.getResourceAsStream("email.template.html").get()) {
            return new String(inputStream.readAllBytes());
        }catch (IOException e){
            LOGGER.error("Can not load email template", e);
            throw new RuntimeException("Can not load email template", e);
        }
    }

    public static String createEmailBody(String otp) {
        return eamilTemplate.replace("${otp}", otp);
    }

}

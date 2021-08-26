package id.attestation.service.email;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.inject.Singleton;


@Singleton
@Requires(condition = AwsResourceAccessCondition.class)
@Secondary
public class AwsSesMailService implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSesMailService.class);
    private final String sourceEmail;
    private final SesClient ses;

    public AwsSesMailService(@Nullable @Value("${AWS_REGION}") String awsRegionEnv,
                             @Nullable @Value("${AWS_SOURCE_EMAIL}") String sourceEmailEnv,
                             @Nullable @Value("${aws.region}") String awsRegionProp,
                             @Nullable @Value("${aws.sourceemail}") String sourceEmailProp) {

        this.sourceEmail = sourceEmailEnv != null ? sourceEmailEnv : sourceEmailProp;
        String awsRegion = awsRegionEnv != null ? awsRegionEnv : awsRegionProp;
        this.ses = SesClient.builder().region(Region.of(awsRegion)).build();
    }

    @Override
    public void send(EmailData email) {
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(email.getRecipient()).build())
                .source(sourceEmail)
                .message(Message.builder().subject(Content.builder().data(email.getSubject()).build())
                        .body(Body.builder().text(Content.builder().data(email.getHtmlBody()).build()).build()).build())
                .build();
        SendEmailResponse response = ses.sendEmail(sendEmailRequest);

        LOGGER.info("Sent email with id: {}", response.messageId());
    }
}

package id.attestation.service.email

import io.micronaut.context.ApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

class AwsResourceAccessConditionSpec extends Specification {

    void "should not load AwsSesMailService without correct env configured"() {
        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        !ctx.containsBean(AwsSesMailService.class)

        cleanup:
        ctx.close()
    }

    @Unroll
    void "should load AwsSesMailService with (#region, #sourceEmail, #accessKeyId, #secretAccessKey) are set"() {
        given:
        System.setProperty(region, "XXXX");
        System.setProperty(sourceEmail, "me@micronaut.example");
        System.setProperty(accessKeyId, "XXXX");
        System.setProperty(secretAccessKey, "YKYY");

        when:
        ApplicationContext ctx = ApplicationContext.run();

        then:
        AwsSesMailService bean = ctx.getBean(AwsSesMailService.class);
        "me@micronaut.example" == bean.sourceEmail

        cleanup:
        ctx.close()

        where:
        region       | sourceEmail        | accessKeyId         | secretAccessKey
        "aws.region" | "aws.sourceemail"  | "aws.accessKeyId"   | "aws.secretAccessKey"
        "AWS_REGION" | "AWS_SOURCE_EMAIL" | "AWS_ACCESS_KEY_ID" | "AWS_SECRET_ACCESS_KEY"
    }

}

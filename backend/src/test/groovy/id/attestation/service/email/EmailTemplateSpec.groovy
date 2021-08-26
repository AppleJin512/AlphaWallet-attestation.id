package id.attestation.service.email

import spock.lang.Specification

class EmailTemplateSpec extends Specification {

    void "should load email template from class path after server created"() {
        expect:
        EmailTemplate.eamilTemplate.indexOf("\${otp}") > 0
    }

    void "should create a html email from email template loaded"() {
        expect:
        EmailTemplate.createEmailBody("YOUR_OTP").indexOf("YOUR_OTP") > 0
    }

}

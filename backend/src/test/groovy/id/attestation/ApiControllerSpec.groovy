package id.attestation

import id.attestation.data.*
import id.attestation.service.email.EmailService
import id.attestation.service.recaptcha.RecaptchaService
import id.attestation.utils.TestUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import reactor.core.publisher.Mono
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest
class ApiControllerSpec extends Specification {

    @Inject
    EmailService emailService

    @Inject
    RecaptchaService recaptchaService

    @Inject
    @Client("/api")
    HttpClient client

    @Unroll
    void "/otp: should return bad request for invalid otp request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/otp'
                , [type: type, value: value, publicKey: publicKey, 'g-recaptcha-response': 'xxx'])

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        type | value | publicKey
        null | null  | null
        null | '1'   | TestUtils.publicKey
        '1'  | null  | TestUtils.publicKey
        '1'  | ''    | null
        ''   | ''    | ''
        ''   | '1'   | TestUtils.publicKey
        '1'  | ''    | TestUtils.publicKey
        '1'  | ''    | ''
    }


    void "/otp: should return bad request for missing g-recaptcha-response"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/otp', [type: 'mail', value: 'your@mail.com', publicKey: TestUtils.publicKey])

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST
    }

    void "/otp: should return bad request for recaptcha verification failure"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/otp'
                , [type: 'mail', value: 'your@mail.com', publicKey: TestUtils.publicKey, 'g-recaptcha-response': 'xxx'])

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST
        1 * recaptchaService.verify(_, 'xxx') >> Mono.just([success: false, "error-codes": ["err1", "err2"]])
    }

    void "/otp: should return bad request for invalid public key"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/otp'
                , [type: 'mail', value: 'your@mail.com', publicKey: TestUtils.invalidPublicKey, 'g-recaptcha-response': 'xxx'])

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        1 * recaptchaService.verify(_, 'xxx') >> Mono.just([success: true])
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST
    }

    void "/otp should work for mail type"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/otp'
                , [type: 'mail', value: 'your@mail.com', publicKey: TestUtils.publicKey, 'g-recaptcha-response': 'xxx'])

        when:
        HttpResponse response = client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        response.code() == HttpStatus.CREATED.code
        TestUtils.decryptWithRSAOAEP(TestUtils.privateKey, response.body().otpEncrypted).length() == 6
        1 * emailService.send({
            it.recipient == 'your@mail.com'
                    && it.subject == 'Your OTP for Attestation Request'
                    && it.htmlBody.find(/<p class="code">\d{6}<\/p>/)
        })
        1 * recaptchaService.verify(_, 'xxx') >> Mono.just([success: true])
    }

    void "/attestation: should return bad request when x-pap-id-provider not provide in header"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation'
                , new AttestationWebRequest(3600, 'AlphaWallet', TestUtils.publicRequest))

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown()
        thrown.status == HttpStatus.BAD_REQUEST
    }

    void "/attestation: should return bad request when email could not be verified"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation'
                , new AttestationWebRequest(3600, 'AlphaWallet', TestUtils.publicRequest))
                .header('x-pap-id-provider', 'alwaysSuccess')
                .header('x-pap-ac', '123456')

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown()
        thrown.status == HttpStatus.BAD_REQUEST
    }

    @Unroll
    void "/attestation: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation'
                , new AttestationWebRequest(validity, attestor, publicRequest))
                .header('x-pap-id-provider', 'alwaysSuccess')

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        validity | attestor      | publicRequest
        0        | null          | null
        -1       | null          | null
        1000     | null          | TestUtils.publicRequest
        1000     | ''            | TestUtils.publicRequest
        1000     | 'AlphaWallet' | null
        1000     | 'AlphaWallet' | ''
        1000     | 'AlphaWallet' | 'sss'
        1000     | 'AlphaWallet' | TestUtils.badPublicRequest
    }

    @Ignore
    void "/attestation should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation'
                        , new AttestationWebRequest(3600, 'AlphaWallet', TestUtils.publicRequest))
                        .header('x-pap-id-provider', 'alwaysSuccess'),
                        AttestationWebResponse)
        AttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey
    }

    @Unroll
    void "/attestation/magic-link: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/magic-link', new MagicLinkAttestationWebRequest(validity, attestor, publicRequest, magicLink))

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        validity | attestor      | publicRequest                    | magicLink
        0        | null          | null                             | null
        -1       | null          | null                             | TestUtils.magicLink
        1000     | null          | TestUtils.publicRequest          | TestUtils.magicLink
        1000     | ''            | TestUtils.publicRequest          | TestUtils.magicLink
        1000     | 'AlphaWallet' | TestUtils.publicRequest          | TestUtils.magicLink
        1000     | 'AlphaWallet' | null                             | TestUtils.magicLink
        1000     | 'AlphaWallet' | ''                               | TestUtils.magicLink
        1000     | 'AlphaWallet' | 'sss'                            | TestUtils.magicLink
        1000     | 'AlphaWallet' | TestUtils.badPublicRequest       | TestUtils.magicLink
        1000     | 'AlphaWallet' | TestUtils.magicLinkPublicRequest | TestUtils.badMagicLink
    }

    @Ignore
    void "/attestation/magic-link should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/magic-link'
                        , new MagicLinkAttestationWebRequest(3600, 'AlphaWallet', TestUtils.magicLinkPublicRequest, TestUtils.magicLink)),
                        AttestationWebResponse)
        AttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey
    }

    @Unroll
    void "/attestation/public: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/public'
                , new PublicAttestationWebRequest(id as String, message, signature, ethAddress, identifier))
                .header('x-pap-ac', '12345')
                .header('x-pap-id-provider', 'alwaysSuccess')

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        id | message           | signature           | ethAddress           | identifier
        1  | null              | TestUtils.signature | TestUtils.ethAddress | TestUtils.mock2Identifier
        1  | TestUtils.message | ''                  | TestUtils.ethAddress | TestUtils.mock2Identifier
        1  | TestUtils.message | TestUtils.signature | null                 | TestUtils.mock2Identifier
        1  | TestUtils.message | TestUtils.signature | TestUtils.ethAddress | ''
        1  | "ddddd"           | TestUtils.signature | TestUtils.ethAddress | TestUtils.mock2Identifier
        1  | TestUtils.message | "0x0x"              | TestUtils.ethAddress | TestUtils.mock2Identifier
        1  | TestUtils.message | TestUtils.signature | "oajsu"              | TestUtils.mock2Identifier
        1  | TestUtils.message | TestUtils.signature | TestUtils.ethAddress | "foxgem"
    }

    void "/attestation/public: should return bad request when identifier could not be verified"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/public'
                , new PublicAttestationWebRequest('14190486'
                , TestUtils.message
                , TestUtils.signature
                , TestUtils.ethAddress
                , TestUtils.mock2Identifier))
                .header('x-pap-ac', '12345')
                .header('x-pap-id-provider', 'alwaysFail')

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST
    }

    void "/attestation/public: should return bad request when x-pap-id-provider could not be found"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/public'
                        , new PublicAttestationWebRequest('14190486'
                        , TestUtils.message
                        , TestUtils.signature
                        , TestUtils.ethAddress
                        , TestUtils.mock1Identifier))
                        .header('x-pap-ac', '12345')
                        .header('x-pap-id-provider', 'non-exists'),
                        PublicAttestationWebResponse)
        PublicAttestationWebResponse result = response.body()

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST
    }

    void "/attestation/public should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/public'
                        , new PublicAttestationWebRequest('14190486'
                        , TestUtils.message
                        , TestUtils.signature
                        , TestUtils.ethAddress
                        , TestUtils.mock1Identifier))
                        .header('x-pap-ac', '12345')
                        .header('x-pap-id-provider', 'alwaysSuccess'),
                        PublicAttestationWebResponse)
        PublicAttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey
        result.publicKey
    }

    @Unroll
    void "/attestation/nft: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/nft', new NftAttestationWebRequest(publicAttestation, nfts))

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        publicAttestation           | nfts
        "bad attestation"           | TestUtils.nfts1
        TestUtils.publicAttestation | null
        TestUtils.publicAttestation | new NftParam[0]
        TestUtils.publicAttestation | new NftParam[]{new NftParam("0x", new BigInteger("1"))}
    }

    @Unroll
    void "/attestation/nft should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/nft'
                        , new NftAttestationWebRequest(TestUtils.publicAttestation
                        , nfts)),
                        AttestationWebResponse)
        AttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey

        where:
        nfts << [TestUtils.nfts1, TestUtils.nfts2]
    }

    @Unroll
    void "/attestation/signed-nft: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/signed-nft', new SignedNftAttestationWebRequest(publicKey, nftAttestation, signature))

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        publicKey                  | nftAttestation           | signature
        null                       | TestUtils.nftAttestation | TestUtils.nftAttestationSignature
        TestUtils.subjectPublicKey | ""                       | TestUtils.nftAttestationSignature
        TestUtils.subjectPublicKey | TestUtils.nftAttestation | null
        "bad key"                  | TestUtils.nftAttestation | TestUtils.nftAttestationSignature
        TestUtils.subjectPublicKey | "bad attestation"        | TestUtils.nftAttestationSignature
        TestUtils.subjectPublicKey | TestUtils.nftAttestation | "bad signature"
    }

    void "/attestation/signed-nft should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/signed-nft'
                        , new SignedNftAttestationWebRequest(TestUtils.subjectPublicKey, TestUtils.nftAttestation
                        , TestUtils.nftAttestationSignature)),
                        AttestationWebResponse)
        AttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey
    }

    @Unroll
    void "/attestation/cosigned: should return bad request for invalid request parameters"() {
        given:
        HttpRequest httpRequest = HttpRequest.POST('/attestation/cosigned', new CoSignedIdentifierAttestationWebRequest(publicKey, attestation, signature))

        when:
        client.toBlocking().exchange(httpRequest, OtpResponse)

        then:
        HttpClientResponseException thrown = thrown(HttpClientResponseException)
        thrown.status == HttpStatus.BAD_REQUEST

        where:
        publicKey               | attestation             | signature
        null                    | TestUtils.paForCosigned | TestUtils.signatureForCosigned
        TestUtils.pkForCosigned | ""                      | TestUtils.signatureForCosigned
        TestUtils.pkForCosigned | TestUtils.paForCosigned | null
        "bad key"               | TestUtils.paForCosigned | TestUtils.signatureForCosigned
        TestUtils.pkForCosigned | "bad attestation"       | TestUtils.signatureForCosigned
        TestUtils.pkForCosigned | TestUtils.paForCosigned | "bad signature"
    }

    void "/attestation/cosigned should work"() {
        when:
        HttpResponse response = client.toBlocking().
                exchange(HttpRequest.POST('/attestation/cosigned'
                        , new CoSignedIdentifierAttestationWebRequest(TestUtils.pkForCosigned, TestUtils.paForCosigned
                        , TestUtils.signatureForCosigned)),
                        AttestationWebResponse)
        AttestationWebResponse result = response.body()

        then:
        response.code() == HttpStatus.CREATED.code
        result.attestation
        result.attestorPublicKey
    }

    @MockBean(EmailService)
    EmailService emailService() {
        Mock(EmailService)
    }

    @MockBean(RecaptchaService)
    RecaptchaService recaptchaService() {
        Mock(RecaptchaService)
    }

}

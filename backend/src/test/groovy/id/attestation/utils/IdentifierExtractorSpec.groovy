package id.attestation.utils

import spock.lang.Specification

class IdentifierExtractorSpec extends Specification {

    void "should extract identifier from publicRequest"() {
        expect:
        IdentifierExtractor.extract(TestUtils.publicRequest) == "jianhgreat@hotmail.com"
    }
}

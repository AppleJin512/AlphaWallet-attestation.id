package id.attestation.utils

import spock.lang.Specification

class MagicLinkParserSpec extends Specification {

    void "should parse magic link correctly"() {
        expect:
        MagicLinkParser.parse(TestUtils.magicLink) == TestUtils.magicLinkObj
    }
}

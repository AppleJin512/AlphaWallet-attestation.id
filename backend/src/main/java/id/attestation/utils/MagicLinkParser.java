package id.attestation.utils;

import id.attestation.data.MagicLink;
import id.attestation.exception.IllegalAttestationRequestException;
import io.micronaut.core.util.StringUtils;
import java.math.BigInteger;
import okhttp3.HttpUrl;

public class MagicLinkParser {

    private static final String TICKET_PARAM = "ticket";
    private static final String SECRET_PARAM = "secret";

    public static MagicLink parse(String link) {
        HttpUrl httpUrl = HttpUrl.parse(link);
        if (httpUrl == null) {
            throw new IllegalAttestationRequestException("failed to parse the magic link");
        }
        String host = httpUrl.host();
        String encodedTicket = httpUrl.queryParameter(TICKET_PARAM);
        if (StringUtils.isEmpty(encodedTicket)) {
            throw new IllegalAttestationRequestException("ticket param missing from magic link");
        }
        String secretStr = httpUrl.queryParameter(SECRET_PARAM);
        if (StringUtils.isEmpty(secretStr)) {
            throw new IllegalAttestationRequestException("secret param missing from magic link");
        }
        BigInteger secret = BigInteger.valueOf(parseLong(secretStr));
        return new MagicLink(host, encodedTicket, secret);
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalAttestationRequestException("secret param is not integer");
        }
    }
}

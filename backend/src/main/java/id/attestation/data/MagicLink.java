package id.attestation.data;

import java.math.BigInteger;
import java.util.Objects;

public class MagicLink {

    private final String host;
    private final String encodedTicket;
    private final BigInteger secret;

    public MagicLink(String host, String encodedTicket, BigInteger secret) {
        this.host = host;
        this.encodedTicket = encodedTicket;
        this.secret = secret;
    }

    public String getHost() {
        return host;
    }

    public String getEncodedTicket() {
        return encodedTicket;
    }

    public BigInteger getSecret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MagicLink magicLink = (MagicLink) o;
        return host.equals(magicLink.host) && encodedTicket.equals(magicLink.encodedTicket) && secret.equals(magicLink.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, encodedTicket, secret);
    }
}

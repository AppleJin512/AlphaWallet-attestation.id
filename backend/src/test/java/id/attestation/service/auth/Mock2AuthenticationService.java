package id.attestation.service.auth;

import org.pf4j.Extension;

import java.util.List;
import java.util.Map;

@Extension
public class Mock2AuthenticationService implements AuthenticationService {

    @Override
    public String idProvider() {
        return "alwaysFail";
    }

    @Override
    public boolean verifySocialConnection(Map<String, List<String>> headers, String paProvider, String userId) {
        return false;
    }

}

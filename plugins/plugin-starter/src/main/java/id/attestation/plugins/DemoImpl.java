package id.attestation.plugins;

import id.attestation.service.auth.AuthenticationService;
import org.pf4j.Extension;

import java.util.List;
import java.util.Map;

@Extension
public class DemoImpl implements AuthenticationService {

    @Override
    public String idProvider() {
        return "example";
    }

    @Override
    public boolean verify(Map<String, List<String>> headers, String paProvider, String userId) {
        String yourCustomHeaderValue = headers.get("x-pap-custom").get(0);
        // =======
        // your verify code here
        // =======
        return true;
    }

}

package id.attestation.service.auth;

import org.pf4j.ExtensionPoint;

import java.util.List;
import java.util.Map;

public interface AuthenticationService extends ExtensionPoint {

    /**
     * Return which pa provider does this plugin uses. E.g: auth0, firebase, ...
     *
     * @return String
     */
    String idProvider();

    /**
     * Use this extension to verify user identify.
     *
     * @param headers    http headers starts with "x-pap" pass to verify, implementation method should extract required params from headers
     * @param paProvider pa = public attestation. third party sign in provider does this user sign in from. E.g: facebook, twitter, google,...
     * @param userId     userId from paProvider
     * @return boolean
     */
    boolean verify(Map<String, List<String>> headers, String paProvider, String userId);

}

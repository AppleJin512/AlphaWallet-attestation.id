package id.attestation.plugins.auth;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import com.auth0.net.Request;
import id.attestation.service.auth.AuthenticationService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Extension
public class Auth0AuthService implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auth0AuthService.class);

    private final AuthAPI authAPI;

    public Auth0AuthService() {
        String domain = System.getenv("AUTH0_DOMAIN");
        String clientId = System.getenv("AUTH0_CLIENT_ID");
        String clientSecret = System.getenv("AUTH0_CLIENT_SECRET");
        this.authAPI = new AuthAPI(domain, clientId, clientSecret);
    }

    @Override
    public String idProvider() {
        return "auth0";
    }

    @Override
    public boolean verify(Map<String, List<String>> headers, String paProvider, String userId) {
        try {
            String accessToken = headers.get("x-pap-ac").get(0);
            Request<UserInfo> request = this.authAPI.userInfo(accessToken);
            UserInfo info = request.execute();
            // auth0 will combine paProvider and userId in '<paProvider>|<userId>' format into 'sub' field.
            // E.g: {"sub": "twitter|123456789"}
            String sub = (String) info.getValues().get("sub");
            String[] subSplit = sub.split("\\|");
            String provider = subSplit[0];
            String id = subSplit[1];
            return userId.equals(id) && paProvider.equals(provider);
        } catch (APIException exception) {
            LOGGER.error("APIException:", exception);
            return false;
        } catch (Auth0Exception exception) {
            LOGGER.error("Auth0Exception:", exception);
            return false;
        } catch (Exception e) {
            LOGGER.error("Unknown exception:", e);
            return false;
        }
    }

}

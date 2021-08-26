package id.attestation.service.auth;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import com.auth0.net.Request;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Requires(condition = Auth0Condition.class)
@Singleton
public class Auth0Service implements AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Auth0Service.class);

    private String domain;
    private String clientId;
    private String clientSecret;
    private AuthAPI authAPI;

    public Auth0Service(@Value("${AUTH0_DOMAIN:none}") String domainEnv,
                        @Value("${AUTH0_CLIENT_ID:none}") String clientIdEnv,
                        @Value("${AUTH0_CLIENT_SECRET:none}") String clientSecurityEnv,
                        @Value("${auth0.domain:none}") String domainProp,
                        @Value("${auth0.client_id:none}") String clientIdProp,
                        @Value("${auth0.client_secret:none}") String clientSecurityProp) {
        this.domain = domainEnv != null && !domainEnv.equals("none") ? domainEnv : domainProp;
        this.clientId = clientIdEnv != null && !clientIdEnv.equals("none") ? clientIdEnv : clientIdProp;
        this.clientSecret = clientSecurityEnv != null && !clientSecurityEnv.equals("none") ? clientSecurityEnv : clientSecurityProp;
        this.authAPI = new AuthAPI(this.domain, this.clientId, this.clientSecret);
    }

    public boolean verify(String accessToken, long userId) {
        try {
            Request<UserInfo> request = this.authAPI.userInfo(accessToken);
            UserInfo info = request.execute();
            String sub = (String) info.getValues().get("sub");
            long id = Long.valueOf(sub.split("\\|")[1]);
            return id == userId;
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

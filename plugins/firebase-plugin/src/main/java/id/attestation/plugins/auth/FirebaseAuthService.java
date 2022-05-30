package id.attestation.plugins.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import id.attestation.service.auth.AuthenticationService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Extension
public class FirebaseAuthService implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseAuthService.class);

    public FirebaseAuthService() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    public String idProvider() {
        return "firebase";
    }

    @Override
    public boolean verifySocialConnection(Map<String, List<String>> headers, String paProvider, String userId) {
        try {
            String accessToken = headers.get("x-pap-ac").get(0);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(accessToken);
            Map<String, ?> claims = decodedToken.getClaims();
            // firebase claims example:
            // {aud=auth-firebase-poc, auth_time=xxx, exp=xxx, iat=xxx, iss=https://securetoken.google.com/auth-firebase-poc
            // , sub=firebase_uid, name=facebook_name, picture=facebook_picture_url, user_id=firebase_uid
            // , email=facebook_user_email, email_verified=false, firebase={identities={facebook.com=[facebook_uid]
            // , email=[facebook_user_email]}, sign_in_provider=facebook.com}}
            Map<String, ?> firebaseMeta = (Map<String, ?>) claims.get("firebase");
            String signInProvider = (String) firebaseMeta.get("sign_in_provider");
            String signInProviderUid = ((Map<String, List<String>>) firebaseMeta.get("identities"))
                    .get(signInProvider)
                    .get(0);
            return userId.equals(signInProviderUid) && signInProvider.equals(signInProviderMapping(paProvider));
        } catch (FirebaseAuthException exception) {
            LOGGER.error("FirebaseAuthException:", exception);
            return false;
        } catch (Exception e) {
            LOGGER.error("Unknown exception:", e);
            return false;
        }
    }

    @Override
    public boolean verifyEmail(Map<String, List<String>> headers, String userEmail) {
        try {
            String accessToken = headers.get("x-pap-ac").get(0);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(accessToken);
            String email = decodedToken.getEmail();
            return email.equalsIgnoreCase(userEmail);
        } catch (FirebaseAuthException exception) {
            LOGGER.error("FirebaseAuthException:", exception);
            return false;
        } catch (Exception e) {
            LOGGER.error("Unknown exception:", e);
            return false;
        }
    }

    private static String signInProviderMapping(String paProvider) {
        switch (paProvider) {
            case "github":
                return "github.com";
            case "twitter":
                return "twitter.com";
            case "facebook":
                return "facebook.com";
            default:
                return paProvider;
        }
    }

}

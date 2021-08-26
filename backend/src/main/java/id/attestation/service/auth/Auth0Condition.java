package id.attestation.service.auth;

import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.util.StringUtils;

public class Auth0Condition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        return envOrSystemProperty("AUTH0_DOMAIN", "auth0.domain") &&
                envOrSystemProperty("AUTH0_CLIENT_ID", "auth0.client_id") &&
                envOrSystemProperty("AUTH0_CLIENT_SECRET", "auth0.client_secret");
    }

    private boolean envOrSystemProperty(String env, String prop) {
        return StringUtils.isNotEmpty(System.getProperty(prop)) || StringUtils.isNotEmpty(System.getenv(env));
    }
}
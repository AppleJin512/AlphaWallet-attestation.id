package id.attestation.service.plugin;

import id.attestation.service.auth.AuthenticationService;
import io.micronaut.http.HttpHeaders;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class AuthenticationServiceManager {

    @Inject
    private PluginService pluginService;

    public List<AuthenticationService> getAll() {
        return pluginService.getExtensions(AuthenticationService.class);
    }

    public Optional<AuthenticationService> findByIdProvider(String idProvider) {
        List<AuthenticationService> authenticationServices = getAll();
        return authenticationServices.stream()
                .filter(service -> service.idProvider().equals(idProvider))
                .findFirst();
    }

    public Map<String, List<String>> filterRequiredHeaders(HttpHeaders headers) {
        return headers.asMap()
                .entrySet()
                .stream()
                .map(map -> {
                    // backward compatibility for old request use x-ac header
                    if ("x-ac".equalsIgnoreCase(map.getKey())) {
                        return Map.entry("x-pap-ac", map.getValue());
                    } else {
                        return map;
                    }
                })
                .filter(map -> map.getKey().toLowerCase().startsWith("x-pap"))
                .collect(Collectors.toMap(map -> map.getKey().toLowerCase(), Map.Entry::getValue));
    }

}

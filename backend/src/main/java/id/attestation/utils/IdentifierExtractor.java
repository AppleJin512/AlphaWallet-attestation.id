package id.attestation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.attestation.data.PublicRequest;
import id.attestation.data.PublicRequestData;
import id.attestation.exception.IllegalAttestationRequestException;

public class IdentifierExtractor {

    private static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static String extract(String publicRequest) {
        try {
            PublicRequest request = mapper.readValue(publicRequest, PublicRequest.class);
            PublicRequestData publicRequestData = mapper.readValue(request.getJsonSigned(), PublicRequestData.class);
            return publicRequestData.getUserData().getIdentifier();
        } catch (JsonProcessingException e) {
            throw new IllegalAttestationRequestException("unable to extract identifier from public request");
        }
    }

}

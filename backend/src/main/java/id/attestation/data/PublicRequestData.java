package id.attestation.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicRequestData {

    @JsonProperty("message")
    private UserData userData;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public static class UserData {

        private String identifier;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }
}

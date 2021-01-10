package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Map;

public class UserSettingsResponsePojo implements Serializable {
    protected String timeZone;
    protected Map<String, String> timeZones;


    public static final class Builder {
        protected String timeZone;
        protected Map<String, String> timeZones;

        private Builder() {
        }

        public static Builder anUserSettingsResponsePojo() {
            return new Builder();
        }

        public Builder timeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder timeZones(Map<String, String> timeZones) {
            this.timeZones = timeZones;
            return this;
        }

        public UserSettingsResponsePojo build() {
            UserSettingsResponsePojo userSettingsResponsePojo = new UserSettingsResponsePojo();
            userSettingsResponsePojo.timeZones = this.timeZones;
            userSettingsResponsePojo.timeZone = this.timeZone;
            return userSettingsResponsePojo;
        }
    }
}
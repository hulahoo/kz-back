package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.global.TimeZones;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.pojo.UserSettingsResponsePojo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

@Service(UserSettingService.NAME)
public class UserSettingServiceBean implements UserSettingService {

    @Inject
    private TimeZones timeZones;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public UserSettingsResponsePojo getTimeZones() {
        Map<String, String> options = new TreeMap<>();
        for (String id : TimeZone.getAvailableIDs()) {
            TimeZone timeZone = TimeZone.getTimeZone(id);
            options.put(id, timeZones.getDisplayNameLong(timeZone));
        }

        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();

        return UserSettingsResponsePojo.Builder.anUserSettingsResponsePojo()
                .timeZone(user.getTimeZone() != null ? user.getTimeZone() : null)
                .timeZones(options)
                .build();
    }
}
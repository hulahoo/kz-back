package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.pojo.UserSettingsResponsePojo;

public interface UserSettingService {
    String NAME = "tsadv_UserSettingService";

    UserSettingsResponsePojo getTimeZones();
}
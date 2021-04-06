package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.pojo.BellNotificationResponsePojo;
import kz.uco.uactivity.entity.ActivityType;

import java.util.List;

public interface NotificationService {
    String NAME = "tsadv_NotificationService";

    List<BellNotificationResponsePojo> notifications();

    List<BellNotificationResponsePojo> tasks();

    String getLinkByCode(ActivityType activityType);
}
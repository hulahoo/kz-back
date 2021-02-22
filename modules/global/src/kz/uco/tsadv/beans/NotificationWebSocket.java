package kz.uco.tsadv.beans;

import java.util.UUID;

public interface NotificationWebSocket {
    void sendNotificationsCount(Object count, UUID userId);

    void sendNotificationsCount(String count, UUID userId);

    void closeConnection(UUID userId);
}

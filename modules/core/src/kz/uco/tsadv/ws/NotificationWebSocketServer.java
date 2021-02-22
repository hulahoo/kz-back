/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kz.uco.tsadv.ws;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haulmont.cuba.security.app.Authenticated;
import kz.uco.tsadv.beans.NotificationWebSocket;
import kz.uco.tsadv.service.portal.NotificationDao;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component("tsadv_NotificationWebSocketServer")
public class NotificationWebSocketServer extends TextWebSocketHandler implements NotificationWebSocket {

    private Map<UUID, WebSocketSession> openedWsConnections = new HashMap<>();

    @Inject
    private NotificationDao notificationDao;

    @Authenticated
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> name = new Gson().fromJson(message.getPayload(), new TypeToken<Map<String, String>>() {
        }.getType());
        UUID userId = UUID.fromString(name.get("userId"));
        openedWsConnections.remove(userId);
        if (name.get("action").equalsIgnoreCase("create")) {
            openedWsConnections.put(userId, session);
            this.sendNotificationsCount(notificationDao.notificationsCount(userId), userId);
        }
    }

    @Authenticated
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        this.sendNotificationsCount(notificationDao.notificationsCount(currentUserId), currentUserId);
    }

    @Authenticated
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println(status.getReason());
//        openedWsConnections.remove(userSessionSource.currentOrSubstitutedUserId());
    }

    @Override
    public void sendNotificationsCount(Object count, UUID userId) {
        this.sendNotificationsCount(count.toString(), userId);
    }

    @Override
    @Authenticated
    public void sendNotificationsCount(String count, UUID userId) {
        openedWsConnections.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(userId))
                .findAny()
                .ifPresent(e -> {
                    try {
                        e.getValue().sendMessage(new TextMessage(count));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @Override
    public void closeConnection(UUID userId) {
        openedWsConnections.remove(userId);
    }
}

package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.SendingMessage;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.entity.core.notification.NotificationTemplate;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.entity.core.notification.SendingSms;
import kz.uco.base.entity.core.notification.SendingTelegram;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.enums.TemplateType;
import kz.uco.base.notification.NotificationSenderAPIServiceBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public class NotificationSenderAPIServiceBeanExt extends NotificationSenderAPIServiceBean {

    @Override
    public SendingNotification sendParametrizedNotification(
            String notificationCode,
            UserExt user,
            Map<String, Object> params,
            EmailAttachment... emailAttachments
    ) {
        SendingNotification sendingNotification = null;
        if (user == null || notificationCode == null) return sendingNotification;

        if (params == null) params = new HashMap<>();
        Configuration configuration = AppBeans.get(Configuration.NAME);
        params.put("webAppUrl", configuration.getConfig(GlobalConfig.class).getWebAppUrl());

        //copied for ImmutableMap
        Map<String, Object> expandedParams = new HashMap<>(params);

        NotificationTemplate notificationTemplate = getNotificationTemplate(notificationCode);
        if (notificationTemplate == null) return sendingNotification;

        String emailCaption = user.getEmail() != null
                ? getUserTemplate(notificationTemplate, TemplateType.EMAIL_CAPTION, user)
                : null;
        String emailTemplate = user.getEmail() != null
                ? getUserTemplate(notificationTemplate, TemplateType.EMAIL, user)
                : null;
        String smsTemplate = user.getMobilePhone() != null
                ? getUserTemplate(notificationTemplate, TemplateType.SMS, user)
                : null;
        String telegramTemplate = user.getTelegramChatId() != null
                ? getUserTemplate(notificationTemplate, TemplateType.TELEGRAM, user)
                : null;

        if (user.getEmail() != null && emailTemplate != null
                || user.getMobilePhone() != null && smsTemplate != null
                || user.getTelegramChatId() != null && telegramTemplate != null) {

            sendingNotification = metadata.create(SendingNotification.class);
            sendingNotification.setReaded(false);
            sendingNotification.setUser(user);
            sendingNotification.setTemplate(notificationTemplate);

            if (user.getEmail() != null && emailTemplate != null) {
                EmailInfo emailInfo;
                if (emailAttachments == null || emailAttachments.length == 0) {
                    emailInfo = new EmailInfo(user.getEmail(),
                            TemplateHelper.processTemplate(emailCaption, expandedParams),
                            "<html><head><meta charset=\"UTF-8\"></head>" + TemplateHelper.processTemplate(emailTemplate, expandedParams) + "</html>");
                } else {
                    emailInfo = new EmailInfo(user.getEmail(),
                            TemplateHelper.processTemplate(emailCaption, expandedParams),
                            null,
                            "<html>" + TemplateHelper.processTemplate(emailTemplate, expandedParams) + "</html>",
                            emailAttachments);
                }
                List<SendingMessage> sm = emailerAPI.sendEmailAsync(emailInfo);
                if (sm != null && !sm.isEmpty()) {
                    sendingNotification.setSendingMessage(sm.get(0));
                }

            }

            if (user.getMobilePhone() != null && smsTemplate != null) {
                SendingSms ss = smsSenderAPI.sendSmsAsync(
                        user.getMobilePhone(),
                        TemplateHelper.processTemplate(smsTemplate, expandedParams));
                if (ss != null)
                    sendingNotification.setSendingSms(ss);
            }
            if (user.getTelegramChatId() != null && telegramTemplate != null) {
                SendingTelegram tf = telegramSenderAPI.sendTelegramAsync(
                        user.getTelegramChatId(),
                        TemplateHelper.processTemplate(telegramTemplate, expandedParams));
                if (tf != null)
                    sendingNotification.setSendingTelegram(tf);
            }
            persistNotification(sendingNotification);
        }
        return sendingNotification;
    }

}

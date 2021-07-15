package kz.uco.tsadv.bproc.beans.entity;

import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;

import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public interface IBprocEntityBean<T extends AbstractBprocRequest> {

    void start(T entity);

    void cancel(T entity);

    void reject(T entity);

    void approve(T entity);

    String changeNotificationTemplateCode(String notificationTemplateCode, T entity);

    Map<String, Object> getNotificationParams(String templateCode, T entity);

    void changeRequestStatus(T entity, String code);
}

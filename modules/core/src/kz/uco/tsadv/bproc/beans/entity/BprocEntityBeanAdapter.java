package kz.uco.tsadv.bproc.beans.entity;

import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component(BprocEntityBeanAdapter.NAME)
public class BprocEntityBeanAdapter<T extends AbstractBprocRequest> implements IBprocEntityBean<T> {
    public static final String NAME = "tsadv_BprocEntityBeanAdapter";

    @Inject
    protected List<AbstractBprocEntityBean<? extends AbstractBprocRequest>> bprocEntityBeans;
    @Inject
    protected DefaultBprocEntityBean defaultBprocEntityBean;

    @Override
    public void start(T entity) {
        this.getEntityBean(entity).start(entity);
    }

    @Override
    public void cancel(AbstractBprocRequest entity) {
        this.getEntityBean(entity).cancel(entity);
    }

    @Override
    public void reject(AbstractBprocRequest entity) {
        this.getEntityBean(entity).reject(entity);
    }

    @Override
    public void approve(AbstractBprocRequest entity) {
        this.getEntityBean(entity).approve(entity);
    }

    @Override
    public String changeNotificationTemplateCode(String notificationTemplateCode, AbstractBprocRequest entity) {
        return this.getEntityBean(entity).changeNotificationTemplateCode(notificationTemplateCode, entity);
    }

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, T entity) {
        return this.getEntityBean(entity).getNotificationParams(templateCode, entity);
    }

    @Override
    public void changeRequestStatus(T entity, String code) {
        this.getEntityBean(entity).changeRequestStatus(entity, code);
    }

    protected AbstractBprocEntityBean<AbstractBprocRequest> getEntityBean(AbstractBprocRequest bprocRequest) {
        return this.getEntityBean(bprocRequest.getClass());
    }

    protected AbstractBprocEntityBean<AbstractBprocRequest> getEntityBean(Class<? extends AbstractBprocRequest> aClass) {
        for (AbstractBprocEntityBean<? extends AbstractBprocRequest> bprocEntityBean : bprocEntityBeans) {
            if (bprocEntityBean.instanceOf(aClass))
                return (AbstractBprocEntityBean<AbstractBprocRequest>) bprocEntityBean;
        }
        return defaultBprocEntityBean;
    }
}
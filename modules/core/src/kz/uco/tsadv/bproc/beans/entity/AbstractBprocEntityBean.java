package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.service.BprocService;
import org.springframework.core.ResolvableType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public abstract class AbstractBprocEntityBean<T extends AbstractBprocRequest> implements IBprocEntityBean<T> {

    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected BprocService bprocService;

    @Override
    public void start(T entity) {
        changeRequestStatus(entity, "APPROVING");
    }

    @Override
    public void cancel(T entity) {
        changeRequestStatus(entity, "CANCELED_BY_INITIATOR");
    }

    @Override
    public void revision(T entity) {
        changeRequestStatus(entity, "TO_BE_REVISED");
    }

    @Override
    public void reject(T entity) {
        changeRequestStatus(entity, "REJECT");
        String rejectNotificationTemplateCode = bprocService.getProcessVariable(entity, "rejectNotificationTemplateCode");
        bprocService.sendNotificationToInitiator(entity, rejectNotificationTemplateCode);
    }

    @Override
    public void approve(T entity) {
        changeRequestStatus(entity, "APPROVED");
        String approveNotificationTemplateCode = bprocService.getProcessVariable(entity, "approveNotificationTemplateCode");
        bprocService.sendNotificationToInitiator(entity, approveNotificationTemplateCode);
    }

    @Override
    public String changeNotificationTemplateCode(String notificationTemplateCode, T entity) {
        return notificationTemplateCode;
    }

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, T entity) {
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void changeRequestStatus(T entity, String code) {
        entity = (T) transactionalDataManager.load(entity.getClass())
                .id(entity.getId())
                .view(new View(entity.getClass()).addProperty("status")).one();
        entity.setStatus(commonService.getEntity(DicRequestStatus.class, code));
        transactionalDataManager.save(entity);
    }

    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return ResolvableType.forClass(AbstractBprocEntityBean.class, this.getClass()).getGeneric(0).isAssignableFrom(tClass);
    }

}

package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import kz.uco.tsadv.modules.personal.model.BpmRequestMessage;
import kz.uco.tsadv.service.BpmService;
import kz.uco.uactivity.entity.Activity;
import org.springframework.stereotype.Component;

/**
 * @author Alibek Berdaulet
 */
@Component("tsadv_BpmRequestMessageListener")
public class BpmRequestMessageListener implements BeforeInsertEntityListener<BpmRequestMessage> {

    @Override
    public void onBeforeInsert(BpmRequestMessage entity, EntityManager entityManager) {
        Activity activity = AppBeans.get(BpmService.class).bpmRequestAskAnswerNotification(entity);
        entity.setActivity(activity);
        entityManager.merge(activity);
    }
}

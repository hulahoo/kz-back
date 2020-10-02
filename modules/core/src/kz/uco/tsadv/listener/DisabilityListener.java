package kz.uco.tsadv.listener;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Disability;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Connection;

/**
 * @author Alibek Berdaulet
 */
@Component("tsadv_DisabilityListener")
public class DisabilityListener implements AfterDeleteEntityListener<Disability>,
        AfterInsertEntityListener<Disability>,
        AfterUpdateEntityListener<Disability> {

    @Inject
    protected IntegrationConfig integrationConfig;

    @Override
    public void onAfterDelete(Disability entity, Connection connection) {
        callUpdatePersonIntegration(entity.getPersonGroupExt(), connection);
    }

    @Override
    public void onAfterInsert(Disability entity, Connection connection) {
        callUpdatePersonIntegration(entity.getPersonGroupExt(), connection);
    }

    @Override
    public void onAfterUpdate(Disability entity, Connection connection) {
        callUpdatePersonIntegration(entity.getPersonGroupExt(), connection);
    }

    protected void callUpdatePersonIntegration(PersonGroupExt personGroupExt, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                PersonExt personExt = getPerson(personGroupExt);
                AppBeans.get(IntegrationService.class).updatePerson(personExt, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }

    protected PersonExt getPerson(PersonGroupExt personGroupExt) {
        return getPerson(personGroupExt, "person-edit");
    }

    @Transactional
    protected PersonExt getPerson(PersonGroupExt personGroupExt, String viewName) {
        return AppBeans.get(CommonService.class).getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :groupId " +
                        "   and :sysdate between e.startDate and e.endDate",
                ParamsMap.of("groupId", personGroupExt.getId(), "sysdate", CommonUtils.getSystemDate()),
                viewName);
    }
}

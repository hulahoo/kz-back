package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceForRecallDataJson;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

@Component("tsadv_AbsenceForRecallListener")
public class AbsenceForRecallListener implements BeforeUpdateEntityListener<AbsenceForRecall> {

    private final String APPROVED_STATUS = "APPROVED";
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    IntegrationRestService integrationRestService;

    @Override
    public void onBeforeUpdate(AbsenceForRecall entity, EntityManager entityManager) {
        if (entity.getStatus().getCode() != null && entity.getStatus().getCode().equals(APPROVED_STATUS)) {
            AbsenceForRecallDataJson absenceForRecallJson = new AbsenceForRecallDataJson();
            String personId = (entity.getEmployee() != null && entity.getEmployee().getLegacyId() != null) ? entity.getEmployee().getLegacyId() : "";
            absenceForRecallJson.setPersonId(personId);
            absenceForRecallJson.setRequestNumber(entity.getRequestNumber().toString());
//            absenceForRecallJson.setRequestDate(formatter.parse(entity.getRequestDate().toString()));

        }
    }
}
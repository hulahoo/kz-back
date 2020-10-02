package kz.uco.tsadv.web.modules.selfservice.Requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import java.util.Date;

public class RequisitionEdit extends AbstractEditor<Requisition> {

    @Inject
    protected Datasource<Requisition> requisitionDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Button button;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DataSupplier dataSupplier;

    @Override
    protected void postInit() {
        super.postInit();

        PersonGroupExt personGroupExt = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        if (personGroupExt != null) {
            personGroupExt = dataSupplier.reload(personGroupExt, "personGroupExt-view-for-selvservice-requisition");

            Long count = commonService.getCount(JobRequest.class,
                    "select e from tsadv$JobRequest e where e.requisition.id=:requisitionId" +
                            " and e.candidatePersonGroup.id=:personGroupExtId",
                    ParamsMap.of("requisitionId", requisitionDs.getItem().getId(),
                            "personGroupExtId",
                            personGroupExt.getId()));
            if (count > 0) {
                button.setEnabled(false);
            }else {
                button.setEnabled(true);
            }
        } else {
            button.setEnabled(false);
        }
    }

    public void onButtonClick() {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setCandidatePersonGroup(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
        jobRequest.setRequisition(requisitionDs.getItem());
        jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);

        jobRequest.setRequestDate(new Date());
        dataManager.commit(jobRequest);
        postInit();


    }
}

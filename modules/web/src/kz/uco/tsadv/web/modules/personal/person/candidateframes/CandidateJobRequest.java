package kz.uco.tsadv.web.modules.personal.person.candidateframes;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.JobRequestHistory;
import kz.uco.base.service.common.CommonService;

import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author veronika.buksha
 */
public class CandidateJobRequest extends AbstractFrame {

    public CollectionDatasource<JobRequest, UUID> jobRequestsDs;
    public CollectionDatasource<JobRequestHistory, UUID> jobRequestHistoryDs;
    public Datasource<PersonGroupExt> personGroupDs;
    protected ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);
    protected CommonService commonService = AppBeans.get(CommonService.class);
    protected Messages messages = AppBeans.get(Messages.class);

    @Named("jobRequestsTable.create")
    private CreateAction jobRequestsTableCreate;
    @Named("jobRequestsTable.edit")
    private EditAction jobRequestsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        jobRequestsDs = (CollectionDatasource<JobRequest, UUID>) getDsContext().get("jobRequestsDs");
        personGroupDs = (Datasource<PersonGroupExt>) getDsContext().get("personGroupDs");
        jobRequestHistoryDs = (CollectionDatasource<JobRequestHistory, UUID>) getDsContext().get("jobRequestHistoryDs");

        jobRequestsTableCreate.setInitialValues(Collections.singletonMap("candidatePersonGroup", personGroupDs.getItem()));
        jobRequestsTableCreate.setWindowParams(Collections.singletonMap("parentFrameId", this.getFrame().getId()));
        jobRequestsTableEdit.setWindowParams(Collections.singletonMap("parentFrameId", this.getFrame().getId()));

        jobRequestsTableEdit.setAfterCommitHandler(entity -> {
            jobRequestsDs.refresh();
        });

    }

    public Component generateInterview(JobRequest entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        Interview interview = entity.getInterview();

        if (interview != null)
            linkButton.setAction(new BaseAction("link")
                    .withCaption(interview.getRequisitionHiringStep().getHiringStep().getStepName() + " (" + messages.getMessage(interview.getInterviewStatus()) + ")")
                    .withHandler(actionPerformedEvent -> {
                        AbstractEditor<Interview> interviewEditor = openEditor("tsadv$Interview.edit", interview, WindowManager.OpenType.THIS_TAB);
                        interviewEditor.addCloseWithCommitListener(() -> jobRequestsDs.refresh());
                    }));
        return linkButton;
    }

    public Component generatePassedInterviews(JobRequest entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getPassedInterviews() != null && entity.getTotalInterviews() != null)
            label.setValue(String.format(messages.getMainMessage("JobRequest.interviews"),
                    entity.getPassedInterviews(),
                    entity.getTotalInterviews()));

        return label;
    }
}

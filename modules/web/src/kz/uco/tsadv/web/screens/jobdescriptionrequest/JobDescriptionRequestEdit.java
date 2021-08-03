package kz.uco.tsadv.web.screens.jobdescriptionrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

@UiController("tsadv_JobDescriptionRequest.edit")
@UiDescriptor("job-description-request-edit.xml")
@EditedEntityContainer("jobDescriptionRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class JobDescriptionRequestEdit extends AbstractBprocEditor<JobDescriptionRequest> {

}
package kz.uco.tsadv.web.screens.jobdescriptionrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;

@UiController("tsadv_JobDescriptionRequest.browse")
@UiDescriptor("job-description-request-browse.xml")
@LookupComponent("jobDescriptionRequestsTable")
@LoadDataBeforeShow
public class JobDescriptionRequestBrowse extends StandardLookup<JobDescriptionRequest> {

    public void openRequest(Entity item, String columnId) {
    }
}
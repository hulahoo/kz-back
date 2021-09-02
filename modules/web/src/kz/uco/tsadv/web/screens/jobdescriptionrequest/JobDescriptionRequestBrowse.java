package kz.uco.tsadv.web.screens.jobdescriptionrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.builders.EditorBuilder;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;

import javax.inject.Inject;

@UiController("tsadv_JobDescriptionRequest.browse")
@UiDescriptor("job-description-request-browse.xml")
@LookupComponent("jobDescriptionRequestsTable")
@LoadDataBeforeShow
public class JobDescriptionRequestBrowse extends StandardLookup<JobDescriptionRequest> {

    @Inject
    protected Screens screens;
    @Inject
    protected ScreenBuilders screenBuilders;

    public void openRequest(Entity item, String columnId) {
        JobDescriptionRequest jobDescriptionRequest = (JobDescriptionRequest) item;
        EditorBuilder<JobDescriptionRequest> jobDescriptionRequestEditorBuilder = screenBuilders.editor(JobDescriptionRequest.class, this);
        Screen screen = jobDescriptionRequestEditorBuilder.withScreenClass(JobDescriptionRequestEdit.class)
                .editEntity(jobDescriptionRequest).withOpenMode(OpenMode.THIS_TAB)
                .withOptions(new MapScreenOptions(ParamsMap.of("readOnly", true)))
                .build().show();
    }
}
package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobRequestSelfBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Button interviewsButtonId;

    @Inject
    private CollectionDatasource<JobRequest, UUID> jobRequestsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        interviewsButtonId.setEnabled(false);
        jobRequestsDs.addItemChangeListener(e -> {
            if (e.getItem() == null)
                interviewsButtonId.setEnabled(false);
            else
                interviewsButtonId.setEnabled(true);
        });
    }

    public Component generatePositionOrJobCell(JobRequest entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getRequisition().getPositionGroup() == null)
            label.setValue(entity.getRequisition().getJobGroup().getJob().getJobName());
        else
            label.setValue(entity.getRequisition().getPositionGroup().getPosition().getPositionName());
        return label;
    }

    public void toRquisition(JobRequest entity, String name) {
        openEditor("tsadv$Requisition.edit",
                entity.getRequisition(),
                WindowManager.OpenType.THIS_TAB,
                Collections.singletonMap("browseOnly", true));
    }

    public void onInterviewsButtonIdClick() {
        Map<String, Object> map = new HashMap<>();
        map.put("jobRequestIdFromJobRequestSelf", jobRequestsDs.getItem());
        openWindow("tsadv$Interview.browse", WindowManager.OpenType.THIS_TAB, map);
    }
}
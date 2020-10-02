package kz.uco.tsadv.web.jobrequest;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobRequestForGroupInterviewWindow extends AbstractWindow {
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected GroupDatasource<JobRequest, UUID> jobRequestsDs;
    @Inject
    protected GroupTable<JobRequest> jobRequestsDataGrid;

    @Inject
    private DataGridDetailsGeneratorService service;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public Component generateLastComment(JobRequest entity) {
        List<Interview> interviews = entity.getInterviews();
        Interview latestInterview = null;
        for (Interview interview : interviews) {
            if (latestInterview == null) {
                latestInterview = interview;
            } else if (latestInterview.getInterviewDate().before(interview.getInterviewDate())) {
                latestInterview = interview;
            } else if (latestInterview.getInterviewDate().equals(interview.getJobRequest())
                    && latestInterview.getTimeTo().before(interview.getTimeTo())) {
                latestInterview = interview;
            }
        }
        String comment;
        if (latestInterview == null || latestInterview.getComment() == null || latestInterview.getComment().isEmpty()) {
            comment = "";
        } else {
            comment = latestInterview.getComment();
        }

        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(comment);
        return label;
    }
}
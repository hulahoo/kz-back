package kz.uco.tsadv.web.modules.performance.jobgroupgoallink;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.JobGroupGoalLink;

import java.util.UUID;

public class JobGroupGoalLinkEdit extends AbstractEditor<JobGroupGoalLink> {

    @Override
    protected void initNewItem(JobGroupGoalLink item) {
        super.initNewItem(item);
        item.setTargetValue(100);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if(getItem().getGoal() != null) {
            if (((CollectionDatasource<JobGroupGoalLink, UUID>) getParentDs()) != null
            && ((CollectionDatasource<JobGroupGoalLink, UUID>) getParentDs()).getItems() != null) {
                ((CollectionDatasource<JobGroupGoalLink, UUID>) getParentDs()).getItems().forEach(gl -> {
                    if (getItem().getGoal().equals(gl.getGoal()) && !gl.getId().equals(getItem().getId()))
                        errors.add(getMessage("ValidationError.goalLink.uniqueGoal"));
                });
            }
        }
    }
}
package kz.uco.tsadv.web.modules.performance.organizationgroupgoallink;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.OrganizationGroupGoalLink;

import java.util.Map;
import java.util.UUID;

public class OrganizationGroupGoalLinkEdit extends AbstractEditor<OrganizationGroupGoalLink> {

    protected CollectionDatasource<OrganizationGroupGoalLink, UUID> goalsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if(params.containsKey("goalsDs")){
            goalsDs = (CollectionDatasource<OrganizationGroupGoalLink, UUID>) params.get("goalsDs");
        }
    }

    @Override
    protected void initNewItem(OrganizationGroupGoalLink item) {
        super.initNewItem(item);
        item.setTargetValue(100);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if(getItem().getGoal() != null) {
            if (goalsDs != null && goalsDs.getItems() != null) {
                goalsDs.getItems().forEach(gl -> {
                    if (getItem().getGoal().equals(gl.getGoal()) && !gl.getId().equals(getItem().getId())) {
                        errors.add(getMessage("ValidationError.goalLink.uniqueGoal"));
                    }
                });
            }
        }
    }
}
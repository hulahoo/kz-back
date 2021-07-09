package kz.uco.tsadv.web.modules.performance.positiongroupgoallink;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.PositionGroupGoalLink;

import java.util.Map;
import java.util.UUID;

public class PositionGroupGoalLinkEdit extends AbstractEditor<PositionGroupGoalLink> {

    protected CollectionDatasource<PositionGroupGoalLink, UUID> goalsDs;

    @Override
    protected void initNewItem(PositionGroupGoalLink item) {
        super.initNewItem(item);
        item.setTargetValue(100);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if(params.containsKey("goalsDs")){
            goalsDs = (CollectionDatasource<PositionGroupGoalLink, UUID>) params.get("goalsDs");
        }
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
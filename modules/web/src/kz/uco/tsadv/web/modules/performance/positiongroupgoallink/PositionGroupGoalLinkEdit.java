package kz.uco.tsadv.web.modules.performance.positiongroupgoallink;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.PositionGroupGoalLink;

import java.util.UUID;

public class PositionGroupGoalLinkEdit extends AbstractEditor<PositionGroupGoalLink> {

//    @Override
//    protected void postValidate(ValidationErrors errors) {
//        super.postValidate(errors);
//        ((CollectionDatasource<PositionGroupGoalLink, UUID>) getParentDs()).getItems().forEach(gl -> {
//                if (getItem().getGoal().equals(gl.getGoal())&& !getItem().getId().equals(gl.getId()))
//                    errors.add(getMessage("ValidationError.goalLink.uniqueGoal"));
//            });
//
//    }
}
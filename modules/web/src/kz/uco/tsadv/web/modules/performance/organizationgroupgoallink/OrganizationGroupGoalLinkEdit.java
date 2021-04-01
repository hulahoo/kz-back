package kz.uco.tsadv.web.modules.performance.organizationgroupgoallink;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.OrganizationGroupGoalLink;

import java.util.UUID;

public class OrganizationGroupGoalLinkEdit extends AbstractEditor<OrganizationGroupGoalLink> {
    private boolean isCreat;

    @Override
    protected void initNewItem(OrganizationGroupGoalLink item) {
        super.initNewItem(item);
        isCreat = true;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (isCreat) {
            if (((CollectionDatasource<OrganizationGroupGoalLink, UUID>) getParentDs()) != null) {
                ((CollectionDatasource<OrganizationGroupGoalLink, UUID>) getParentDs()).getItems().forEach(gl -> {
                    if (getItem().getGoal().equals(gl.getGoal()))
                        errors.add(getMessage("ValidationError.goalLink.uniqueGoal"));
                });
            }
        }
    }
}
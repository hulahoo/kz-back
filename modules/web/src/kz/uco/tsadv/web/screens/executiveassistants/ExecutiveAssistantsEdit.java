package kz.uco.tsadv.web.screens.executiveassistants;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.ExecutiveAssistants;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.annotation.Nonnull;
import javax.inject.Inject;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_ExecutiveAssistants.edit")
@UiDescriptor("executive-assistants-edit.xml")
@EditedEntityContainer("executiveAssistantsDc")
@LoadDataBeforeShow
public class ExecutiveAssistantsEdit extends StandardEditor<ExecutiveAssistants> {

    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected MessageTools messageTools;
    @Inject
    private PickerField<OrganizationGroupExt> assistanceOrganizationGroup;
    @Inject
    private PickerField<OrganizationGroupExt> managerOrganizationGroup;
    @Inject
    private DataManager dataManager;

    @Override
    protected void validateAdditionalRules(@Nonnull ValidationErrors errors) {
        super.validateAdditionalRules(errors);

        ExecutiveAssistants editedEntity = getEditedEntity();
        if (editedEntity.getStartDate() != null && editedEntity.getEndDate() != null
                && editedEntity.getStartDate().after(editedEntity.getEndDate()))
            errors.add(String.format(messageBundle.getMessage("validate.dates"),
                    messageTools.getPropertyCaption(getEditedEntity().getMetaClass(), "endDate"),
                    messageTools.getPropertyCaption(getEditedEntity().getMetaClass(), "startDate")
            ));
    }

    protected void chageOrgValue(PickerField<OrganizationGroupExt> pickerField, PositionGroupExt positionGroup) {
        if (positionGroup != null)
            pickerField.setValue(dataManager.reload(positionGroup, "executiveAssistants-browseView").getPosition().getOrganizationGroupExt());
        else pickerField.setValue(null);
    }

    @Subscribe("managerPositionGroupField")
    public void onManagerPositionGroupFieldValueChange(HasValue.ValueChangeEvent<PositionGroupExt> event) {
        this.chageOrgValue(managerOrganizationGroup, event.getValue());
    }

    @Subscribe("assistancePositionGroupField")
    public void onAssistancePositionGroupFieldValueChange(HasValue.ValueChangeEvent<PositionGroupExt> event) {
        this.chageOrgValue(assistanceOrganizationGroup, event.getValue());
    }
}
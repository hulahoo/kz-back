package kz.uco.tsadv.web.screens.executiveassistants;

import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.ExecutiveAssistants;

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
}
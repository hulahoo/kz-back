package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;
import java.util.Date;
import java.util.Set;

@UiController("tsadv$InsuredPersonBulk.edit")
@UiDescriptor("insured-person-bulk-edit.xml")
@EditedEntityContainer("insuredPersonDc")
@LoadDataBeforeShow
public class InsuredPersonBulkEdit extends StandardEditor<InsuredPerson> {
    @Inject
    private LookupField<DicMICAttachmentStatus> statusRequestField;
    private Set<InsuredPerson> bulks;
    @Inject
    private DateField<Date> attachDateField;
    @Inject
    private DateField<Date> exclusionDateField;
    @Inject
    private DataManager dataManager;
    @Inject
    private Button closeBtn;
    @Inject
    private HBoxLayout editActions;
    @Inject
    private Actions actions;
    @Inject
    private TextArea<String> commentField;

    public void setParameter(Set<InsuredPerson> bulks){
        this.bulks = bulks;
    }

    @Subscribe("statusRequestField")
    public void onStatusRequestFieldValueChange(HasValue.ValueChangeEvent<DicMICAttachmentStatus> event) {
        if (event.getValue() != null && "EXCLUDED".equals(statusRequestField.getValue().getCode())){
            commentField.setRequired(true);
        }
    }

    @Subscribe("okBtn")
    public void onOkBtnClick(Button.ClickEvent event) {
        CommitContext context = new CommitContext();
        bulks.forEach(insuredPerson -> {
            insuredPerson.setAttachDate(attachDateField.getValue());
            insuredPerson.setComment(commentField.getValue());
            insuredPerson.setExclusionDate(exclusionDateField.getValue());
            insuredPerson.setStatusRequest(statusRequestField.getValue());
            context.addInstanceToCommit(insuredPerson);
        });
        dataManager.commit(context);
        closeWithDefaultAction();
    }
}
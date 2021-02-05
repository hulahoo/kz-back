package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.global.common.CommonUtils;
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
        if (event.getValue() != null){
            if ("EXCLUDED".equals(statusRequestField.getValue().getCode())){
                commentField.setRequired(true);
                exclusionDateField.setRequired(true);
                exclusionDateField.setVisible(true);
                exclusionDateField.setValue(CommonUtils.getSystemDate());
                attachDateField.setRequired(false);
                attachDateField.setVisible(false);
                attachDateField.setValue(null);
            }else if ("INSURED".equals(statusRequestField.getValue().getCode())){
                attachDateField.setVisible(true);
                attachDateField.setValue(CommonUtils.getSystemDate());
                attachDateField.setRequired(true);
                exclusionDateField.setVisible(false);
                exclusionDateField.setRequired(false);
                exclusionDateField.setValue(null);
                commentField.setRequired(false);
            }else if ("DRAFT".equals(statusRequestField.getValue().getCode())){
                attachDateField.setVisible(true);
                attachDateField.setValue(CommonUtils.getSystemDate());
                attachDateField.setRequired(true);
                exclusionDateField.setVisible(false);
                exclusionDateField.setRequired(false);
                exclusionDateField.setValue(null);
                commentField.setRequired(false);
            }
        }else {
            attachDateField.setValue(null);
            exclusionDateField.setValue(null);
            commentField.setRequired(false);
            attachDateField.setVisible(false);
            exclusionDateField.setVisible(false);
        }
    }

    @Subscribe("okBtn")
    public void onOkBtnClick(Button.ClickEvent event) {
        CommitContext context = new CommitContext();
        bulks.forEach(insuredPerson -> {
            if (attachDateField.getValue() != null){
                insuredPerson.setAttachDate(attachDateField.getValue());
            }
            if (commentField.getValue() != null){
                insuredPerson.setComment(commentField.getValue());
            }
            if (exclusionDateField.getValue() != null){
                insuredPerson.setExclusionDate(exclusionDateField.getValue());
            }

            insuredPerson.setStatusRequest(statusRequestField.getValue());
            context.addInstanceToCommit(insuredPerson);
        });
        dataManager.commit(context);
        closeWithDefaultAction();
    }


}
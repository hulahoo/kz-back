package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
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
    @Inject
    private TextField<String> commentField;


    public void setParameter(Set<InsuredPerson> bulks){

    }

    @Subscribe("statusRequestField")
    public void onStatusRequestFieldValueChange(HasValue.ValueChangeEvent<DicMICAttachmentStatus> event) {
        if (event.getValue() != null && "EXCLUDED".equals(statusRequestField.getValue().getCode())){
            commentField.setRequired(true);
        }
    }





}
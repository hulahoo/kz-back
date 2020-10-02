package kz.uco.tsadv.web.personalprotection;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personprotection.PersonalProtection;
import kz.uco.tsadv.modules.personprotection.enums.PersonalProtectionEquipmentStatus;

import javax.inject.Inject;

public class PersonalProtectionEditReplacement extends AbstractEditor<PersonalProtection> {

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<PersonalProtection> personalProtectionDs;

    @Override
    public void commitAndClose() {
        if (getItem().getReplacementDate() != null) {
            PersonalProtection personalProtection = metadata.create(PersonalProtection.class);
            personalProtection.setEmployee(getItem().getEmployee());
            personalProtection.setProtectionEquipment(getItem().getProtectionEquipment());
            personalProtection.setDateOfIssue(getItem().getReplacementDate());
            personalProtection.setStatus(getItem() != null ? getItem().getStatus() : null);
            personalProtection.setWrittenOfReason(null);
            personalProtection.setWrittenOfDate(getItem() != null ? getItem().getWrittenOfDate() : null);
            personalProtection.setQuantity(getItem().getQuantity());
            personalProtection.setPlanChangeDate(getItem() != null ? getItem().getPlanChangeDate() : null);
            personalProtection.setCondition(getItem() != null ? getItem().getCondition() : null);
            personalProtection.setIsAcceptedByPerson(getItem() != null ? getItem().getIsAcceptedByPerson() : false);

            getItem().setStatus(PersonalProtectionEquipmentStatus.RETURN);
            getItem().setWrittenOfDate(getItem().getReplacementDate());
            dataManager.commit(personalProtection);
        }
        super.commitAndClose();
    }
}
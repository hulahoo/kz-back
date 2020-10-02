package kz.uco.tsadv.web.personalprotection;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personprotection.PersonalProtection;
import kz.uco.tsadv.modules.personprotection.enums.PersonalProtectionEquipmentStatus;

import javax.inject.Inject;

public class PersonalProtectionEditReturn extends AbstractEditor<PersonalProtection> {

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<PersonalProtection> personalProtectionDs;

    @Override
    public void commitAndClose() {
        if (getItem().getWrittenOfDate() != null) {
            getItem().setStatus(PersonalProtectionEquipmentStatus.RETURN);
        }
        super.commitAndClose();
    }
}
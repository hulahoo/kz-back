package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personprotection.PersonalProtection;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;


public class PcfPersonalProtection extends EditableFrame {

    @Named("personalProtectionTable.create")
    protected CreateAction personalProtectionTableCreate;
    @Named("personalProtectionTable.edit")
    protected EditAction personalProtectionTableEdit;
    public CollectionDatasource<PersonalProtection, UUID> personalProtectionDs;

    @Override
    public void editable(boolean editable) {
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personalProtectionTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("employee", getDsContext().get("personGroupDs").getItem())
                        : null);

        personalProtectionTableCreate.setWindowId("tsadv$PersonalProtection.edit.issue");
        personalProtectionTableEdit.setWindowId("tsadv$PersonalProtection.edit.replacement");

        personalProtectionTableEdit.setAfterWindowClosedHandler((window, closeActionId) -> {
            personalProtectionDs.refresh();
        });
    }

    public void returnButton() {
        AbstractEditor abstractEditor = openEditor("tsadv$PersonalProtection.edit.return", personalProtectionDs.getItem(), WindowManager.OpenType.THIS_TAB);
        abstractEditor.addCloseListener(actionId -> {
            personalProtectionDs.refresh();
        });
    }

    @Override
    public void initDatasource() {
        personalProtectionDs = (CollectionDatasource<PersonalProtection, UUID>) getDsContext().get("personalProtectionDs");
    }
}
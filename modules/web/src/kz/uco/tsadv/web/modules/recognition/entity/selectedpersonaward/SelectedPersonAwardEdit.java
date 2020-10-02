package kz.uco.tsadv.web.modules.recognition.entity.selectedpersonaward;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.recognition.SelectedPersonAward;

import javax.inject.Named;

public class SelectedPersonAwardEdit extends AbstractEditor<SelectedPersonAward> {

    @Named("fieldGroup.awardProgram")
    private PickerField awardProgramField;
    @Named("fieldGroup.personGroup")
    private PickerField personGroupField;

    @Override
    protected void postInit() {
        super.postInit();

        if (!PersistenceHelper.isNew(getItem())) {
            awardProgramField.setEditable(false);
            personGroupField.setEditable(false);
        }
    }
}
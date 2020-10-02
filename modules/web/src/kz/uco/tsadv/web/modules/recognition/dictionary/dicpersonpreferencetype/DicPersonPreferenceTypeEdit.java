package kz.uco.tsadv.web.modules.recognition.dictionary.dicpersonpreferencetype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonPreferenceType;

public class DicPersonPreferenceTypeEdit extends AbstractEditor<DicPersonPreferenceType> {

    @Override
    protected void initNewItem(DicPersonPreferenceType item) {
        super.initNewItem(item);

        item.setCoins(0L);
    }
}
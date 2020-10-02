package kz.uco.tsadv.web.modules.recognition.dictionary.dicrecognitiontype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;

public class DicRecognitionTypeEdit extends AbstractEditor<DicRecognitionType> {

    @Override
    protected void initNewItem(DicRecognitionType item) {
        super.initNewItem(item);

        item.setCoins(0L);
    }
}
package kz.uco.tsadv.web.modules.recognition.entity.personpoint;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.recognition.PersonPoint;

public class PersonPointEdit extends AbstractEditor<PersonPoint> {

    @Override
    protected void initNewItem(PersonPoint item) {
        super.initNewItem(item);
        item.setPoints(0L);
    }
}
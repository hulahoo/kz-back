package kz.uco.tsadv.web.modules.recognition.entity.personcoin;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.recognition.PersonCoin;

public class PersonCoinEdit extends AbstractEditor<PersonCoin> {

    @Override
    protected void initNewItem(PersonCoin item) {
        super.initNewItem(item);
        item.setCoins(0L);
    }
}
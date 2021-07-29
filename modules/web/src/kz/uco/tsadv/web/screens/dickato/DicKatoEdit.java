package kz.uco.tsadv.web.screens.dickato;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicKato;

@UiController("tsadv_DicKato.edit")
@UiDescriptor("dic-kato-edit.xml")
@EditedEntityContainer("dicKatoDc")
@LoadDataBeforeShow
public class DicKatoEdit extends StandardEditor<DicKato> {
}
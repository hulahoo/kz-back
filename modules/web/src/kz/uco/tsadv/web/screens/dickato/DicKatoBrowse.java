package kz.uco.tsadv.web.screens.dickato;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicKato;

@UiController("tsadv_DicKato.browse")
@UiDescriptor("dic-kato-browse.xml")
@LookupComponent("dicKatoesTable")
@LoadDataBeforeShow
public class DicKatoBrowse extends StandardLookup<DicKato> {
}
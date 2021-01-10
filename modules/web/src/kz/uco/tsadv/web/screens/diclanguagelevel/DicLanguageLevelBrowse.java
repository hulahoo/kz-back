package kz.uco.tsadv.web.screens.diclanguagelevel;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguageLevel;

@UiController("tsadv_DicLanguageLevel.browse")
@UiDescriptor("dic-language-level-browse.xml")
@LookupComponent("dicLanguageLevelsTable")
@LoadDataBeforeShow
public class DicLanguageLevelBrowse extends StandardLookup<DicLanguageLevel> {
}
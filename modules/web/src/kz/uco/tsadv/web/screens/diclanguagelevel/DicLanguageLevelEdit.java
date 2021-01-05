package kz.uco.tsadv.web.screens.diclanguagelevel;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguageLevel;

@UiController("tsadv_DicLanguageLevel.edit")
@UiDescriptor("dic-language-level-edit.xml")
@EditedEntityContainer("dicLanguageLevelDc")
@LoadDataBeforeShow
public class DicLanguageLevelEdit extends StandardEditor<DicLanguageLevel> {
}
package kz.uco.tsadv.web.screens.dicissuingauthority;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIssuingAuthority;

@UiController("tsadv_DicIssuingAuthority.edit")
@UiDescriptor("dic-issuing-authority-edit.xml")
@EditedEntityContainer("dicIssuingAuthorityDc")
@LoadDataBeforeShow
public class DicIssuingAuthorityEdit extends StandardEditor<DicIssuingAuthority> {
}
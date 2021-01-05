package kz.uco.tsadv.web.screens.dicissuingauthority;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIssuingAuthority;

@UiController("tsadv_DicIssuingAuthority.browse")
@UiDescriptor("dic-issuing-authority-browse.xml")
@LookupComponent("dicIssuingAuthoritiesTable")
@LoadDataBeforeShow
public class DicIssuingAuthorityBrowse extends StandardLookup<DicIssuingAuthority> {
}
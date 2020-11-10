package kz.uco.tsadv.web.screens.diccompany;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;

@UiController("tsadv$DicCompany.browse")
@UiDescriptor("dic-company-browse.xml")
@LookupComponent("dicCompaniesTable")
@LoadDataBeforeShow
public class DicCompanyBrowse extends StandardLookup<DicCompany> {
}
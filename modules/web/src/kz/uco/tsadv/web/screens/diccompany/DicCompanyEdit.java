package kz.uco.tsadv.web.screens.diccompany;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;

@UiController("tsadv$DicCompany.edit")
@UiDescriptor("dic-company-edit.xml")
@EditedEntityContainer("dicCompanyDc")
@LoadDataBeforeShow
public class DicCompanyEdit extends StandardEditor<DicCompany> {
}
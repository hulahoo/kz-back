package kz.uco.tsadv.web.screens.personpayslip;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonPayslip;

@UiController("tsadv_PersonPayslip.browse")
@UiDescriptor("person-payslip-browse.xml")
@LookupComponent("personPayslipsTable")
@LoadDataBeforeShow
public class PersonPayslipBrowse extends StandardLookup<PersonPayslip> {
}
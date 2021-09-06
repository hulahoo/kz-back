package kz.uco.tsadv.web.screens.personpayslip;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonPayslip;

@UiController("tsadv_PersonPayslip.edit")
@UiDescriptor("person-payslip-edit.xml")
@EditedEntityContainer("personPayslipDc")
@LoadDataBeforeShow
public class PersonPayslipEdit extends StandardEditor<PersonPayslip> {
}
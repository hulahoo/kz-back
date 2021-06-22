package kz.uco.tsadv.web.screens.personpayslip;

import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.PersonPayslip;

import javax.inject.Inject;

@UiController("tsadv_PersonPayslip.browse")
@UiDescriptor("person-payslip-browse.xml")
@LookupComponent("personPayslipsTable")
@LoadDataBeforeShow
public class PersonPayslipBrowse extends StandardLookup<PersonPayslip> {

    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private ComponentsFactory componentsFactory;

    public void generatorName(PersonPayslip item, String columnId) {
        exportDisplay.show(item.getFile(), ExportFormat.OCTET_STREAM);
    }
}
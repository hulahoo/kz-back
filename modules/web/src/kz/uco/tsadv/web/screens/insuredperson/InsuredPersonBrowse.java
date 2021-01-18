package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

@UiController("tsadv$InsuredPerson.browse")
@UiDescriptor("insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class InsuredPersonBrowse extends StandardLookup<InsuredPerson> {
}
package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@UiController("base$PersonForKpiCard.browse")
@UiDescriptor("person-for-kpi-card.xml")
@LookupComponent("dataGrid")
@LoadDataBeforeShow
public class PersonForKpiCard extends StandardLookup<PersonExt> {
}
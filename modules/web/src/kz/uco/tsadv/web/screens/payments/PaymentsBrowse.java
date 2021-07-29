package kz.uco.tsadv.web.screens.payments;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Payments;

@UiController("tsadv_Payments.browse")
@UiDescriptor("payments-browse.xml")
@LookupComponent("paymentsesTable")
@LoadDataBeforeShow
public class PaymentsBrowse extends StandardLookup<Payments> {
}
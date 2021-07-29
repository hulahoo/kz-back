package kz.uco.tsadv.web.screens.payments;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Payments;

@UiController("tsadv_Payments.edit")
@UiDescriptor("payments-edit.xml")
@EditedEntityContainer("paymentsDc")
@LoadDataBeforeShow
public class PaymentsEdit extends StandardEditor<Payments> {
}
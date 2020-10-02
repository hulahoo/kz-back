package kz.uco.tsadv.web.modules.personal.businesstripcost;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.LookupPickerField;
import kz.uco.tsadv.modules.personal.model.BusinessTripCost;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;


public class BusinessTripCostEdit extends AbstractEditor<BusinessTripCost> {

    @Named("fieldGroup.currency")
    protected Field currencyField;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        ((LookupPickerField) currencyField).removeAllActions();
    }
}
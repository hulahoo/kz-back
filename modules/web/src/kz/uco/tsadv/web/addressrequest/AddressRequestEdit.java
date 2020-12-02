package kz.uco.tsadv.web.addressrequest;

import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AddressRequest;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;

import javax.inject.Inject;

public class AddressRequestEdit<T extends AddressRequest> extends AbstractBpmEditor<T> {

    /*public static final String PROCESS_NAME = "addressRequest";

    @Inject
    protected FieldGroup fieldGroup;

    @Override
    protected void initNewItem(T item) {
        if (item.getCountry() == null) {
            item.setCountry(commonService.getEntity(DicCountry.class, "398"));
        }
        if (item.getStartDate() == null) {
            item.setStartDate(CommonUtils.getSystemDate());
        }
        if (item.getEndDate() == null) {
            item.setEndDate(CommonUtils.getEndOfTime());
        }
        super.initNewItem(item);
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (getItem().getRequestNumber() == null && !isDraft()) {
            getItem().setRequestNumber(employeeNumberService.generateNextRequestNumber());
            commit();
        }
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        if (!isDraft()) {
            fieldGroup.getFields().forEach(f -> f.setEditable(false));
        }
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }*/
}
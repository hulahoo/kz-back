package kz.uco.tsadv.web.modules.personal.dictionary.dicoperatorcode;

import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicOperatorCode;
import kz.uco.base.web.abstraction.AbstractDictionaryEditor;

import javax.inject.Inject;
import javax.inject.Named;

public class DicOperatorCodeEdit extends AbstractDictionaryEditor<DicOperatorCode> {

    @Inject
    private FieldGroup fieldGroup;

    @Named("fieldGroup.startDate")
    private DateField startDateField;

    @Named("fieldGroup.endDate")
    private DateField endDateField;


//    @Override
//    protected void initNewItem(DicOperatorCode item) {
//        super.initNewItem(item);
//        item.setStartDate(CommonUtils.getSystemDate());
//        item.setEndDate(CommonUtils.getEndOfTime());
//    }
}
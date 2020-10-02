package kz.uco.tsadv.web.modules.learning.internshipexpenses;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.InternshipExpenses;

import javax.inject.Inject;

public class InternshipExpensesEdit extends AbstractEditor<InternshipExpenses> {
    @Inject
    private CommonService commonService;


    @Override
    protected void initNewItem(InternshipExpenses item) {
        super.initNewItem(item);
        item.setCurrency(commonService.getEntity(DicCurrency.class, "KZT"));
    }
}
package kz.uco.tsadv.web.modules.personal.competence;

import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.Competence;
import kz.uco.tsadv.gui.components.AbstractHrEditor;

import javax.inject.Inject;
import javax.inject.Named;

public class CompetenceEdit extends AbstractHrEditor<Competence> {
    @Inject
    private FieldGroup fieldGroup;

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    @Named("fieldGroup.startDate")
    private DateField startDateField;

    @Named("fieldGroup.endDate")
    private DateField endDateField;

    @Override
    protected void initNewItem(Competence item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}
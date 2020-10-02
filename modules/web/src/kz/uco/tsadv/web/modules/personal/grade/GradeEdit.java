package kz.uco.tsadv.web.modules.personal.grade;

import com.haulmont.bali.util.ParamsMap;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.model.Grade;

import javax.inject.Inject;

public class GradeEdit extends AbstractHrEditor<Grade> {
    /*@Inject
    private FieldGroup fieldGroup;

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    @Named("fieldGroup.startDate")
    private DateField startDateField;

    @Named("fieldGroup.endDate")
    private DateField endDateField; */
    @Inject
    private CommonService commonService;

    @Override
    protected void initNewItem(Grade item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    @Override
    protected boolean preCommit() {
        Grade grade = commonService.emQueryFirstResult(Grade.class,
                "select e from tsadv$Grade e\n" +
                        "where e.gradeName = :gradeName and e.id <> :gradeId",
                ParamsMap.of("gradeName", getItem().getGradeName(),
                        "gradeId", getItem().getId()),
                "_local");
        if (grade != null) {
            showNotification(messages.getMessage("kz.uco.tsadv.web", "uniqueGrade"), NotificationType.TRAY);
        }
        return grade == null;
    }

    @Override
    protected boolean postCommit(final boolean committed, final boolean close) {
        return super.postCommit(committed, close);
    }
}
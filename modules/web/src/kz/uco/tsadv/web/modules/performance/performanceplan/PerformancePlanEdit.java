package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.screen.MessageBundle;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;

import javax.inject.Inject;
import java.util.Date;

public class PerformancePlanEdit extends AbstractEditor<PerformancePlan> {
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Datasource<PerformancePlan> performancePlanDs;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Button assignedEdit;
    @Inject
    protected Screens screens;

    @Override
    protected void init(InitEvent initEvent) {
        super.init(initEvent);
        PickerField.LookupAction administratorPersonGroup = ((PickerField) fieldGroup
                .getFieldNN("administratorPersonGroup").getComponentNN()).getLookupAction();
        administratorPersonGroup.setLookupScreen("base$PersonGroup.browse");
        administratorPersonGroup.setLookupScreenParamsSupplier(() ->
                ParamsMap.of("administratorPersonGroup", true)
        );

    }

    @Override
    protected boolean preCommit() {
        Date accessibilityStartDate = performancePlanDs.getItem().getAccessibilityStartDate();
        Date startDate = performancePlanDs.getItem().getStartDate();
        Date accessibilityEndDate = performancePlanDs.getItem().getAccessibilityEndDate();
        Date endDate = performancePlanDs.getItem().getEndDate();
        if (accessibilityStartDate != null && startDate != null && accessibilityStartDate.before(startDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
            return false;
        }
        if (accessibilityEndDate != null && endDate != null && accessibilityEndDate.after(endDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessEndDateNotBeAfter")).show();
            return false;
        }
        return super.preCommit();
    }

    public void edit() {
        screens.create("tsadv_AssignedPerformancePlanEdit.edit", OpenMode.THIS_TAB).show();
    }
}
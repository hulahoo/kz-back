package kz.uco.tsadv.web.screens.activity;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;

import javax.inject.Inject;

@UiController("uactivity$ActivityForHandbell.browse")
@UiDescriptor("activity-for-handbell-browse.xml")
@LookupComponent("groupBox")
public class ActivityForHandbellBrowse extends StandardLookup<Activity> {
    @WindowParam
    Activity activity;
    @Inject
    private Label<String> header;
    @Inject
    private Label<String> body;
    @Inject
    private DataManager dataManager;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        header.setValue(activity.getNotificationHeader());
        body.setValue(activity.getNotificationBody());
    }

    @Subscribe("lookUpAction")
    public void onLookUpActionClick(Button.ClickEvent event) {
        activity.setStatus(StatusEnum.done);
        dataManager.commit(activity);
        closeWithDefaultAction();
    }
}
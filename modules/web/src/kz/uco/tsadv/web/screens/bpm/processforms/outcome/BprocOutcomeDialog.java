package kz.uco.tsadv.web.screens.bpm.processforms.outcome;

import com.haulmont.addon.bproc.web.uicomponent.outcomespanel.OutcomesPanel;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.administration.UserExt;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.function.Supplier;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_BprocOutcomeDialog")
@UiDescriptor("bproc-outcome-dialog.xml")
public class BprocOutcomeDialog extends Screen {

    @Inject
    protected Notifications notifications;
    @Inject
    protected Messages messages;

    @Inject
    protected PickerField<UserExt> userPicker;
    @Inject
    protected TextArea<String> commentField;

    protected String outcome;
    protected boolean isCommentRequired;
    protected Action action;
    protected OutcomesPanel outcomesPanel;

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public void setCommentRequired(boolean commentRequired) {
        isCommentRequired = commentRequired;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setOutcomesPanel(OutcomesPanel outcomesPanel) {
        this.outcomesPanel = outcomesPanel;
    }

    public Supplier<UserExt> getUserSupplier() {
        return () -> userPicker.getValue();
    }

    public String getComment() {
        return commentField.getValue();
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        getWindow().setCaption(messages.getMainMessage("OUTCOME_" + outcome));
        commentField.setRequired(isCommentRequired);
        userPicker.setVisible(outcome.equals(AbstractBprocRequest.OUTCOME_REASSIGN));
    }

    @Subscribe("ok")
    protected void onOkClick(Button.ClickEvent event) {
        if (!validate()) return;
        action.actionPerform(event.getButton());

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);
    }

    @Subscribe("cancel")
    protected void onCancelClick(Button.ClickEvent event) {
        closeWithDefaultAction();
    }

    protected boolean validate() {
        if (userPicker.isVisible() && userPicker.getValue() == null) {
            notifications.create(Notifications.NotificationType.TRAY)
                    .withDescription(messages.getMainMessage("fill.user"))
                    .show();
            return false;
        }
        if (commentField.isRequired() && StringUtils.isBlank(commentField.getValue())) {
            notifications.create(Notifications.NotificationType.TRAY)
                    .withDescription(messages.getMainMessage("fill.comment"))
                    .show();
            return false;
        }

        return true;
    }
}
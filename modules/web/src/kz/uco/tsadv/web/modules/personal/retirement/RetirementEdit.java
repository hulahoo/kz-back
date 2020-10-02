package kz.uco.tsadv.web.modules.personal.retirement;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Frame;
import kz.uco.tsadv.modules.personal.model.Retirement;

public class RetirementEdit extends AbstractEditor<Retirement> {
    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), Frame.NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}
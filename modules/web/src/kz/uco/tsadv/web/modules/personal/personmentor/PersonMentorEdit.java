package kz.uco.tsadv.web.modules.personal.personmentor;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.personal.model.PersonMentor;

import javax.inject.Named;
import java.util.Map;

public class PersonMentorEdit extends AbstractEditor<PersonMentor> {

    @Named("fieldGroup.mentor")
    protected PickerField mentorField;

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }

    @Override
    protected void postInit() {
        super.postInit();
        mentorField.addValueChangeListener(e -> {
            if (getItem().getMentor() != null) {
                if (getItem().getMentor().getCurrentAssignment() != null) {
                    getItem().setOrganizationGroup(getItem().getMentor().getCurrentAssignment().getOrganizationGroup());
                }
            }
        });
    }
}
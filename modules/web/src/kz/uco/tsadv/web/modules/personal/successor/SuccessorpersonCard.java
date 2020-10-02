package kz.uco.tsadv.web.modules.personal.successor;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.personal.model.Successor;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class SuccessorpersonCard extends AbstractEditor<Successor> {
    @Inject
    @Named("fieldGroup.personGroup")
    private PickerField personGroupField;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

    }

    @Override
    protected boolean preCommit() {
        getItem().setPersonGroup(dataManager.reload(getItem().getPersonGroup(), "personGroup.listInfo"));
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close){
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), Frame.NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }

}
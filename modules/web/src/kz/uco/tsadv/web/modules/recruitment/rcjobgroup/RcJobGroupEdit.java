package kz.uco.tsadv.web.modules.recruitment.rcjobgroup;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import kz.uco.tsadv.modules.recruitment.model.RcJobGroup;

import javax.inject.Named;
import java.util.Map;

public class RcJobGroupEdit extends AbstractEditor<RcJobGroup> {
    @Named("jobsTable.remove")
    private RemoveAction jobsTableRemove;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        jobsTableRemove.setConfirm(false);
    }
}
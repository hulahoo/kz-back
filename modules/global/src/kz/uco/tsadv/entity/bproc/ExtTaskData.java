package kz.uco.tsadv.entity.bproc;

import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import java.util.List;

@Extends(TaskData.class)
@MetaClass(name = "tsadv_ExtTaskData")
public class ExtTaskData extends TaskData {
    private static final long serialVersionUID = 4783014765359912908L;

    @MetaProperty
    private List<User> assigneeOrCandidates;

    @MetaProperty
    private String outcome;

    @MetaProperty
    private DicHrRole hrRole;

    public DicHrRole getHrRole() {
        return hrRole;
    }

    public void setHrRole(DicHrRole hrRole) {
        this.hrRole = hrRole;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public List<User> getAssigneeOrCandidates() {
        return assigneeOrCandidates;
    }

    public void setAssigneeOrCandidates(List<User> assigneeOrCandidates) {
        this.assigneeOrCandidates = assigneeOrCandidates;
    }
}
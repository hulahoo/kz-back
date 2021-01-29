package kz.uco.tsadv.entity.bproc;

import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Extends;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import java.util.List;

@Extends(TaskData.class)
@MetaClass(name = "tsadv_ExtTaskData")
public class ExtTaskData extends TaskData {
    private static final long serialVersionUID = 4783014765359912908L;

    @MetaProperty
    private List<UserExt> assigneeOrCandidates;

    @MetaProperty
    private String outcome;

    @MetaProperty
    private DicHrRole hrRole;

    @MetaProperty
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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

    public List<UserExt> getAssigneeOrCandidates() {
        return assigneeOrCandidates;
    }

    public void setAssigneeOrCandidates(List<UserExt> assigneeOrCandidates) {
        this.assigneeOrCandidates = assigneeOrCandidates;
    }

    public static String getUniqueCommentKey(Outcome outcome) {
        return outcome.getTaskDefinitionKey() + outcome.getDate().getTime() + outcome.getUser();
    }
}
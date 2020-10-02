package kz.uco.tsadv.modules.recognition.feedback;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_RCG_FEEDBACK_ATTACHMENT")
@Entity(name = "tsadv$RcgFeedbackAttachment")
public class RcgFeedbackAttachment extends AbstractParentEntity {
    private static final long serialVersionUID = 4208863760370356772L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RCG_FEEDBACK_ID")
    protected RcgFeedback rcgFeedback;

    public void setRcgFeedback(RcgFeedback rcgFeedback) {
        this.rcgFeedback = rcgFeedback;
    }

    public RcgFeedback getRcgFeedback() {
        return rcgFeedback;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


}
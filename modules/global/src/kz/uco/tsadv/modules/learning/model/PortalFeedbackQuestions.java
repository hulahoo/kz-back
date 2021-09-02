package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@PublishEntityChangedEvents
@Table(name = "TSADV_PORTAL_FEEDBACK_QUESTIONS")
@Entity(name = "tsadv_PortalFeedbackQuestions")
@NamePattern("%s|user")
public class PortalFeedbackQuestions extends AbstractParentEntity {
    private static final long serialVersionUID = -6641391280104550281L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected TsadvUser user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PORTAL_FEEDBACK_ID")
    protected PortalFeedback portalFeedback;

    @NotNull
    @Column(name = "TOPIC", nullable = false)
    protected String topic;

    @NotNull
    @Lob
    @Column(name = "TEXT", nullable = false)
    protected String text;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    private DicPortalFeedbackType type;

    @JoinTable(name = "TSADV_PORTAL_FEEDBACK_QUESTIONS_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PORTAL_FEEDBACK_QUESTIONS_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> files;

    public DicPortalFeedbackType getType() {
        return type;
    }

    public void setType(DicPortalFeedbackType type) {
        this.type = type;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptor> files) {
        this.files = files;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public PortalFeedback getPortalFeedback() {
        return portalFeedback;
    }

    public void setPortalFeedback(PortalFeedback portalFeedback) {
        this.portalFeedback = portalFeedback;
    }

    public TsadvUser getUser() {
        return user;
    }

    public void setUser(TsadvUser user) {
        this.user = user;
    }
}
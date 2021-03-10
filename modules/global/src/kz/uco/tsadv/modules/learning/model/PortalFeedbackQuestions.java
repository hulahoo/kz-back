package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@PublishEntityChangedEvents
@Table(name = "TSADV_PORTAL_FEEDBACK_QUESTIONS")
@Entity(name = "tsadv_PortalFeedbackQuestions")
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
package kz.uco.tsadv.modules.learning.model.feedback;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@PublishEntityChangedEvents
@NamePattern("%s|name")
@Table(name = "TSADV_LEARNING_FEEDBACK_TEMPLATE")
@Entity(name = "tsadv$LearningFeedbackTemplate")
public class LearningFeedbackTemplate extends AbstractParentEntity {
    private static final long serialVersionUID = -1038099842411767858L;

    @NotNull
    @Column(name = "NAME", nullable = false, length = 500)
    protected String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "feedbackTemplate")
    protected List<LearningFeedbackTemplateQuestion> templateQuestions;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @NotNull
    @Column(name = "USAGE_TYPE", nullable = false)
    protected String usageType;

    @NotNull
    @Column(name = "EMPLOYEE", nullable = false)
    protected Boolean employee = false;

    @NotNull
    @Column(name = "MANAGER", nullable = false)
    protected Boolean manager = false;

    @NotNull
    @Column(name = "TRAINER", nullable = false)
    protected Boolean trainer = false;

    public void setTemplateQuestions(List<LearningFeedbackTemplateQuestion> templateQuestions) {
        this.templateQuestions = templateQuestions;
    }

    public List<LearningFeedbackTemplateQuestion> getTemplateQuestions() {
        return templateQuestions;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setUsageType(LearningFeedbackUsageType usageType) {
        this.usageType = usageType == null ? null : usageType.getId();
    }

    public LearningFeedbackUsageType getUsageType() {
        return usageType == null ? null : LearningFeedbackUsageType.fromId(usageType);
    }

    public void setEmployee(Boolean employee) {
        this.employee = employee;
    }

    public Boolean getEmployee() {
        return employee;
    }

    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    public Boolean getManager() {
        return manager;
    }

    public void setTrainer(Boolean trainer) {
        this.trainer = trainer;
    }

    public Boolean getTrainer() {
        return trainer;
    }


}
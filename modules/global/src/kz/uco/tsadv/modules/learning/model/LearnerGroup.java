package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Группа учащихся
 */
@NamePattern("%s|code")
@Table(name = "TSADV_LEARNER_GROUP")
@Entity(name = "tsadv$LearnerGroup")
public class LearnerGroup extends StandardEntity {
    private static final long serialVersionUID = -6958075217277089015L;

    public static final String PARAMETER_LEARNER_GROUP = "learnerGroup";    // Параметр экрана: Группа учащихся

    @NotNull
    @Column(name = "CODE", nullable = false, length = 50)
    protected String code;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = true;

    @Column(name = "DESCRIPTION")
    protected String description;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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


}
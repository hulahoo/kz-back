package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.learning.dictionary.DicPortalFeedbackQuestion;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_PORTAL_FEEDBACK")
@Entity(name = "tsadv_PortalFeedback")
@NamePattern("%s|category")
public class PortalFeedback extends AbstractParentEntity {
    private static final long serialVersionUID = 5811365451358538679L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicPortalFeedbackQuestion category;

    @NotNull
    @Column(name = "EMAIL", nullable = false)
    protected String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DicPortalFeedbackQuestion getCategory() {
        return category;
    }

    public void setCategory(DicPortalFeedbackQuestion category) {
        this.category = category;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }
}
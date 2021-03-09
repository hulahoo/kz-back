package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.learning.dictionary.DicPortalFeedbackQuestion;

import javax.persistence.*;

@Table(name = "TSADV_PORTAL_FEEDBACK")
@Entity(name = "tsadv_PortalFeedback")
public class PortalFeedback extends AbstractParentEntity {
    private static final long serialVersionUID = 5811365451358538679L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicPortalFeedbackQuestion category;

    @Column(name = "EMAIL")
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
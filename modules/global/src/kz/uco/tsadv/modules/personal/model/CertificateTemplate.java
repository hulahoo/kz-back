package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.reports.entity.Report;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_CERTIFICATE_TEMPLATE")
@Entity(name = "tsadv_CertificateTemplate")
public class CertificateTemplate extends AbstractDictionary {
    private static final long serialVersionUID = 5432478836546413447L;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVING_TYPE_ID")
    private DicReceivingType receivingType;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    private OrganizationGroupExt organization;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LANGUAGE_ID")
    private DicLanguage language;

    @NotNull
    @Column(name = "SHOW_SALARY", nullable = false)
    private Boolean showSalary = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SIGNER_ID")
    private PersonGroupExt signer;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private Report report;

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public PersonGroupExt getSigner() {
        return signer;
    }

    public void setSigner(PersonGroupExt signer) {
        this.signer = signer;
    }

    public Boolean getShowSalary() {
        return showSalary;
    }

    public void setShowSalary(Boolean showSalary) {
        this.showSalary = showSalary;
    }

    public DicLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DicLanguage language) {
        this.language = language;
    }

    public DicReceivingType getReceivingType() {
        return receivingType;
    }

    public void setReceivingType(DicReceivingType receivingType) {
        this.receivingType = receivingType;
    }
}
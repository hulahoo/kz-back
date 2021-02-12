package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.AppBeans;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicCertificateType;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@PublishEntityChangedEvents
@Table(name = "TSADV_CERTIFICATE_REQUEST")
@Entity(name = "tsadv_CertificateRequest")
@NamePattern("%s|requestNumber")
public class CertificateRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -2710520566356850633L;

    public static final String PROCESS_DEFINITION_KEY = "certificateRequest";

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CERTIFICATE_TYPE_ID")
    private DicCertificateType certificateType;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVING_TYPE_ID")
    private DicReceivingType receivingType;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LANGUAGE_ID")
    private DicLanguage language;

    @NotNull
    @Column(name = "SHOW_SALARY", nullable = false)
    private Boolean showSalary = false;

    @NotNull
    @Column(name = "NUMBER_OF_COPY", nullable = false)
    private Integer numberOfCopy;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public Integer getNumberOfCopy() {
        return numberOfCopy;
    }

    public void setNumberOfCopy(Integer numberOfCopy) {
        this.numberOfCopy = numberOfCopy;
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

    public DicCertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(DicCertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();

        CommonService commonService = AppBeans.get(CommonService.class);

        this.setShowSalary(false);
        this.setNumberOfCopy(1);
        this.setCertificateType(commonService.getEntity(DicCertificateType.class, "CERTIFICATE_OF_EMPLOYMENT"));
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}
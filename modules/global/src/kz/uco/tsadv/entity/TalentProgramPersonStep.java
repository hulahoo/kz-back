package kz.uco.tsadv.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;

@Table(name = "TSADV_TALENT_PROGRAM_PERSON_STEP")
@Entity(name = "tsadv$TalentProgramPersonStep")
public class TalentProgramPersonStep extends AbstractParentEntity {
    private static final long serialVersionUID = 841810766296732853L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DIC_TALENT_PROGRAM_STEP_ID")
    protected DicTalentProgramStep dicTalentProgramStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_REQUEST_ID")
    protected TalentProgramRequest talentProgramRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "ADDRESS_RU")
    protected String addressRu;

    @Column(name = "ADDRESS_EN")
    protected String addressEn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicTalentProgramRequestStatus status;

    @Column(name = "COMMENT_")
    protected String comment;

    @Column(name = "RESULT_")
    protected String result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setTalentProgramRequest(TalentProgramRequest talentProgramRequest) {
        this.talentProgramRequest = talentProgramRequest;
    }

    public TalentProgramRequest getTalentProgramRequest() {
        return talentProgramRequest;
    }

    public void setAddressRu(String addressRu) {
        this.addressRu = addressRu;
    }

    public String getAddressRu() {
        return addressRu;
    }

    public void setAddressEn(String addressEn) {
        this.addressEn = addressEn;
    }

    public String getAddressEn() {
        return addressEn;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public DicTalentProgramRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DicTalentProgramRequestStatus status) {
        this.status = status;
    }

    public void setDicTalentProgramStep(DicTalentProgramStep dicTalentProgramStep) {
        this.dicTalentProgramStep = dicTalentProgramStep;
    }

    public DicTalentProgramStep getDicTalentProgramStep() {
        return dicTalentProgramStep;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

}
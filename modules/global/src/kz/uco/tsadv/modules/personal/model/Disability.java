package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicDisabilityType;
import kz.uco.tsadv.modules.personal.dictionary.DicDuration;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Listeners("tsadv_DisabilityListener")
@Table(name = "TSADV_DISABILITY")
@Entity(name = "tsadv$Disability")
public class Disability extends AbstractParentEntity {
    private static final long serialVersionUID = -1794672830174013482L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISABILITY_TYPE_ID")
    protected DicDisabilityType disabilityType;

    @Column(name = "ATTACHMENT_NAME")
    protected String attachmentName;

    @Column(name = "ATTACHMENT")
    protected byte[] attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DURATION_ID")
    protected DicDuration duration;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_EXT_ID")
    protected PersonGroupExt personGroupExt;

    @Column(name = "HAVE_DISABILITY")
    private String haveDisability;

    @Column(name = "GROUP_", length = 2000)
    private String group;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    public Date getEndDateHistory() {
        return endDateHistory;
    }

    public void setEndDateHistory(Date endDateHistory) {
        this.endDateHistory = endDateHistory;
    }

    public Date getStartDateHistory() {
        return startDateHistory;
    }

    public void setStartDateHistory(Date startDateHistory) {
        this.startDateHistory = startDateHistory;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public YesNoEnum getHaveDisability() {
        return haveDisability == null ? null : YesNoEnum.fromId(haveDisability);
    }

    public void setHaveDisability(YesNoEnum haveDisability) {
        this.haveDisability = haveDisability == null ? null : haveDisability.getId();
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentName() {
        return attachmentName;
    }


    public void setPersonGroupExt(PersonGroupExt personGroupExt) {
        this.personGroupExt = personGroupExt;
    }

    public PersonGroupExt getPersonGroupExt() {
        return personGroupExt;
    }


    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public byte[] getAttachment() {
        return attachment;
    }


    public void setDisabilityType(DicDisabilityType disabilityType) {
        this.disabilityType = disabilityType;
    }

    public DicDisabilityType getDisabilityType() {
        return disabilityType;
    }

    public void setDuration(DicDuration duration) {
        this.duration = duration;
    }

    public DicDuration getDuration() {
        return duration;
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


}
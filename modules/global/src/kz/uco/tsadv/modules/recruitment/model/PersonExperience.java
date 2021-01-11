package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicIndustry;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s (%s - %s)|company,startMonth,endMonth")
@Table(name = "TSADV_PERSON_EXPERIENCE")
@Entity(name = "tsadv$PersonExperience")
public class PersonExperience extends AbstractParentEntity {
    private static final long serialVersionUID = -5431966919596389466L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "UNTIL_NOW")
    protected Boolean untilNow;

    @Column(name = "COMPANY", nullable = false)
    protected String company;

    @Column(name = "JOB", nullable = false)
    protected String job;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_MONTH", nullable = false)
    protected Date startMonth;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_MONTH")
    protected Date endMonth;

    @Column(name = "DESCRIPTION", length = 4000)
    protected String description;

    @Column(name = "LOCATION", length = 2000)
    private String location;

    @Column(name = "PART_TIME")
    private Boolean partTime;

    @Column(name = "MINING_EXPERIENCE")
    private Boolean miningExperience;

    @Column(name = "GROUP_EXPERIENCE")
    private Boolean groupExperience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDUSTRY_ID")
    private DicIndustry industry;

    @Column(name = "YEARS")
    private Integer years;

    @Column(name = "MONTHS")
    private Integer months;

    @Column(name = "DAYS")
    private Integer days;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXPERIENCE_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXPERIENCE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

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

    public DicIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(DicIndustry industry) {
        this.industry = industry;
    }

    public Boolean getGroupExperience() {
        return groupExperience;
    }

    public void setGroupExperience(Boolean groupExperience) {
        this.groupExperience = groupExperience;
    }

    public Boolean getMiningExperience() {
        return miningExperience;
    }

    public void setMiningExperience(Boolean miningExperience) {
        this.miningExperience = miningExperience;
    }

    public Boolean getPartTime() {
        return partTime;
    }

    public void setPartTime(Boolean partTime) {
        this.partTime = partTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUntilNow(Boolean untilNow) {
        this.untilNow = untilNow;
    }

    public Boolean getUntilNow() {
        return untilNow;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setStartMonth(Date startMonth) {
        this.startMonth = startMonth;
    }

    public Date getStartMonth() {
        return startMonth;
    }

    public void setEndMonth(Date endMonth) {
        this.endMonth = endMonth;
    }

    public Date getEndMonth() {
        return endMonth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
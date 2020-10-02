package kz.uco.tsadv.entity.dbview;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.group.JobGroup;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "AA_JOB_VW")
@Entity(name = "tsadv$JobSsView")
public class JobSsView extends StandardEntity {
    private static final long serialVersionUID = -3976179294566694657L;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @Column(name = "JOB_NAME_RU")
    protected String jobNameRu;

    @Column(name = "JOB_NAME_KZ")
    protected String jobNameKz;

    @Column(name = "JOB_NAME_EN")
    protected String jobNameEn;

    @Transient
    @MetaProperty(related = {"jobNameRu", "jobNameKz", "jobNameEn"})
    protected String jobName;

    @Column(name = "MAX_START_DATE")
    protected String maxStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_CATEGORY_ID")
    protected DicEmployeeCategory employeeCategory;

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 1:
                    return jobNameKz;
                case 2:
                    return jobNameEn;
                default:
                    return jobNameRu;
            }
        }

        return jobName;
    }

    public void setEmployeeCategory(DicEmployeeCategory employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public DicEmployeeCategory getEmployeeCategory() {
        return employeeCategory;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setJobNameRu(String jobNameRu) {
        this.jobNameRu = jobNameRu;
    }

    public String getJobNameRu() {
        return jobNameRu;
    }

    public void setJobNameKz(String jobNameKz) {
        this.jobNameKz = jobNameKz;
    }

    public String getJobNameKz() {
        return jobNameKz;
    }

    public void setJobNameEn(String jobNameEn) {
        this.jobNameEn = jobNameEn;
    }

    public String getJobNameEn() {
        return jobNameEn;
    }

    public void setMaxStartDate(String maxStartDate) {
        this.maxStartDate = maxStartDate;
    }

    public String getMaxStartDate() {
        return maxStartDate;
    }


}
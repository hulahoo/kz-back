package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;

@MetaClass(name = "tsadv$ForMassEnrollment")
public class ForMassEnrollment extends BaseUuidEntity {
    private static final long serialVersionUID = 2654673093057221179L;

    @MetaProperty
    protected Course course;

    @MetaProperty
    protected OrganizationGroupExt organization;

    @MetaProperty
    protected Boolean check;

    @MetaProperty
    protected PositionGroupExt position;

    @MetaProperty
    protected JobGroup job;

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }


    public JobGroup getJob() {
        return job;
    }

    public void setJob(JobGroup job) {
        this.job = job;
    }


    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setPosition(PositionGroupExt position) {
        this.position = position;
    }

    public PositionGroupExt getPosition() {
        return position;
    }


}
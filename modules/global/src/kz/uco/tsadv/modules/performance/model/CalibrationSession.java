package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_CALIBRATION_SESSION")
@Entity(name = "tsadv$CalibrationSession")
public class CalibrationSession extends AbstractParentEntity {
    private static final long serialVersionUID = -3600597433931808615L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ADMINISTRATOR_ID")
    protected PersonGroupExt administrator;

    @Column(name = "STATUS")
    protected String status;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "calibrationSession")
    protected List<CalibrationComission> comissions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEMPLATE_ID")
    protected kz.uco.tsadv.modules.performance.model.AssessmentTemplate template;

    public void setTemplate(kz.uco.tsadv.modules.performance.model.AssessmentTemplate template) {
        this.template = template;
    }

    public AssessmentTemplate getTemplate() {
        return template;
    }


    public void setComissions(List<CalibrationComission> comissions) {
        this.comissions = comissions;
    }

    public List<CalibrationComission> getComissions() {
        return comissions;
    }


    public PersonGroupExt getAdministrator() {
        return administrator;
    }

    public void setAdministrator(PersonGroupExt administrator) {
        this.administrator = administrator;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}
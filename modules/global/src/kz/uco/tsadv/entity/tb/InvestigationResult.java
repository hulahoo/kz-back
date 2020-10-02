package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.entity.tb.dictionary.InvestigationType;
import java.util.UUID;

@Table(name = "TSADV_INVESTIGATION_RESULT")
@Entity(name = "tsadv$InvestigationResult")
public class InvestigationResult extends StandardEntity {
    private static final long serialVersionUID = 2964954008992971949L;

    @Temporal(TemporalType.DATE)
    @Column(name = "INVESTIGATION_DATE")
    protected Date investigationDate;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INVESTIGATION_TYPE_ID")
    protected InvestigationType investigationType;

    @Column(name = "PRODUCTION_CONNECTION")
    protected Boolean productionConnection;

    @Column(name = "EMPLOYEE_GUILT")
    protected Long employeeGuilt;

    @Column(name = "EMPLOYERGUILT")
    protected Long employerguilt;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDEN_INJURED_ID")
    protected AccidenInjured accidenInjured;

    public void setAccidenInjured(AccidenInjured accidenInjured) {
        this.accidenInjured = accidenInjured;
    }

    public AccidenInjured getAccidenInjured() {
        return accidenInjured;
    }


    public void setInvestigationType(InvestigationType investigationType) {
        this.investigationType = investigationType;
    }

    public InvestigationType getInvestigationType() {
        return investigationType;
    }


    public void setInvestigationDate(Date investigationDate) {
        this.investigationDate = investigationDate;
    }

    public Date getInvestigationDate() {
        return investigationDate;
    }

    public void setProductionConnection(Boolean productionConnection) {
        this.productionConnection = productionConnection;
    }

    public Boolean getProductionConnection() {
        return productionConnection;
    }

    public void setEmployeeGuilt(Long employeeGuilt) {
        this.employeeGuilt = employeeGuilt;
    }

    public Long getEmployeeGuilt() {
        return employeeGuilt;
    }

    public void setEmployerguilt(Long employerguilt) {
        this.employerguilt = employerguilt;
    }

    public Long getEmployerguilt() {
        return employerguilt;
    }


}
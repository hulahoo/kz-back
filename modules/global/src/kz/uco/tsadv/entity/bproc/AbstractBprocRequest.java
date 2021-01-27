package kz.uco.tsadv.entity.bproc;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.BeanLocator;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractBprocRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -1908181720688177085L;

    public static final String OUTCOME_APPROVE = "APPROVE";

    public static final String OUTCOME_REJECT = "REJECT";

    public static final String OUTCOME_REVISION = "REVISION";

    public static final String OUTCOME_REASSIGN = "REASSIGN";

    @Column(name = "REQUEST_NUMBER")
    protected Long requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE")
    protected Date requestDate;

    @Column(name = "COMMENT_", length = 3000)
    protected String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicRequestStatus getStatus() {
        return status;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    @PostConstruct
    public void postConstruct() {
        BeanLocator beanLocator = AppBeans.get(BeanLocator.class);

        this.setStatus(beanLocator.get(CommonService.class).getEntity(DicRequestStatus.class, "DRAFT"));
        this.setRequestNumber(beanLocator.get(EmployeeNumberService.class).generateNextRequestNumber());
        this.setRequestDate(new Date());
    }

    public abstract String getProcessDefinitionKey();
}
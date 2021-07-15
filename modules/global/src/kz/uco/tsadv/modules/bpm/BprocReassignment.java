package kz.uco.tsadv.modules.bpm;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_BPROC_REASSIGNMENT")
@Entity(name = "tsadv_BprocReassignment")
public class BprocReassignment extends StandardEntity {
    private static final long serialVersionUID = -2723262548178019117L;

    @Column(name = "EXECUTION_ID", nullable = false)
    @NotNull
    private String executionId;

    @Column(name = "TASK_DEFINITION_KEY", nullable = false)
    @NotNull
    private String taskDefinitionKey;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "START_TIME", nullable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME", nullable = false)
    @NotNull
    private Date endTime;

    @Column(name = "COMMENT_", length = 2000)
    private String comment;

    @NotNull
    @Column(name = "OUTCOME", nullable = false, length = 50)
    private String outcome;

    @Column(name = "ORDER_")
    private Integer order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNEE_ID")
    private TsadvUser assignee;

    @Column(name = "PROCESS_INSTANCE_ID", nullable = false, length = 50)
    @NotNull
    private String processInstanceId;

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public TsadvUser getAssignee() {
        return assignee;
    }

    public void setAssignee(TsadvUser assignee) {
        this.assignee = assignee;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
package kz.uco.tsadv.pojo.kpi;

import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AssignedPerformancePlanListPojo implements Serializable {
    private UUID id;
    private String performancePlanName;
    private Date startDate;
    private Date endDate;
    private CardStatusEnum status;
    private String statusName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public CardStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CardStatusEnum status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPerformancePlanName() {
        return performancePlanName;
    }

    public void setPerformancePlanName(String performancePlanName) {
        this.performancePlanName = performancePlanName;
    }

    public static final class AssignedPerformancePlanListPojoBuilder {
        private AssignedPerformancePlanListPojo assignedPerformancePlanListPojo;

        private AssignedPerformancePlanListPojoBuilder() {
            assignedPerformancePlanListPojo = new AssignedPerformancePlanListPojo();
        }

        public static AssignedPerformancePlanListPojoBuilder anAssignedPerformancePlanListPojo() {
            return new AssignedPerformancePlanListPojoBuilder();
        }

        public AssignedPerformancePlanListPojoBuilder id(UUID id) {
            assignedPerformancePlanListPojo.setId(id);
            return this;
        }

        public AssignedPerformancePlanListPojoBuilder performancePlanName(String performancePlanName) {
            assignedPerformancePlanListPojo.setPerformancePlanName(performancePlanName);
            return this;
        }

        public AssignedPerformancePlanListPojoBuilder startDate(Date startDate) {
            assignedPerformancePlanListPojo.setStartDate(startDate);
            return this;
        }

        public AssignedPerformancePlanListPojoBuilder endDate(Date endDate) {
            assignedPerformancePlanListPojo.setEndDate(endDate);
            return this;
        }

        public AssignedPerformancePlanListPojoBuilder status(CardStatusEnum status) {
            assignedPerformancePlanListPojo.setStatus(status);
            return this;
        }

        public AssignedPerformancePlanListPojoBuilder statusName(String statusName) {
            assignedPerformancePlanListPojo.setStatusName(statusName);
            return this;
        }

        public AssignedPerformancePlanListPojo build() {
            return assignedPerformancePlanListPojo;
        }
    }
}

package kz.uco.tsadv.pojo.kpi;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AssignedPerformancePlanPojo implements Serializable {
    private UUID id;
    private Date startDate;
    private Date endDate;
    private String assignedPersonName;
    private String jobGroup;
    private String organizationName;
    private String gradeGroup;
    private String assignedByName;

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

    public String getAssignedPersonName() {
        return assignedPersonName;
    }

    public void setAssignedPersonName(String assignedPersonName) {
        this.assignedPersonName = assignedPersonName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(String gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public static final class AssignedPerformancePlanPojoBuilder {
        private UUID id;
        private Date startDate;
        private Date endDate;
        private String assignedPersonName;
        private String jobGroup;
        private String organizationName;
        private String gradeGroup;
        private String assignedByName;

        private AssignedPerformancePlanPojoBuilder() {
        }

        public static AssignedPerformancePlanPojoBuilder anAssignedPerformancePlanPojo() {
            return new AssignedPerformancePlanPojoBuilder();
        }

        public AssignedPerformancePlanPojoBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder assignedPersonName(String assignedPersonName) {
            this.assignedPersonName = assignedPersonName;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder jobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder gradeGroup(String gradeGroup) {
            this.gradeGroup = gradeGroup;
            return this;
        }

        public AssignedPerformancePlanPojoBuilder assignedByName(String assignedByName) {
            this.assignedByName = assignedByName;
            return this;
        }

        public AssignedPerformancePlanPojo build() {
            AssignedPerformancePlanPojo assignedPerformancePlanPojo = new AssignedPerformancePlanPojo();
            assignedPerformancePlanPojo.gradeGroup = this.gradeGroup;
            assignedPerformancePlanPojo.id = this.id;
            assignedPerformancePlanPojo.endDate = this.endDate;
            assignedPerformancePlanPojo.jobGroup = this.jobGroup;
            assignedPerformancePlanPojo.assignedPersonName = this.assignedPersonName;
            assignedPerformancePlanPojo.organizationName = this.organizationName;
            assignedPerformancePlanPojo.startDate = this.startDate;
            assignedPerformancePlanPojo.assignedByName = this.assignedByName;
            return assignedPerformancePlanPojo;
        }
    }
}

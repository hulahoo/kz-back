package kz.uco.tsadv.pojo;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class PositionRequestSaveModel extends RequestDetailSaveModel {

    private UUID positionGroupId;
    private UUID gradeGroupId;
    private BigDecimal headCount;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;

    public UUID getPositionGroupId() {
        return positionGroupId;
    }

    public void setPositionGroupId(UUID positionGroupId) {
        this.positionGroupId = positionGroupId;
    }

    public UUID getGradeGroupId() {
        return gradeGroupId;
    }

    public void setGradeGroupId(UUID gradeGroupId) {
        this.gradeGroupId = gradeGroupId;
    }

    public BigDecimal getHeadCount() {
        return headCount;
    }

    public void setHeadCount(BigDecimal headCount) {
        this.headCount = headCount;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }
}

package kz.uco.tsadv.pojo;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class PositionRequestSaveModel extends RequestSaveModel {

    private UUID positionGroupId;
    private UUID gradeGroupId;
    private BigDecimal headCount;

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
}

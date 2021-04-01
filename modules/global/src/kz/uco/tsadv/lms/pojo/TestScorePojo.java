package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class TestScorePojo implements Serializable {
    protected BigDecimal score = BigDecimal.ZERO;
    protected BigDecimal maxScore = BigDecimal.ZERO;
    protected Boolean success = false;

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
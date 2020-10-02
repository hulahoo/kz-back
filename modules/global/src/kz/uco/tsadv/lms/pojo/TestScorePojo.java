package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class TestScorePojo implements Serializable {
    protected Integer score = 0;
    protected Integer maxScore = 0;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
}
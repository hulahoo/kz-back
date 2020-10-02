package kz.uco.tsadv.api;

import java.io.Serializable;

/**
 * @param passed - признак, сдал тест или нет
 * @param score  - количество набранных баллов
 * @author adilbekov.yernar
 */
@SuppressWarnings("all")
public class TestResult implements Serializable {

    private boolean passed;
    private String score;

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

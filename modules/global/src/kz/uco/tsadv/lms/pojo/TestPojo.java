package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class TestPojo implements Serializable {
    protected UUID attemptId;
    protected Integer timer;
    protected List<TestSectionPojo> testSections;

    public UUID getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(UUID attemptId) {
        this.attemptId = attemptId;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public List<TestSectionPojo> getTestSections() {
        return testSections;
    }

    public void setTestSections(List<TestSectionPojo> testSections) {
        this.testSections = testSections;
    }
}

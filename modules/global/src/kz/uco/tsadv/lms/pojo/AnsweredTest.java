package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class AnsweredTest implements Serializable {

    protected String attemptId;

    protected List<AttemptTestSectionPojo> testSections;

    public List<AttemptTestSectionPojo> getTestSections() {
        return testSections;
    }

    public void setTestSections(List<AttemptTestSectionPojo> testSections) {
        this.testSections = testSections;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }


}

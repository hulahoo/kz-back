package kz.uco.tsadv.web.modules.learning.course;

import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;

import java.util.List;

/**
 * @author Adilbekov Yernar
 */
public class TestSectionQuestion {

    private TestSection testSection;
    private List<Question> questions;

    public TestSection getTestSection() {
        return testSection;
    }

    public void setTestSection(TestSection testSection) {
        this.testSection = testSection;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}

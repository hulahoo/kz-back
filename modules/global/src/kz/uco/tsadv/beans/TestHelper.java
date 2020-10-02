package kz.uco.tsadv.beans;

import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;
import kz.uco.tsadv.modules.learning.model.TestSection;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static kz.uco.base.common.Null.nullReplace;

@Component(TestHelper.NAME)
public class TestHelper {
    public static final String NAME = "tsadv_TestHelper";

    @Nullable
    public Integer calculateTestResultPercent(@Nonnull CourseSectionAttempt courseSectionAttempt) {
        Integer maxScore = 0;

        if (courseSectionAttempt.getTest() == null) return null;

        for (TestSection testSection : courseSectionAttempt.getTest().getSections()) {
            for (QuestionInSection questionInSection : testSection.getQuestions()) {
                Question question = questionInSection.getQuestion();
                if (question != null) {
                    maxScore += nullReplace(question.getScore(), 0);
                }
            }
        }

        if (maxScore != 0) {
            return nullReplace(courseSectionAttempt.getTestResult(), 0) * 100 / maxScore;
        } else {
            return null;
        }
    }

}

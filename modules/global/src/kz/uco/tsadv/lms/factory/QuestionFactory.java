package kz.uco.tsadv.lms.factory;

import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface QuestionFactory {
    void checkQuestion(@NotNull Question question, @NotNull PersonAnswer answer, @NotNull TestSection testSection, List<Answer> answers);
}

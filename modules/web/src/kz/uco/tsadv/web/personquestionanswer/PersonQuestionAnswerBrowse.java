package kz.uco.tsadv.web.personquestionanswer;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.PersonQuestionAnswer;
import kz.uco.tsadv.modules.recognition.RcgQuestion;
import kz.uco.tsadv.modules.recognition.RcgQuestionAnswer;

import javax.inject.Inject;

public class PersonQuestionAnswerBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    public Component generateAnswer(PersonQuestionAnswer personQuestionAnswer) {
        RcgQuestion rcgQuestion = personQuestionAnswer.getQuestion();
        if (rcgQuestion != null) {
            RcgQuestionAnswer rcgQuestionAnswer = personQuestionAnswer.getAnswer();
            if (rcgQuestionAnswer != null) {
                Label label = componentsFactory.createComponent(Label.class);
                label.setHtmlEnabled(true);
                switch (rcgQuestion.getAnswerType()) {
                    case ICON: {
                        label.setValue(String.format(
                                "<img src=\"./dispatch/rcg_answer_icon/%s\" style=\"width:30px;height:30px;margin:5px 0\">",
                                rcgQuestionAnswer.getId().toString()));
                        break;
                    }
                    case RADIO: {
                        label.setValue(rcgQuestionAnswer.getText());
                    }
                }
                return label;
            }
        }
        return null;
    }
}
package kz.uco.tsadv.web.modules.learning.question.v72;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionBank;

import javax.inject.Inject;

@UiController("tsadv$Question.browse")
@UiDescriptor("question-browse.xml")
@LookupComponent("questionsTable")
@LoadDataBeforeShow
public class QuestionBrowse extends StandardLookup<Question> {

    @Inject
    protected CollectionLoader<Question> questionsDl;

    public void setBank(QuestionBank bank) {
        if (bank != null) questionsDl.setParameter("bank", bank.getId());
        else questionsDl.removeParameter("bank");
    }
}
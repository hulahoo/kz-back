package kz.uco.tsadv.web.modules.learning.questionbank;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.model.QuestionBank;

import javax.inject.Inject;
import java.util.Map;

public class QuestionBankEdit extends AbstractEditor<QuestionBank> {
    @Inject
    protected Datasource<QuestionBank> questionBankDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("item")){
            questionBankDs.setItem(((QuestionBank) params.get("item")));
        }
    }
}
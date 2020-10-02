package kz.uco.tsadv.web.modules.recognition.entity.rcgquestion;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import kz.uco.tsadv.modules.recognition.RcgQuestion;
import kz.uco.tsadv.modules.recognition.enums.RcgAnswerType;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RcgQuestionEdit extends AbstractEditor<RcgQuestion> {

    @Named("answersTable.create")
    protected CreateAction createAnswerAction;
    @Named("answersTable.edit")
    protected EditAction editAnswerAction;
    @Inject
    protected RecognitionService recognitionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        createAnswerAction.setWindowParamsSupplier(() -> ParamsMap.of("rcgAnswerType", getItem().getAnswerType()));
        editAnswerAction.setWindowParamsSupplier(() -> ParamsMap.of("rcgAnswerType", getItem().getAnswerType()));
    }

    @Override
    protected void initNewItem(RcgQuestion item) {
        super.initNewItem(item);
        item.setAnswerType(RcgAnswerType.ICON);
        item.setCoins(0L);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            if (BooleanUtils.isTrue(getItem().getActive())) {
                try {
                    recognitionService.resetActiveQuestions(getItem().getId(), true);
                } catch (Exception ex) {
                    showNotification(ex.getMessage());
                }
            }
        }
        return super.postCommit(committed, close);
    }
}
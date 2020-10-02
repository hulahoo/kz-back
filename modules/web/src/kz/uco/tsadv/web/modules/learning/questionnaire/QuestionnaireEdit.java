package kz.uco.tsadv.web.modules.learning.questionnaire;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.performance.model.Questionnaire;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;
import kz.uco.tsadv.modules.performance.model.QuestionnaireResultScale;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class QuestionnaireEdit extends AbstractEditor<Questionnaire> {
    @Inject
    private CollectionDatasource<QuestionnaireQuestion, UUID> questionDs;

    @Named("answerTable.create")
    private CreateAction answerTableCreate;
    @Inject
    private CollectionDatasource<QuestionnaireResultScale, UUID> scaleDs;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        answerTableCreate.setEnabled(false);
    }

    @Override
    protected void postInit() {
        super.postInit();
        questionDs.addItemChangeListener(event -> {
            if (event.getItem() == null) {
                answerTableCreate.setEnabled(false);
            } else {
                answerTableCreate.setEnabled(true);
            }
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        ArrayList<QuestionnaireResultScale> sortedScaleList =
                new ArrayList<>(scaleDs.getItems());
        sortedScaleList.sort((o1, o2) -> {
            if (o1.getMin() < o2.getMin()) return -1;
            if (o1.getMin() == o2.getMin()) return 0;
            return 1;
        });
        if (hasGapOrOverlap(sortedScaleList)) {
            errors.add(getMessage("error.has.gap.or.overlap"));
        }
        if (!minMaxEquals(sortedScaleList)) {
            errors.add(getMessage("error.min.not.equals"));
        }
        super.postValidate(errors);
    }

    protected boolean minMaxEquals(List<QuestionnaireResultScale> sortedScaleList) {
        /*Map<Integer, Object> paramsMap = new HashMap<>();
        StringBuilder paramsString = new StringBuilder();
        ArrayList<QuestionnaireQuestion> list = new ArrayList<>(questionDs.getItems());
        for (int i = 0; i < list.size(); i++) {
            paramsMap.put(i+1, list.get(i));
            paramsString.append("?" + i+1 + ",");
        }
        paramsString.delete(paramsString.length()-2, paramsString.length());
        List<Object[]> resultList = commonService.emNativeQueryResultList(
                "select min(a.score), max(a.score)" +
                "  from tsadv_questionnaire_question q" +
                "  join tsadv_question_answer a on q.id = a.question_id" +
                " where q.id in (" + paramsString + ")" +
                " group by q.id", paramsMap);
        Integer overalMin = 0;
        Integer overalMax = 0;
        if (resultList != null) {
            for (Object[] objects : resultList) {
                if (objects != null) {
                    overalMin += objects[0]==null ? 0 : (Integer)objects[0];
                    overalMax += objects[1]==null ? 0 : (Integer)objects[1];
                }
            }
        }
        if (sortedScaleList.size() > 0) {
            return sortedScaleList.get(0).getMin() == overalMin &&
                    sortedScaleList.get(sortedScaleList.size()-1).getMax() == overalMax;
        }TODO доделать валидацию uts-392 пункт 7*/
        return true;
    }

    protected boolean hasGapOrOverlap(List<QuestionnaireResultScale> sortedScaleList) {
        /*int prevMax = sortedScaleList.get(0).getMax();
        for (int i = 0; i < sortedScaleList.size(); i++) {
            if (i != 0) {
                if (sortedScaleList.get(i).getMin() - prevMax != 1) return true;
                prevMax = sortedScaleList.get(i).getMax();
            }
        }TODO доделать валидацию uts-392 пункт 7*/
        return false;
    }
}
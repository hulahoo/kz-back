package kz.uco.tsadv.web.modules.recruitment.rcanswer;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.OptionsGroup;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.enums.RcQuestionType;
import kz.uco.tsadv.modules.recruitment.model.RcAnswer;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class RcAnswerEdit extends AbstractEditor<RcAnswer> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private OptionsGroup positiveOptionsGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Map<String, Boolean> optionsMap = new HashMap<>();
        String p = getMessage("interview.questionnaire.multi.p");
        String n = getMessage("interview.questionnaire.multi.n");

        optionsMap.put(p, true);
        optionsMap.put(n, false);

        positiveOptionsGroup.setOptionsMap(optionsMap);

//        positiveOptionsGroup.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                boolean positive = e.getValue() != null && Boolean.parseBoolean(e.getValue().toString());
//                fieldGroup.getField("positiveField").setCaption(positive ? p : n);
//                getItem().setPositive(positive);
//            }
//        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (getItem().getQuestion() != null
                && getItem().getQuestion().getQuestionType() != null) {

            RcQuestionType rcQuestionType = getItem().getQuestion().getQuestionType();

            switch (rcQuestionType) {
                case SELECTION:
                    fieldGroup.getField("answerResult").setRequired(true);
                    break;
                case ASSESSMENT: {
                    fieldGroup.getField("answerResult").setEditable(false);
                    fieldGroup.getField("answerResult").setVisible(false);
                    break;
                }
                case COMPETENCE: {
                    fieldGroup.getField("answerResult").setVisible(false);

                    RcAnswerType rcAnswerType = getItem().getQuestion().getAnswerType();
                    if (rcAnswerType != null && rcAnswerType.equals(RcAnswerType.MULTI)) {
                        /**
                         * initialize value or default value. Default value is FALSE
                         * */
                        boolean positive = BooleanUtils.isTrue(getItem().getPositive());
                        positiveOptionsGroup.setValue(positive);

                        positiveOptionsGroup.setVisible(true);
                    }
                    break;
                }
            }
        }
    }
}
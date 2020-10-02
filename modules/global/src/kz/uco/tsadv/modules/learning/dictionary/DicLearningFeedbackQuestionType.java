package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_LEARNING_FEEDBACK_QUESTION_TYPE")
@Entity(name = "tsadv$DicLearningFeedbackQuestionType")
public class DicLearningFeedbackQuestionType extends AbstractDictionary {
    private static final long serialVersionUID = -1408549064529147280L;

}
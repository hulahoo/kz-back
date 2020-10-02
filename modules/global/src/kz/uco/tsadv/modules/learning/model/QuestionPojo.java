package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.List;

@MetaClass(name = "tsadv$QuestionPojo")
public class QuestionPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -4568964161459473762L;

    @MetaProperty
    protected String text;

    @MetaProperty
    protected List<AnswerPojo> answers;

    @MetaProperty
    protected Integer type;

    public void setAnswers(List<AnswerPojo> answers) {
        this.answers = answers;
    }

    public List<AnswerPojo> getAnswers() {
        return answers;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setType(QuestionType type) {
        this.type = type == null ? null : type.getId();
    }

    public QuestionType getType() {
        return type == null ? null : QuestionType.fromId(type);
    }


}
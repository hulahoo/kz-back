package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.List;

@MetaClass(name = "tsadv$RcgQuestionPojo")
public class RcgQuestionPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -6888330116488976466L;

    @MetaProperty
    protected String text;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected String type;

    @MetaProperty
    protected Long coins = 0L;

    @MetaProperty
    protected List<RcgQuestionAnswerPojo> answers;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setAnswers(List<RcgQuestionAnswerPojo> answers) {
        this.answers = answers;
    }

    public List<RcgQuestionAnswerPojo> getAnswers() {
        return answers;
    }


}
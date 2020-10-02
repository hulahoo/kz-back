package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$AnswerPojo")
public class AnswerPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -8787517468368644104L;

    @MetaProperty
    protected String answer;

    @NotNull
    @MetaProperty(mandatory = true)
    protected Boolean checked = false;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }


}
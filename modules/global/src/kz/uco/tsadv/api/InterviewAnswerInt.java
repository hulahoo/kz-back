package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.api.AbstractEntityInt;

import java.util.UUID;

@MetaClass(name = "tsadv$InterviewAnswerInt")
public class InterviewAnswerInt extends AbstractEntityInt {
    private static final long serialVersionUID = -7171919729160951320L;

    @MetaProperty
    protected Boolean checked;

    @MetaProperty
    protected UUID answerId;

    public UUID getAnswerId() {
        return answerId;
    }

    public void setAnswerId(UUID answerId) {
        this.answerId = answerId;
    }



    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }


}
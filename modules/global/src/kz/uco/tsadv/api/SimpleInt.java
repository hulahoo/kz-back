package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

/**
 * Результат true/false с сообщением об ошибке
 */
@MetaClass(name = "tsadv$SimpleInt")
public class SimpleInt extends AbstractEntityInt {
    private static final long serialVersionUID = -7731188391778768328L;

    @MetaProperty
    protected String result;

    @MetaProperty
    protected String errorMessage;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }


}
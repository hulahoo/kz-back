package kz.uco.tsadv.api;

import java.io.Serializable;

/**
 * Результат выполения
 *
 * @author veronika.buksha
 */
public class BaseResult implements Serializable {

    /**
     * Успешно ли (true / false)
     */
    private boolean success;

    /**
     * Сообщение при успехе
     */
    private String successMessage;

    /**
     * Сообщение при ошибке
     */
    private String errorMessage;



    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

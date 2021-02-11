package kz.uco.tsadv.pojo;

import java.io.Serializable;

/**
 * @author Alibek Berdaulet
 */
public class ExceptionResponse implements Serializable {

    private int errorStatus;
    private String message;

    public ExceptionResponse(int errorStatus, String message) {
        this.errorStatus = errorStatus;
        this.message = message;
    }

    public int getError() {
        return errorStatus;
    }

    public void setError(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

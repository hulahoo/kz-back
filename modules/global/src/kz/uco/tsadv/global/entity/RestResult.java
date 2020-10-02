package kz.uco.tsadv.global.entity;

import java.io.Serializable;

/**
 * @author adilbekov.yernar
 */
public class RestResult implements Serializable {

    private boolean success;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

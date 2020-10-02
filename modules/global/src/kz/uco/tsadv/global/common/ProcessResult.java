package kz.uco.tsadv.global.common;

import kz.uco.tsadv.global.enums.CompletionStatus;

/**
 * Result of some operation, action, or process
 * @author Felix Kamalov
 * @version 1.0
 */
public class ProcessResult {

    protected CompletionStatus status = CompletionStatus.SUCCESS;

    protected String message;

    protected String details;

    public boolean isSuccess() {
        if (status == CompletionStatus.SUCCESS) {
            return true;
        }
        return false;
    }

    public CompletionStatus getStatus() {
        return status;
    }

    public void setStatus(CompletionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

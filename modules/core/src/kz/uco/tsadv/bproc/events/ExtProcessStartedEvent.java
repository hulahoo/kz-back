package kz.uco.tsadv.bproc.events;

import com.haulmont.addon.bproc.events.ProcessStartedEvent;

/**
 * @author Alibek Berdaulet
 */
public class ExtProcessStartedEvent extends ProcessStartedEvent {

    protected String executionId;

    public ExtProcessStartedEvent(Object source) {
        super(source);
    }

    public ExtProcessStartedEvent withExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getExecutionId() {
        return executionId;
    }
}

package kz.uco.tsadv.bproc.events;

import com.haulmont.addon.bproc.events.ProcessStartedEvent;

import java.util.Map;

/**
 * @author Alibek Berdaulet
 */
public class ExtProcessStartedEvent extends ProcessStartedEvent {

    protected Map<String, Object> variables;

    public ExtProcessStartedEvent(Object source) {
        super(source);
    }

    public ExtProcessStartedEvent withVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}

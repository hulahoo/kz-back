package kz.uco.tsadv.web.modules.events;

import com.haulmont.addon.globalevents.GlobalApplicationEvent;
import com.haulmont.addon.globalevents.GlobalUiEvent;
import org.springframework.context.ApplicationEvent;

public class RcgOrdersEvent extends ApplicationEvent implements GlobalUiEvent {

    protected int count;

    public RcgOrdersEvent(Object source, int count) {
        super(source);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

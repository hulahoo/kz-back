package kz.uco.tsadv.components;

import com.haulmont.cuba.gui.components.mainwindow.SideMenu;
import kz.uco.tsadv.web.modules.events.RcgOrdersEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class RcgEventBean {

    protected int count;

    protected SideMenu.MenuItem ordersMenuItem;

    @Order(15)
    @EventListener
    public void onTestMethod1(RcgOrdersEvent event) {
        this.count = event.getCount();
        if (count > 0) getOrdersMenuItem().setBadgeText("+" + count + " NEW");
        else getOrdersMenuItem().setBadgeText("");
    }

    public void setOrdersMenuItem(SideMenu.MenuItem ordersMenuItem) {
        this.ordersMenuItem = ordersMenuItem;
    }

    public SideMenu.MenuItem getOrdersMenuItem() {
        return ordersMenuItem;
    }
}

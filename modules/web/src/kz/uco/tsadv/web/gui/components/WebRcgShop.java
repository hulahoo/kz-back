package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.RcgShop;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public class WebRcgShop extends WebAbstractComponent<kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent.RcgShopComponent> implements RcgShop {
    public WebRcgShop() {
        this.component = new kz.uco.tsadv.web.toolkit.ui.rcgshopcomponent.RcgShopComponent();
    }
}
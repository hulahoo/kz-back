package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.RcgWall;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public class WebRcgWall extends WebAbstractComponent<kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent.RcgWallComponent> implements RcgWall {
    public WebRcgWall() {
        this.component = new kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent.RcgWallComponent();
    }
}
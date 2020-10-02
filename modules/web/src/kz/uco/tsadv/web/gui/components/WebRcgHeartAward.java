package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.RcgHeartAward;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public class WebRcgHeartAward extends WebAbstractComponent<kz.uco.tsadv.web.toolkit.ui.rcgheartawardcomponent.RcgHeartAwardComponent> implements RcgHeartAward {
    public WebRcgHeartAward() {
        this.component = new kz.uco.tsadv.web.toolkit.ui.rcgheartawardcomponent.RcgHeartAwardComponent();
    }
}
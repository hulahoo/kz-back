package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.RcgTeam;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;

public class WebRcgTeam extends WebAbstractComponent<kz.uco.tsadv.web.toolkit.ui.rcgteamcomponent.RcgTeamComponent> implements RcgTeam {
    public WebRcgTeam() {
        this.component = new kz.uco.tsadv.web.toolkit.ui.rcgteamcomponent.RcgTeamComponent();
    }
}
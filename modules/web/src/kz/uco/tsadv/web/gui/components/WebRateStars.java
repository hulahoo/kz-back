package kz.uco.tsadv.web.gui.components;

import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.gui.components.RateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

public class WebRateStars extends WebAbstractComponent<RateStarsComponent> implements RateStars {
    public WebRateStars() {
        this.component = new RateStarsComponent();
    }
}
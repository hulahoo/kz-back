package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.FontRateStars;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.web.toolkit.ui.fontratestarscomponent.FontRateStarsComponent;

public class WebFontRateStars extends WebAbstractComponent<FontRateStarsComponent> implements FontRateStars {
    public WebFontRateStars() {
        this.component = new FontRateStarsComponent();
    }
}
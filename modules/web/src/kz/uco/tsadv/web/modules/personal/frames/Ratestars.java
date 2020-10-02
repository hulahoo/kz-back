package kz.uco.tsadv.web.modules.personal.frames;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.inject.Inject;
import java.util.Map;

public class Ratestars extends AbstractWindow {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        WebRateStars webRateStars = componentsFactory.createComponent(WebRateStars.class);
        RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
        rateStars.setValue(2.4);
        rateStars.setReadOnly(true);
        rateStars.setListener(newValue -> {
            showNotification("rateValue: " + newValue);
        });

        fieldGroup.getFieldNN("rate").setComponent(webRateStars);
    }
}
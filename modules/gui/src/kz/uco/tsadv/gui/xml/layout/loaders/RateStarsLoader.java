package kz.uco.tsadv.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;
import kz.uco.tsadv.gui.components.RateStars;

public class RateStarsLoader extends AbstractComponentLoader<RateStars> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(RateStars.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

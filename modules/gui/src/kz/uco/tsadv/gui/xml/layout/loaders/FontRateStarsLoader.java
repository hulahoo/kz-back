package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.FontRateStars;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class FontRateStarsLoader extends AbstractComponentLoader<FontRateStars> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(FontRateStars.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

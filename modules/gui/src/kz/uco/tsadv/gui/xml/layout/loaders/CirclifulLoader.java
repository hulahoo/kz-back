package kz.uco.tsadv.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;
import kz.uco.tsadv.gui.components.Circliful;

/**
 * @author Adilbekov Yernar
 */
public class CirclifulLoader extends AbstractComponentLoader<Circliful> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(Circliful.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

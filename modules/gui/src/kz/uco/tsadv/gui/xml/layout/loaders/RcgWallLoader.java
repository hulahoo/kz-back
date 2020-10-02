package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.RcgWall;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class RcgWallLoader extends AbstractComponentLoader<RcgWall> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(RcgWall.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

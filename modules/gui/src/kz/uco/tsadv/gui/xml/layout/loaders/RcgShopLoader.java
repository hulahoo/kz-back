package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.RcgShop;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class RcgShopLoader extends AbstractComponentLoader<RcgShop> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(RcgShop.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

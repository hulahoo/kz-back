package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.RcgHeartAward;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class RcgHeartAwardLoader extends AbstractComponentLoader<RcgHeartAward> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(RcgHeartAward.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

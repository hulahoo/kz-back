package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.ScrumBoard;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class ScrumBoardLoader extends AbstractComponentLoader<ScrumBoard> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(ScrumBoard.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.RcgTeam;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class RcgTeamLoader extends AbstractComponentLoader<RcgTeam> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(RcgTeam.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

package kz.uco.tsadv.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;
import kz.uco.tsadv.gui.components.Chart;

public class ChartLoader extends AbstractComponentLoader<Chart> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(Chart.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

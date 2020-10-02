package kz.uco.tsadv.web.gui.components;

import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.gui.components.Chart;
import kz.uco.tsadv.web.toolkit.ui.chart.ChartServerComponent;

public class WebChart extends WebAbstractComponent<ChartServerComponent> implements Chart {
    public WebChart() {
        this.component = new ChartServerComponent();
    }
}
package kz.uco.tsadv.web.gui.components;

import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.gui.components.Circliful;
import kz.uco.tsadv.web.toolkit.ui.circliful.CirclifulServerComponent;

/**
 * @author Adilbekov Yernar
 */
public class WebCircliful extends WebAbstractComponent<CirclifulServerComponent> implements Circliful {
    public WebCircliful() {
        this.component = new CirclifulServerComponent();
    }
}
package kz.uco.tsadv.web.gui.components;

import kz.uco.tsadv.gui.components.Matrix;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.tsadv.web.toolkit.ui.matrixcomponent.MatrixComponent;

public class WebMatrix extends WebAbstractComponent<MatrixComponent> implements Matrix {
    public WebMatrix() {
        this.component = new MatrixComponent();
    }
}
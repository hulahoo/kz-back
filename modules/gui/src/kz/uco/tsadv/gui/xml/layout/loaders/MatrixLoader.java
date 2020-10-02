package kz.uco.tsadv.gui.xml.layout.loaders;

import kz.uco.tsadv.gui.components.Matrix;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class MatrixLoader extends AbstractComponentLoader<Matrix> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(Matrix.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
    }
}

package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;

import java.util.Map;

/**
 * @author Adilbekov Yernar
 */
public abstract class EditableFrame extends AbstractFrame {

    protected ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);
    protected DataManager dataManager = AppBeans.get(DataManager.class);
    protected UserSession userSession = AppBeans.get(UserSession.class);

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initDatasource();
    }

    public void setInitialValues(Map<String, Object> initialValues) {
        initInitialValues(initialValues);
    }

    protected void initInitialValues(Map<String, Object> initialValues) {
    }

    public abstract void editable(boolean editable);

    public abstract void initDatasource();
}

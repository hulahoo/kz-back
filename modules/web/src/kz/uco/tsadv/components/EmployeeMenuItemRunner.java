package kz.uco.tsadv.components;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.builders.ScreenBuilder;
import com.haulmont.cuba.gui.config.MenuItem;
import com.haulmont.cuba.gui.config.MenuItemRunnable;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.tsadv.modules.recognition.exceptions.EmployeePersonGroupException;
import kz.uco.tsadv.modules.recognition.exceptions.ScreenException;
import kz.uco.tsadv.service.UserService;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class EmployeeMenuItemRunner implements MenuItemRunnable {

    @Override
    public void run(FrameOwner origin, MenuItem menuItem) {
        UserService userService = AppBeans.get(UserService.class);
        Element currentElement = menuItem.getDescriptor();
        if (userService.isEmployee()) {
            buildScreenAndShow(origin, getScreenId(currentElement), getScreenOpenType(currentElement));
        } else {
            throw new EmployeePersonGroupException();
        }
    }

    protected String getScreenId(Element currentElement) {
        Attribute menuItemIdAttribute = currentElement.attribute("id");
        Element[] paramsList = currentElement.elements("param")
                .stream()
                .filter(element -> {
                    Attribute nameAttribute = element.attribute("name");
                    return nameAttribute != null && nameAttribute.getValue().equals("screen");
                })
                .toArray(Element[]::new);

        if (paramsList.length == 0) {
            throw ScreenException.createNoScreenException(menuItemIdAttribute == null ? "" : menuItemIdAttribute.getValue());
        }
        if (paramsList.length > 1) {
            throw ScreenException.createSeveralScreensException();
        }
        Attribute screenIdAttribute = paramsList[0].attribute("value");
        if (screenIdAttribute != null) {
            return screenIdAttribute.getValue();
        } else {
            throw ScreenException.createNoScreenException(menuItemIdAttribute == null ? "" : menuItemIdAttribute.getValue());
        }
    }

    protected OpenMode getScreenOpenType(Element currentElement) {
        Attribute openTypeAttribute = currentElement.attribute("openType");
        if (openTypeAttribute != null) {
            return OpenMode.valueOf(openTypeAttribute.getValue());
        }
        return null;
    }

    protected void buildScreenAndShow(FrameOwner origin, String screenId, OpenMode openMode) {
        ScreenBuilders screenBuilders = AppBeans.get(ScreenBuilders.class);
        ScreenBuilder builder = screenBuilders.screen(origin)
                .withScreenId(screenId);
        if (openMode != null) {
            builder.withOpenMode(openMode);
        }
        builder.build()
                .show();
    }
}

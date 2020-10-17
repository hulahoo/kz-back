package kz.uco.tsadv.components;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.builders.ScreenBuilder;
import com.haulmont.cuba.gui.config.MenuItem;
import com.haulmont.cuba.gui.config.MenuItemRunnable;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.tsadv.service.UserService;

public class EmployeeMenuItemRunner implements MenuItemRunnable {
    @Override
    public void run(FrameOwner origin, MenuItem menuItem) {
        UserService userService = AppBeans.get(UserService.class);
        ScreenBuilders screenBuilders = AppBeans.get(ScreenBuilders.class);
        EmployeeCheckComponent checkComponent = AppBeans.get(EmployeeCheckComponent.class);
        if (checkComponent.checkIfEmployeeAndSendNotification()) {
            ScreenBuilder builder = screenBuilders.screen(origin)
                    .withScreenId(menuItem.getId());
            String openType = menuItem.getStylename();
            if (openType != null && !openType.isEmpty()) {
                builder.withOpenMode(OpenMode.valueOf(openType));
            }
            builder.build()
                    .show();
        }
    }
}

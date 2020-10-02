package kz.uco.tsadv.web.modules.personal.timecard;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowManagerProvider;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.server.Page;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.service.OrganizationService;

import java.net.MalformedURLException;
import java.net.URL;

public class TimecardWindowOpener implements Runnable {

    @Override
    public void run() {
        OrganizationService organizationService = AppBeans.get(OrganizationService.class);
        UserSession userSession = AppBeans.get(UserSession.class);
        Messages messages = AppBeans.get(Messages.class);
        WindowManager windowManager = AppBeans.get(WindowManagerProvider.class).get();
        String urlShort = "";
        try {
            URL url = Page.getCurrent().getLocation().toURL();
            String s = url.toString();
            urlShort = s.substring(0, s.length() - 2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (organizationService.getOrganizationsWhereUserIsHr((UserExt) userSession.getUser()).isEmpty()) {
            windowManager.showNotification(messages.getMessage(this.getClass(), "no.rights"), Frame.NotificationType.ERROR_HTML);
        } else {
            windowManager.showWebPage(urlShort + "open?screen=tsadv$Timecard.browse&openType=DIALOG", null);
        }
    }
}

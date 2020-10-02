package kz.uco.tsadv.web.modules.personal.timecard;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowManagerProvider;
import com.vaadin.server.Page;

import java.net.MalformedURLException;
import java.net.URL;

public class PaidinternTimecardWindowOpener implements Runnable {
    @Override
    public void run() {
        WindowManager windowManager = AppBeans.get(WindowManagerProvider.class).get();
        String urlShort = "";
        try {
            URL url = Page.getCurrent().getLocation().toURL();
            String s = url.toString();
            urlShort = s.substring(0, s.length() - 2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        windowManager.showWebPage(urlShort + "open?screen=tsadv$PaidinternTimecard.browse&openType=DIALOG", null);
    }
}

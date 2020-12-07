package kz.uco.tsadv.mixins;

import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.model.ScreenData;
import com.haulmont.cuba.gui.screen.Extensions;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.security.global.UserSession;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SelfServiceMixin {

    Pattern CONTAINER_REF_PATTERN = Pattern.compile(":(session\\$(\\w+))");

    @Subscribe
    default void onSelfServiceMixinBeforeShow(Screen.BeforeShowEvent event) {
        Screen screen = event.getSource();
        ScreenData screenData = UiControllerUtils.getScreenData(screen);

        for (String loaderId : screenData.getLoaderIds()) {
            DataLoader loader = screenData.getLoader(loaderId);
            String query = loader.getQuery();
            Matcher matcher = CONTAINER_REF_PATTERN.matcher(query);
            while (matcher.find()) {
                String paramName = matcher.group(1);
                String sessionAttribute = matcher.group(2);
                BeanLocator beanLocator = Extensions.getBeanLocator(event.getSource());
                UserSession userSession = beanLocator.get(UserSession.class);
                loader.setParameter(paramName, userSession.getAttribute(sessionAttribute));
                loader.load();
            }
        }
    }
}

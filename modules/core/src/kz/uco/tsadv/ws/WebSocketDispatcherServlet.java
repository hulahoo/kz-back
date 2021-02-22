package kz.uco.tsadv.ws;

import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class WebSocketDispatcherServlet extends DispatcherServlet {

    private volatile boolean initialized = false;

    @Override
    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext wac = findWebApplicationContext();
        if (wac == null) {
            ApplicationContext parent = AppContext.getApplicationContext();
            wac = createWebApplicationContext(parent);
        }

        onRefresh(wac);

        String attrName = getServletContextAttributeName();
        getServletContext().setAttribute(attrName, wac);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Published WebApplicationContext of servlet '" + getServletName() +
                    "' as ServletContext attribute with name [" + attrName + "]");
        }

        return wac;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        if (!initialized) {
            super.init(config);
            initialized = true;
        }
    }

    @Override
    public String getContextConfigLocation() {
        return "classpath:kz/uco/tsadv/websocket-spring.xml";
    }
}
package kz.uco.tsadv.ws;

import com.haulmont.cuba.core.sys.AbstractWebAppContextLoader;
import com.haulmont.cuba.core.sys.servlet.ServletRegistrationManager;
import com.haulmont.cuba.core.sys.servlet.events.ServletContextInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component("dispatching_WebSocketDispatcherServletInitializer")
public class WebSocketDispatcherServletInitializer {

    private static final String WEB_DISPATCHER_CLASS = "kz.uco.tsadv.ws.WebSocketDispatcherServlet";

    private static final String WEB_DISPATCHER_NAME = "dispatching_ws_servlet";

    private static final String URL_MAPPING = "/ws/*";

    @Inject
    private ServletRegistrationManager servletRegistrationManager;

    @EventListener
    public void initialize(ServletContextInitializedEvent e) {
        Servlet webDispatcherServlet = servletRegistrationManager.createServlet(e.getApplicationContext(), WEB_DISPATCHER_CLASS);
        ServletContext servletContext = e.getSource();
        try {
            webDispatcherServlet.init(new AbstractWebAppContextLoader.CubaServletConfig(WEB_DISPATCHER_NAME, servletContext));
        } catch (ServletException ex) {
            throw new RuntimeException("Failed to init WebDispatcherServlet");
        }
        servletContext.addServlet(WEB_DISPATCHER_NAME, webDispatcherServlet)
                .addMapping(URL_MAPPING);
    }
}

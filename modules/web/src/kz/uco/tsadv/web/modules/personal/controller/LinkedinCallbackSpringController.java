package kz.uco.tsadv.web.modules.personal.controller;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import kz.uco.tsadv.service.LinkedinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.UUID;

@Controller
public class LinkedinCallbackSpringController {

    @Inject
    private LinkedinService linkedinService;
    @Inject
    private TrustedClientService loginService;

    @RequestMapping(value = "linkedin", method = RequestMethod.GET)
    @ResponseBody
    public String getPage(@RequestParam("code") String code, @RequestParam("state") String personGroupId) {
        Configuration configuration = AppBeans.get(Configuration.class);
        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());
            AppContext.setSecurityContext(new SecurityContext(userSession));
        } catch (LoginException e) {
            e.printStackTrace();
        }
        try {
            linkedinService.fetchAccessToken(personGroupId != null ? UUID.fromString(personGroupId) : null, code);
        } catch (Exception e ) {
            e.printStackTrace();
        } finally {
            AppContext.setSecurityContext(null);
        }

        String html = "<html>\n" +
                "<head>\n" +
                "<script type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js\"></script>\n" +
                "<script type=\"text/javascript\"> \n" +
                "\n" +
                "    $(function() { \n" +
                "        window.close();" +
                "    });\n" +
                "</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }
}

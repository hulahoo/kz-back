package kz.uco.tsadv.rest;

import com.haulmont.addon.restapi.api.config.RestApiConfig;
import com.haulmont.addon.restapi.api.ldap.RestLdapConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import kz.uco.tsadv.components.RestUtilComponent;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Inject
    protected Configuration configuration;

    @Inject
    RestUtilComponent restUtilComponent;

    @Inject
    protected RestLdapConfig ldapConfig;

    @Inject
    protected WebAuthConfig webAuthConfig;

    @Inject
    protected RestApiConfig restApiConfig;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public String postAccessToken(Principal principal,
                                  @RequestParam Map<String, String> parameters,
                                  HttpServletRequest request) throws HttpRequestMethodNotSupportedException {
        String username = parameters.get("username");

        if (restApiConfig.getStandardAuthenticationUsers().contains(username)) {
            throw new BadCredentialsException("Bad credentials");
        }

        String password = parameters.get("password");

        if (ldapConfig.getLdapEnabled() && !webAuthConfig.getStandardAuthenticationUsers().contains(username)) {
            return restUtilComponent.getApplicationToken("/rest/v2/ldap/token", username, password);
        } else {
            return restUtilComponent.getApplicationToken("/rest/v2/oauth/token", username, password);
        }
    }
}
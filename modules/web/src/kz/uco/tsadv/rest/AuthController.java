package kz.uco.tsadv.rest;

import com.haulmont.addon.restapi.api.config.RestApiConfig;
import com.haulmont.addon.restapi.api.ldap.RestLdapConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Inject
    protected Configuration configuration;

    @Inject
    protected RestLdapConfig ldapConfig;

    @Inject
    protected WebAuthConfig webAuthConfig;

    @Inject
    protected RestApiConfig restApiConfig;

    @Inject
    private RestTemplate restTemplate;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public String postAccessToken(@RequestParam MultiValueMap<String, String> parameters,
                                  @RequestHeader HttpHeaders headers) {
        String username = parameters.getFirst("username");

        if (restApiConfig.getStandardAuthenticationUsers().contains(username)) {
            throw new BadCredentialsException("Bad credentials");
        }

        String defaultUrl = "/rest/v2/oauth/token";

        if (ldapConfig.getLdapEnabled() && !webAuthConfig.getStandardAuthenticationUsers().contains(username))
            defaultUrl = "/rest/v2/ldap/token";

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        return restTemplate.postForObject(AppContext.getProperty("cuba.webAppUrl") + defaultUrl, request, String.class);
    }
}
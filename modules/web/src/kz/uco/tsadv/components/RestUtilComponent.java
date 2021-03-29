package kz.uco.tsadv.components;

import com.haulmont.cuba.core.sys.AppContext;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@Component
public class RestUtilComponent {

    @Inject
    private RestTemplate restTemplate;

    public String getApplicationToken(String url, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);

        String restClientSecret = new String(Base64.encodeBase64(
                (AppContext.getProperty("cuba.rest.client.id") + ":" + (AppContext.getProperty("cuba.rest.client.secret")).substring(AppContext.getProperty("cuba.rest.client.secret").indexOf("}") + 1))
                        .getBytes(StandardCharsets.UTF_8)));
        headers.add("Authorization", "Basic " + restClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return this.restTemplate.postForObject(AppContext.getProperty("cuba.webAppUrl") + url, request, String.class);
    }
}

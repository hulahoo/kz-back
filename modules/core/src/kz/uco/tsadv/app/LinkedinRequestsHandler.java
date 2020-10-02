package kz.uco.tsadv.app;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.GlobalConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dto.LinkedinProfileDTO;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.common.IMAGE_SIZE;
import kz.uco.base.service.common.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component("tsadv_LinkedinRequestsHandler")
public class LinkedinRequestsHandler {

    private final Logger log = LoggerFactory.getLogger(LinkedinRequestsHandler.class);

    protected LinkedinApiConfig config;

    @Inject
    private Persistence persistence;

    @Inject
    private CommonService commonService;

    @Inject
    private GlobalConfig globalConfig;

    @Inject
    protected DataManager dataManager;

    private Configuration configuration;

    private String clientId;
    private String clientSecret;
    private String scope;
    private String callbackUrl;

    private void initFields() {
        configuration = AppBeans.get(Configuration.NAME);
        config = configuration.getConfig(LinkedinApiConfig.class);
        clientId = config.getLinkedinClientId();
        clientSecret = config.getLinkedinClientSecret();
        scope = config.getLinkedinScope();
        callbackUrl = config.getLinkedinCallbackUrl();
    }

    public String buildOauthLink(UUID personGroupId) throws URISyntaxException {
        initFields();
        URIBuilder builder = buildURIBuilder();
        builder.setPath("/oauth/v2/authorization")
                .setParameter("state", String.valueOf(personGroupId))
                .setParameter("redirect_uri", callbackUrl);
        URI uri = builder.build();

        HttpGet httpget = new HttpGet(uri);
        httpget.setHeader("content-type", "application/json");
        httpget.addHeader("x-li-format", "json");
        return String.valueOf(httpget.getURI());
    }

    private URIBuilder buildURIBuilder() {
        configuration = AppBeans.get(Configuration.NAME);
        config = configuration.getConfig(LinkedinApiConfig.class);
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("www.linkedin.com")
                .setParameter("client_id", clientId)
                .setParameter("client_secret", clientSecret)
                .setParameter("scope", scope)
                .setParameter("response_type", "code")
                .setParameter("redirect_uri", callbackUrl);
        return builder;
    }

    public void retrieveAndSetAccessToken(UUID personGroupId, String code) throws Exception {
        initFields();
        URIBuilder builder = buildURIBuilder();
        builder.setPath("/oauth/v2/accessToken")
                .setParameter("grant_type", "authorization_code")
                .setParameter("code", code);

        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("content-type", "x-www-form-urlencoded");
        httpPost.addHeader("x-li-format", "json");
        httpPost.addHeader("accept", "application/json");

        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();
        RequestConfig localConfig = RequestConfig.copy(globalConfig)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        HttpClient client = HttpClientBuilder.create().build();
        httpPost.setConfig(localConfig);
        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = response != null ? response.getStatusLine().getStatusCode() : 0;

        BufferedReader rd = new BufferedReader(new InputStreamReader(response != null ? response.getEntity().getContent() : null));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        JSONObject o = new JSONObject(result.toString());
        String accessToken = o.getString("access_token");

        if (responseCode == 200 && StringUtils.isNotEmpty(accessToken)) {
            try (Transaction tx = persistence.getTransaction()) {
                PersonGroupExt personGroup = persistence.getEntityManager().find(PersonGroupExt.class, personGroupId);
                if (personGroup != null) {
                    personGroup.setLinkedinAccessToken(accessToken);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 60);
                    personGroup.setLinkedinTokenExpiresInDate(calendar.getTime());
                    tx.commit();
                }
                linkUrlToProfileAndMaybeGetProfileDTO(personGroup, false);
            }
        }

    }

    public LinkedinProfileDTO linkUrlToProfileAndMaybeGetProfileDTO(PersonGroupExt personGroup, boolean isNeedToGetDTO) throws Exception {
        StringBuilder result = null;
        try {
            result = getProfile(personGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject o = new JSONObject(result.toString());
        try {
            int status = o.getInt("status");
            if (status != 0 && status != 200) {
                throw new Exception(o.getString("message") + " Status: " + status);
            }
        } catch (Exception e) {
            JSONObject siteStandardProfileRequestObject = o.getJSONObject("siteStandardProfileRequest");
            String profileUrl = siteStandardProfileRequestObject.getString("url");
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https").setHost("api.linkedin.com")
                    .setParameter("redirect_uri", callbackUrl)
                    .setParameter("format", "json");
            builder.setPath("v1/people/~:(first-name,email-address,last-name,headline,picture-url,picture-urls::(original),industry,summary,specialties,positions,educations,associations,interests,date-of-birth,publications,languages:(language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)");
            try {
                URI uri = builder.build();
                HttpGet httpGet = buildSecureHttpGet(uri, personGroup);

                HttpClient client = HttpClientBuilder.create().build();
                HttpResponse response = client.execute(httpGet);

                String responseString = String.valueOf(buildStringFromHttpResponse(response));

                try (Transaction tx = persistence.getTransaction()) {
                    PersonGroupExt personGroupToSave = persistence.getEntityManager().find(PersonGroupExt.class, personGroup.getId());
                    if (personGroupToSave != null) {
                        personGroupToSave.setLinkedinProfileLink(profileUrl);
                        if (isNeedToGetDTO) {
                            JSONObject jsonObject = new JSONObject(responseString);
                            JSONObject pictureUrlsJson = jsonObject.getJSONObject("pictureUrls");
                            if (pictureUrlsJson != null && pictureUrlsJson.getInt("_total") > 0) {
                                JSONArray jsonArray = pictureUrlsJson.getJSONArray("values");
                                String imageUrl = (String) jsonArray.get(0);
                                return getLinkedinProfileWithPhoto(imageUrl);
                            } else {
                                throw new Exception("User has no photo on Linkedin!");
                            }
                        }
                        tx.commit();
                    }
                }
            } catch (URISyntaxException | IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    private LinkedinProfileDTO getLinkedinProfileWithPhoto(String imageUrl) {
        byte[] bytes = null;
        try {
            bytes = CommonUtils.resize(new URL(imageUrl).openStream(), IMAGE_SIZE.XSS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytes != null) {
            LinkedinProfileDTO linkedinProfileDTO = new LinkedinProfileDTO();
            linkedinProfileDTO.setPhoto(bytes);
            return linkedinProfileDTO;
        }
        return null;
    }

    private StringBuilder getProfile(PersonGroupExt personGroup) throws Exception {
        if (StringUtils.isEmpty(personGroup.getLinkedinAccessToken()) || personGroup.getLinkedinTokenExpiresInDate().before(new Date())) {
            throw new Exception("LinkedinRequestsHandler::getProfile:: Person has no valid accessToken!");
        }
        URIBuilder builder = buildURIBuilder();
        builder.setPath("/v1/people/~");
        URI uri = builder.build();
        HttpGet httpGet = buildSecureHttpGet(uri, personGroup);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        return buildStringFromHttpResponse(response);
    }

    private HttpGet buildSecureHttpGet(URI uri, PersonGroupExt personGroup) {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("x-li-format", "json");
        httpGet.addHeader("accept", "application/json");
        httpGet.addHeader("Connection", "Keep-Alive");
        httpGet.addHeader("Authorization", "Bearer " + personGroup.getLinkedinAccessToken());
        return httpGet;
    }

    private StringBuilder buildStringFromHttpResponse(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result;
    }

}

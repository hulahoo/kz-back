package kz.uco.tsadv.web.screens.login;

import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExtAppLoginWindow extends kz.uco.base.web.template.BaseLoginWindowOld {

    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonConfig commonConfig;

    @Override
    public void login() {
        if (!checkUserForHavingAnyRole()) return;

        super.login();

        //saveRestAuthToken(loginField.getValue(), passwordField.getValue() != null ? passwordField.getValue() : "");
    }

    protected boolean checkUserForHavingAnyRole() {
        return isAllowedUserWithoutRole() ||
                isAnyRoleExistsForTheUser();
    }

    protected boolean isAllowedUserWithoutRole() {
        if (loginField.getValue() == null) return false;

        String allowedUsersWithoutRoles = commonConfig.getAllowedUsersWithoutRoles();
        if (allowedUsersWithoutRoles == null) return false;

        String[] logins = allowedUsersWithoutRoles.split("\\s+");   // whitespace
        if (logins.length == 0) return false;

        for (int i = 0; i < logins.length; i++) {
            if (loginField.getValue().equals(logins[i])) return true;
        }
        return false;
    }

    protected boolean isAnyRoleExistsForTheUser() {
        UserExt user = employeeService.getUserByLogin(loginField.getValue(), "user.roles");
        if (user != null && user.getUserRoles().size() == 0){
            showNotification(getMessage("userHasNotAnyRole.message"), NotificationType.ERROR);
            return false;
        }
        return true;
    }


    protected void saveRestAuthToken(String login, String password) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(AppContext.getProperty("cuba.webAppUrl") + "/rest/v2/oauth/token");
            String restClientSecret = new String(Base64.encodeBase64(
                    (AppContext.getProperty("cuba.rest.client.id") + ":" + AppContext.getProperty("cuba.rest.client.secret"))
                            .getBytes(StandardCharsets.UTF_8)));
            httpPost.addHeader("Authorization", "Basic " + restClientSecret);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> par = new ArrayList<NameValuePair>(3);
            par.add(new BasicNameValuePair("grant_type", "password"));
            par.add(new BasicNameValuePair("username", login));
            par.add(new BasicNameValuePair("password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(par, "UTF-8"));

            String json = httpClient.execute(httpPost, new StringResponseHandler());
            JSONObject jsonObject = new JSONObject(json);
            String accessToken = jsonObject.getString(OAuth2AccessToken.ACCESS_TOKEN);

            connection.getSession().setAttribute(StaticVariable.REST_AUTHORIZATION_TOKEN, accessToken); //TODO: 6.8.5
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class StringResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    }

}
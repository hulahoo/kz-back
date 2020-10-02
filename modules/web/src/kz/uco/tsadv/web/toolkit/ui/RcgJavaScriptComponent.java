package kz.uco.tsadv.web.toolkit.ui;

import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * @author adilbekov.yernar
 */
public class RcgJavaScriptComponent extends AbstractJavaScriptComponent {

    public RcgJavaScriptComponent() {
        setWebAppUrl(".");
    }

    @Override
    protected RcgComponentState getState() {
        return (RcgComponentState) super.getState();
    }

    @Override
    public void callFunction(String name, Object... arguments) {
        super.callFunction(name, arguments);
    }

    public String getAuthorizationToken() {
        return getState().authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        getState().authorizationToken = authorizationToken;
    }

    public String getWebAppUrl() {
        return getState().webAppUrl;
    }

    public void setWebAppUrl(String webAppUrl) {
        getState().webAppUrl = webAppUrl;
    }

    public String getReloadAttributes() {
        return getState().reloadAttributes;
    }

    public void setReloadAttributes(String reloadAttributes) {
        getState().reloadAttributes = reloadAttributes;
    }

    public String getPageName() {
        return getState().pageName;
    }

    public void setPageName(String pageName) {
        getState().pageName = pageName;
    }

    public int getAutomaticTranslate() {
        return getState().automaticTranslate;
    }

    public void setAutomaticTranslate(int automaticTranslate) {
        getState().automaticTranslate = automaticTranslate;
    }

    public String getMessageBundle() {
        return getState().messageBundle;
    }

    public void setMessageBundle(String messageBundle) {
        getState().messageBundle = messageBundle;
    }

    public String getLanguage() {
        return getState().language;
    }

    public void setLanguage(String language) {
        getState().language = language;
    }
}

package kz.uco.tsadv.web.toolkit.ui.jplayer;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.annotations.JavaScript;

@JavaScript({"jplayer-connector.js", "video.js"})
@StyleSheet({"video-js.css"})
public class JPlayerServerComponent extends AbstractJavaScriptComponent {
    public JPlayerServerComponent() {
    }

    @Override
    protected JPlayerState getState() {
        return (JPlayerState) super.getState();
    }

    public String getUrl() {
        return getState().url;
    }

    public void setUrl(String url) {
        getState().url = url;
    }

    public String getSourceType() {
        return getState().sourceType;
    }

    public void setSourceType(String sourceType) {
        getState().sourceType = sourceType;
    }
}
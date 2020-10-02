package kz.uco.tsadv.web.toolkit.ui.orgchartnew;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonString;

import javax.xml.transform.sax.SAXSource;
import java.util.ArrayList;

@JavaScript({"orgchartnew-connector.js", "getorgchart.js", "graphics.png"})
@StyleSheet({"getorgchart.css"})
public class OrgChartNew extends AbstractJavaScriptComponent {

    public interface ValueChangeListener {
        void linkClicked(String personGroupId);
    }

    private ValueChangeListener listener;

    /*public OrgChartNew() {
    }*/

    public OrgChartNew() {
        addFunction("linkClicked", (JavaScriptFunction) arguments -> {
            if (arguments.get(0) instanceof JsonString
            && arguments.getString(0) != null
            && !arguments.getString(0).isEmpty()) {
                String personGroupId = arguments.getString(0);
                for (ValueChangeListener listener : listeners)
                    listener.linkClicked(personGroupId);
            }
        });
    }

    @Override
    protected OrgChartNewState getState() {
        return (OrgChartNewState) super.getState();
    }

    private ArrayList<ValueChangeListener> listeners = new ArrayList<>();

    public void addValueChangeListener(ValueChangeListener listener) {
        listeners.add(listener);
    }

    public String getUrl() {
        return getState().url;
    }

    public void setUrl(String url) {
        getState().url = url;
    }

    public String getPersonGroupId() {
        return getState().personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        getState().personGroupId = personGroupId;
    }

    public String getChartId() {
        return getState().chartId;
    }

    public void setChartId(String chartId) {
        getState().chartId = chartId;
    }

    //*** + for rest api security
    public String getAuthorizationToken() {
        return getState().authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        getState().authorizationToken = authorizationToken;
    }
    //+ for rest api security ***
}
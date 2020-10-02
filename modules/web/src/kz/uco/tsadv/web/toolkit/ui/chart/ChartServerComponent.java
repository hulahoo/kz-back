package kz.uco.tsadv.web.toolkit.ui.chart;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;

@JavaScript({"chart-connector.js", "jquery.orgchart.js"})
@StyleSheet({"fa.min.css", "jquery.orgchart.css"})
public class ChartServerComponent extends AbstractJavaScriptComponent {

    public interface ValueChangeListener extends Serializable {
        void linkClicked();
    }

    public ChartServerComponent() {
        addFunction("linkClicked", (JavaScriptFunction) arguments -> {
            JsonArray array = arguments.getArray(0);

            setUrl(array.getString(0));
            setChartId(array.getString(1));

            for (ValueChangeListener listener : listeners)
                listener.linkClicked();
        });
    }

    private ArrayList<ValueChangeListener> listeners = new ArrayList<>();

    public void addValueChangeListener(ValueChangeListener listener) {
        listeners.add(listener);
    }

    public String getData() {
        return getState().data;
    }

    public void setData(String data) {
        getState().data = data;
    }

    public String getUrl() {
        return getState().url;
    }

    public void setUrl(String url) {
        getState().url = url;
    }

    public String getChartId() {
        return getState().chartId;
    }

    public void setChartId(String chartId) {
        getState().chartId = chartId;
    }

    public String getPersonGroupId() {
        return getState().personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        getState().personGroupId = personGroupId;
    }

    public String getLang() {
        return getState().lang;
    }

    public void setLang(String lang) {
        getState().lang = lang;
    }

    public String getSystemDate() {
        return getState().systemDate;
    }

    public void setSystemDate(String systemDate) {
        getState().systemDate = systemDate;
    }

    //*** + for rest api security
    public String getAuthorizationToken() {
        return getState().authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        getState().authorizationToken = authorizationToken;
    }
    //+ for rest api security ***

    @Override
    protected ChartState getState() {
        return (ChartState) super.getState();
    }
}
package kz.uco.tsadv.web.toolkit.ui.matrixcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"matrixcomponent-connector.js", "matrix.js"})
@StyleSheet({"matrix.css", "bootstrap/css/bootstrap.min.css"})
public class MatrixComponent extends AbstractJavaScriptComponent {

    public interface ValueChangeListener {
        void valueChanged(String changedItems);
    }

    private ValueChangeListener listener;

    public MatrixComponent() {
        addFunction("valueChanged", arguments -> {
            String changedItems = arguments.getString(0);
            listener.valueChanged(changedItems);
        });
    }

    @Override
    protected MatrixComponentState getState() {
        return (MatrixComponentState) super.getState();
    }

    public String getChangedItems() {
        return getState().changedItems;
    }

    public void setChangedItems(String changedItems) {
        getState().changedItems = changedItems;
    }

    public String getRequestUrl() {
        return getState().requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        getState().requestUrl = requestUrl;
    }

    public void setReload() {
        getState().reload = true;
    }

    public boolean getReload() {
        return getState().reload;
    }

    public ValueChangeListener getListener() {
        return listener;
    }

    public void setListener(ValueChangeListener listener) {
        this.listener = listener;
    }

    public void setAuthorizationToken(String authorizationToken) {
        getState().authorizationToken = authorizationToken;
    }

    public String getAuthorizationToken() {
        return getState().authorizationToken;
    }
}
package kz.uco.tsadv.web.toolkit.ui.scrumboardcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

@JavaScript({"scrumboardcomponent-connector.js", "scrum-board.min.js", "stupidtable.min.js"})
@StyleSheet({"scrum-board.css","bootstrap/css/bootstrap.min.css"})
public class ScrumBoardComponent extends AbstractJavaScriptComponent {

    public interface ValueChangeListener {
        void valueChanged(String changedItems);
    }

    public interface ActionClickListener {
        void actionChanged(String userId, String actionType);
    }

    private ValueChangeListener listener;

    private ActionClickListener actionClickListener;

    public ScrumBoardComponent() {
        addFunction("valueChanged", arguments -> {
            String changedItems = arguments.getString(0);
            listener.valueChanged(changedItems);
        });

        addFunction("actionClick", (JavaScriptFunction) arguments -> {
            String userId = arguments.getString(0);
            String actionType = arguments.getString(1);
            actionClickListener.actionChanged(userId, actionType);
        });
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

    public void setActionClickListener(ActionClickListener actionClickListener) {
        this.actionClickListener = actionClickListener;
    }

    @Override
    protected ScrumBoardComponentState getState() {
        return (ScrumBoardComponentState) super.getState();
    }

    public void setAuthorizationToken(String authorizationToken) {
        getState().authorizationToken = authorizationToken;
    }

    public String getAuthorizationToken() {
        return getState().authorizationToken;
    }
}
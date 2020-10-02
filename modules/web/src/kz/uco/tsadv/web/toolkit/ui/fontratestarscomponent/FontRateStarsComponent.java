package kz.uco.tsadv.web.toolkit.ui.fontratestarscomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"fontratestarscomponent-connector.js", "assets/font-rating.min.js", "assets/tipi.min.js"})
@StyleSheet("assets/font-rating.css")
public class FontRateStarsComponent extends AbstractJavaScriptComponent {

    public interface ValueChangeListener {
        void valueChanged(int newValue);
    }

    private ValueChangeListener listener;

    public FontRateStarsComponent() {
        addFunction("valueChanged", arguments -> {
            double jsonNumber = arguments.getNumber(0);
            listener.valueChanged((int) jsonNumber);
        });
    }

    public int getValue() {
        return getState().value;
    }

    public void setValue(int value) {
        getState().value = value;
    }

    public String[] getMessages() {
        return getState().messages;
    }

    public void setMessages(String[] messages) {
        getState().messages = messages;
    }

    @Override
    protected FontRateStarsComponentState getState() {
        return (FontRateStarsComponentState) super.getState();
    }

    public ValueChangeListener getListener() {
        return listener;
    }

    public void setListener(ValueChangeListener listener) {
        this.listener = listener;
    }

}
package kz.uco.tsadv.web.toolkit.ui.ratestarscomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.impl.JreJsonNumber;

@JavaScript({"ratestarscomponent-connector.js", "jquery.rateyo.min.js"})
@StyleSheet({"jquery.rateyo.min.css"})
public class RateStarsComponent extends AbstractJavaScriptComponent {

    public interface ValueChangeListener {
        void valueChanged(double newValue);
    }

    private ValueChangeListener listener;

    public RateStarsComponent() {
        addFunction("valueChanged", arguments -> {
            double value = -1;
            JreJsonNumber jsonNumber = arguments.get(0);
            if (jsonNumber != null) {
                value = Double.valueOf(jsonNumber.asString());
            }
            listener.valueChanged(value);
        });
    }

    @Override
    protected RateStarsComponentState getState() {
        return (RateStarsComponentState) super.getState();
    }

    public double getValue() {
        return getState().value;
    }

    public void setValue(double value) {
        getState().value = value;
    }

    public String getStarWidth() {
        return getState().starWidth;
    }

    public void setStarWidth(String starWidth) {
        getState().starWidth = starWidth;
    }

    public boolean isFullStar() {
        return getState().fullStar;
    }

    public void setFullStar(boolean fullStar) {
        getState().fullStar = fullStar;
    }

    public boolean isReadOnly() {
        return getState().onlyRead;
    }

    public void setReadOnly(boolean onlyRead) {
        getState().onlyRead = onlyRead;
    }

    public ValueChangeListener getListener() {
        return listener;
    }

    public void setListener(ValueChangeListener listener) {
        this.listener = listener;
    }

}
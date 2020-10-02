package kz.uco.tsadv.web.toolkit.ui.circliful;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"circliful-connector.js", "jquery.circliful.min.js"})
@StyleSheet({"jquery.circliful.css"})
public class CirclifulServerComponent extends AbstractJavaScriptComponent {
    public CirclifulServerComponent() {
    }

    public int getPercentage() {
        return getState().percentage;
    }

    public void setPercentage(int percentage) {
        getState().percentage = percentage;
    }

    public int getBorderWidth() {
        return getState().borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        getState().borderWidth = borderWidth;
    }

    public String getForegroundColor() {
        return getState().foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        getState().foregroundColor = foregroundColor;
    }

    public String getBackgroundColor() {
        return getState().backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        getState().backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return getState().fontColor;
    }

    public void setFontColor(String fontColor) {
        getState().fontColor = fontColor;
    }

    @Override
    protected CirclifulState getState() {
        return (CirclifulState) super.getState();
    }
}
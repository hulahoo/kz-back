package kz.uco.tsadv.web.toolkit.ui.circliful;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class CirclifulState extends JavaScriptComponentState {

    public int percentage;
    /**
     * Цвет заполнения (круг)
     */
    public String foregroundColor = "#197de1";

    /**
     * Фон коипонента (круга)
     */
    public String backgroundColor = "#FFFFFF";

    /**
     * Цвет текста (значение в %)
     */
    public String fontColor = "#FFFFFF";

    public int borderWidth = 10;
}

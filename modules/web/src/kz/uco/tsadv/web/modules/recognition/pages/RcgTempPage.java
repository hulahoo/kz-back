package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.gui.components.Label;
import com.vaadin.ui.JavaScript;

import java.util.Map;

public class RcgTempPage extends AbstractRcgPage {

    @Override
    protected void loadPage(Map<String, Object> params) {
        setAlignment(Alignment.MIDDLE_CENTER);

        Label label = componentsFactory.createComponent(Label.class);
        label.setWidthFull();
        label.setStyleName("rcg-page-under-dev");
        label.setAlignment(Alignment.MIDDLE_CENTER);
        label.setValue(getMessage("rcg.shop.under.development"));
        add(label);

        JavaScript.eval("var tps = $('.tippy-popper');if (tps) tps.remove();");
    }
}
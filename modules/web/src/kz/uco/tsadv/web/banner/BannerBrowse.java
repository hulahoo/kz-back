package kz.uco.tsadv.web.banner;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.Banner;

import javax.inject.Inject;

public class BannerBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    public Component generatePageName(Banner banner) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(getMessage("page." + banner.getPage()));
        return label;
    }
}
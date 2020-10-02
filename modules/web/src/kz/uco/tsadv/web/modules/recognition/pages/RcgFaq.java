package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.vaadin.ui.JavaScript;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class RcgFaq extends AbstractRcgPage {

    @Inject
    private VBoxLayout rcgFaqContent;

    @Override
    protected void loadPage(Map<String, Object> params) {
        List<kz.uco.tsadv.modules.recognition.RcgFaq> rcgFaqList = recognitionService.loadRcgFaqs();
        if (rcgFaqList != null && !rcgFaqList.isEmpty()) {
            for (kz.uco.tsadv.modules.recognition.RcgFaq rcgFaq : rcgFaqList) {
                rcgFaqContent.add(generateFaqBlock(rcgFaq));
            }
        }

        JavaScript.eval("var tps = $('.tippy-popper');if (tps) tps.remove();");
    }

    private Component generateFaqBlock(kz.uco.tsadv.modules.recognition.RcgFaq rcgFaq) {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);

        wrapper.setStyleName("rcg-faq-w");

        HBoxLayout header = componentsFactory.createComponent(HBoxLayout.class);
        header.setMargin(true);
        header.setStyleName("rcg-faq-h");
        header.setWidthFull();

        LinkButton title = componentsFactory.createComponent(LinkButton.class);
        title.setCaption(rcgFaq.getTitle());
        title.setStyleName("rcg-faq-h-title");
        header.add(title);
        header.expand(title);

        Label toggleIcon = componentsFactory.createComponent(Label.class);
        toggleIcon.setStyleName("rcg-faq-h-toggle");
        toggleIcon.setIcon("font-icon:CHEVRON_RIGHT");
        header.add(toggleIcon);

        wrapper.add(header);

        Label content = componentsFactory.createComponent(Label.class);
        content.setStyleName("rcg-faq-content");
        content.setSizeFull();
        content.setHtmlEnabled(true);
        content.setValue(rcgFaq.getContent());
        content.setVisible(false);
        wrapper.add(content);

        title.setAction(new BaseAction("toogle") {
            @Override
            public void actionPerform(Component component) {
                boolean visible = !content.isVisible();
                content.setVisible(visible);

                if (visible) {
                    wrapper.addStyleName("rcg-faq-w-active");
                } else {
                    wrapper.removeStyleName("rcg-faq-w-active");
                }

                toggleIcon.setIcon(visible ? "font-icon:CHEVRON_DOWN" : "font-icon:CHEVRON_RIGHT");
            }
        });

        return wrapper;
    }

}
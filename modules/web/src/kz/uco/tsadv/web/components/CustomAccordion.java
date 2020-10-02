package kz.uco.tsadv.web.components;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

/**
 * @author adilbekov.yernar
 */
public class CustomAccordion {

    public interface BoxExpandListener {
        void expand();
    }

    private static final String CUSTOM_ACCORDION_HEADER = "custom-accordion-header";
    private static final String CUSTOM_ACCORDION_CONTENT = "custom-accordion-content";

    private static final String CUSTOM_ACCORDION = "custom-accordion-w";
    private static final String CUSTOM_ACCORDION_OPEN = "custom-accordion-w-opened";
    private static final String CUSTOM_ACCORDION_CLOSE = "custom-accordion-w-closed";

    private ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);

    private VBoxLayout accordionWrapper;

    public CustomAccordion() {
        accordionWrapper = componentsFactory.createComponent(VBoxLayout.class);
        accordionWrapper.setSpacing(true);
    }

    public VBoxLayout addTab(String id, String title, String icon, Component actionsPanel, Component content, BoxExpandListener boxExpandListener, boolean opened) {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setStyleName(CUSTOM_ACCORDION);
        if (id != null) {
            wrapper.setId(id);
        }

        HBoxLayout headerBox = componentsFactory.createComponent(HBoxLayout.class);
        headerBox.setHeight("40px");
        headerBox.setWidthFull();
        headerBox.setId(CUSTOM_ACCORDION_HEADER);
        headerBox.addStyleName(CUSTOM_ACCORDION_HEADER);

        actionsPanel.setAlignment(Component.Alignment.MIDDLE_LEFT);
        headerBox.add(actionsPanel);

        Label titleLabel = label(title, icon, "custom-accordion-header-title");
        headerBox.add(titleLabel);

        wrapper.add(headerBox);

        VBoxLayout contentBox = componentsFactory.createComponent(VBoxLayout.class);
        contentBox.setStyleName(CUSTOM_ACCORDION_CONTENT);
        contentBox.setId(CUSTOM_ACCORDION_CONTENT);
        contentBox.add(content);
        contentBox.setVisible(opened);
        wrapper.add(contentBox);

        headerBox.addLayoutClickListener(event -> {
            if (!event.getMouseEventDetails().isDoubleClick()) {
                boolean opened1 = headerBox.getParent().getStyleName().contains(CUSTOM_ACCORDION_OPEN);
                contentBox.setVisible(!opened1);

                initVisible(wrapper, !opened1);
                initVisibleHeader(actionsPanel, titleLabel, !opened1);

                if (boxExpandListener != null) {
                    boxExpandListener.expand();
                }
            }
        });

        initVisibleHeader(actionsPanel, titleLabel, opened);
        initVisible(wrapper, opened);

        accordionWrapper.add(wrapper);
        return accordionWrapper;
    }

    public VBoxLayout addTab(String title, String icon, Component actionsPanel, Component content, BoxExpandListener boxExpandListener, boolean opened) {
        return addTab(null, title, icon, actionsPanel, content, boxExpandListener, opened);
    }

    public void customAccordionVisible(Boolean visible) {
        if (visible != null) {
            accordionWrapper.setVisible(visible);
        }
    }

    private void initVisibleHeader(Component actionsPanel, Component titleLabel, boolean opened) {
        actionsPanel.setVisible(opened);

        if (opened) {
            titleLabel.setAlignment(Component.Alignment.MIDDLE_RIGHT);

        } else {
            titleLabel.setAlignment(Component.Alignment.MIDDLE_LEFT);

        }
    }

    private void initVisible(VBoxLayout wrapper, boolean visible) {
        wrapper.removeStyleName(CUSTOM_ACCORDION_OPEN);
        wrapper.removeStyleName(CUSTOM_ACCORDION_CLOSE);

        wrapper.addStyleName(visible ? CUSTOM_ACCORDION_OPEN : CUSTOM_ACCORDION_CLOSE);
    }

    private Label label(String value, String icon, String cssClass) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        if (cssClass != null) {
            label.addStyleName(cssClass);
        }

        if (icon != null) {
            label.setIcon(icon);
        }
        return label;
    }

    public VBoxLayout getAccordionWrapper() {
        return accordionWrapper;
    }
}

package kz.uco.tsadv.cuba.actions.cuba68;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import org.dom4j.Element;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CreateAction68TS extends CreateAction {
    public CreateAction68TS(ListComponent target) {
        super(target);
        initStyle(target);
    }

    public CreateAction68TS(ListComponent target, WindowManager.OpenType openType) {
        super(target, openType);
        initStyle(target);
    }

    public CreateAction68TS(ListComponent target, WindowManager.OpenType openType, String id) {
        super(target, openType, id);
        initStyle(target);
    }

    protected void initStyle(ListComponent target) {
        if (target instanceof HasButtonsPanel) {
            ButtonsPanel buttonsPanel = ((HasButtonsPanel) target).getButtonsPanel();
            if (buttonsPanel != null) {
                Collection<Component> components = buttonsPanel.getComponents();
                if (components != null) {
                    for (Component component : components) {
                        if (component instanceof Button) {
                            Button button = (Button) component;
                            if (isButtonAction(target, button) && StringUtils.isEmpty(button.getStyleName())) {
                                button.addStyleName("create-btn-style");
                                button.addStyleName("border-none");
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected boolean isButtonAction(ListComponent target, Button button) {
        boolean isInstanceofAbstractWindow = target.getFrame().getFrameOwner() instanceof AbstractWindow;

        WebAbstractComponent abstractComponent = (WebAbstractComponent) this.target;
        Field field = ReflectionUtils.findField(abstractComponent.getClass(), "element");
        field.setAccessible(true);
        Element element = (Element) ReflectionUtils.getField(field, abstractComponent);
        List<Element> buttonsPanel = element.element("buttonsPanel").elements();

        List<Element> actions = element.element("actions").elements();

        String actionElementId = null;

        for (Element action : actions) {
            if (ACTION_ID.equals(action.attributeValue(isInstanceofAbstractWindow ? "id" : "type"))) {
                actionElementId = action.attributeValue("id");
            }
        }

        if (buttonsPanel != null) {
            for (Element btnElement : buttonsPanel) {
                String action = btnElement.attributeValue("action");
                if (target != null && btnElement != null && button != null && actionElementId != null && action != null) {
                    if (Objects.equals(button.getId(), btnElement.attributeValue("id"))
                            && action.equals(target.getId() + "." + actionElementId)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }
}

package kz.uco.tsadv.cuba.actions.cuba68;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import org.dom4j.Element;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class RemoveAction68TS extends RemoveAction {

    public RemoveAction68TS(ListComponent target) {
        super(target);
        initStyle(target);
    }

    public RemoveAction68TS(ListComponent target, boolean autocommit) {
        super(target, autocommit);
        initStyle(target);
    }


    public RemoveAction68TS(ListComponent target, boolean autocommit, String id) {
        super(target, autocommit, id);
        initStyle(target);
    }

    protected void initStyle(ListComponent target) {
        if (target instanceof HasButtonsPanel) {
            ButtonsPanel buttonsPanel = ((HasButtonsPanel) target).getButtonsPanel();
            Collection<Component> components = buttonsPanel.getComponents();
            if (components != null) {
                for (Component component : components) {
                    if (component instanceof Button) {
                        Button button = (Button) component;
                        if (isButtonAction(target, button) && StringUtils.isEmpty(button.getStyleName())) {
                            button.addStyleName("remove-btn-style");
                            button.addStyleName("border-none");
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
                if (Objects.equals(button.getId(), btnElement.attributeValue("id"))
                        && action.equals(target.getId() + "." + actionElementId)) {
                    return true;
                }
            }
        }
        return false;
    }
}

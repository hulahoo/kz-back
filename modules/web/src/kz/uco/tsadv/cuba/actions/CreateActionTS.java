package kz.uco.tsadv.cuba.actions;

import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import kz.uco.base.common.ScreenHelper;
import kz.uco.base.cuba.actions.CreateActionExt;
import org.dom4j.Element;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ActionType(CreateAction.ID)
public class CreateActionTS extends CreateActionExt {

    public CreateActionTS() {
    }

    public CreateActionTS(String id) {
        super(id);
    }
    @Inject
    protected ScreenHelper screenHelper;

    @Override
    public void actionPerform(Component component) {
        if (!hasSubscriptions(ActionPerformedEvent.class)) {
            AbstractEditor abstractEditor = screenHelper.openEditorComposition(target, this);
            if (abstractEditor != null) return;     // opened Screen
        }
        super.actionPerform(component);
    }

    @Override
    public void setTarget(ListComponent target) {
        super.setTarget(target);
        if (target instanceof HasButtonsPanel) {
            ButtonsPanel buttonsPanel = ((HasButtonsPanel) target).getButtonsPanel();
            Collection<Component> components = buttonsPanel.getComponents();
            if (components != null) {
                for (Component component : components) {
                    if (component instanceof Button) {
                        Button button = (Button) component;
                        if (isButtonAction(target, button)) {
                            button.setStyleName("red");
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
            if (ID.equals(action.attributeValue(isInstanceofAbstractWindow ? "id" : "type"))) {
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

package kz.uco.tsadv.components;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * @author adilbekov.yernar
 */
@org.springframework.stereotype.Component("cuba_ExtWebWindowManager")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExtWebWindowManager /*extends WebWindowManager*/ {

 /*   public ExtWebWindowManager() {
        setUi(AppUI.getCurrent());
    }

    private boolean recognition = false;

    private String additionalClass;

    @Override
    protected Component showWindowDialog(Window window, OpenType openType, boolean forciblyDialog) {
        Component component = super.showWindowDialog(window, openType, forciblyDialog);
        if (recognition) component.addStyleName("rcg-dialog-window");
        if (!StringUtils.isBlank(additionalClass)) {
            component.addStyleName(additionalClass);
        }
        return component;
    }

    public void setAdditionalClass(String additionalClass) {
        this.additionalClass = additionalClass;
    }

    public boolean isRecognition() {
        return recognition;
    }

    public void setRecognition(boolean recognition) {
        this.recognition = recognition;
    }
  */
}

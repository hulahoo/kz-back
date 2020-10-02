package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ResizableTextArea;
import com.haulmont.cuba.gui.components.actions.BaseAction;

import javax.inject.Inject;
import java.util.Map;

public class RequisitionCancel extends AbstractWindow {

    @Inject
    private ResizableTextArea<String> reasonTextArea;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addAction(new BaseAction("ok") {
            @Override
            public void actionPerform(Component component) {
                if (validateAll()) {
                    close("ok");
                }
            }
        });

        addAction(new BaseAction("close") {
            @Override
            public void actionPerform(Component component) {
                close("close");
            }
        });
    }

    public String getReasonValue() {
        return reasonTextArea.getValue();
    }

}
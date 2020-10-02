package kz.uco.tsadv.web.modules.performance.assignedgoal;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.RichTextArea;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;

import javax.inject.Inject;
import java.util.Map;

public class AssignedGoalEdit extends AbstractEditor<AssignedGoal> {
    @Inject
    protected FieldGroup assignedGoalFieldGroup;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSession userSession;
    protected RichTextArea richTextArea;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        assignedGoalFieldGroup.addCustomField("successCritetia", (datasource, propertyId) -> {
            richTextArea = componentsFactory.createComponent(RichTextArea.class);
            richTextArea.setDatasource(datasource, propertyId);
            return richTextArea;
        });
    }

    protected void checkManagerPrimaryflag() {
        if (!(Boolean) userSession.getAttribute("userPositionManagerFlag")) {
            for (FieldGroup.FieldConfig f : assignedGoalFieldGroup.getFields()) {
                if (!f.getProperty().equals("actualValue"))
                    f.setEditable(false);
            }
        }
    }

    @Override
    public void ready() {
        super.ready();
        checkManagerPrimaryflag();
        changeRichTextForManagerPrimaryFlag();
    }

    protected void changeRichTextForManagerPrimaryFlag() {
        if (!(Boolean) userSession.getAttribute("userPositionManagerFlag") && richTextArea.getValue() == null) {
            richTextArea.setHeight("30px");
        }
    }
}
package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PositionStructure;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("all")
public class PcfAssignmentMaster extends EditableFrame {

    private Datasource<AssignmentExt> assignmentDs;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    @Named("fieldGroup.organizationGroup")
    private PickerField organizationGroup;

    @Inject
    @Named("fieldGroup.jobGroup")
    private PickerField jobGroup;

    @Inject
    @Named("fieldGroup.gradeGroup")
    private PickerField gradeGroup;

    @Inject
    @Named("fieldGroup.location")
    private PickerField location;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        organizationGroup.removeAction(PickerField.OpenAction.NAME);
        jobGroup.removeAction(PickerField.OpenAction.NAME);
        gradeGroup.removeAction(PickerField.OpenAction.NAME);
        location.removeAction(PickerField.OpenAction.NAME);

        FieldGroup.FieldConfig positionGroupConfig = fieldGroup.getField("positionGroup");
        PickerField positionGroup = componentsFactory.createComponent(PickerField.class);
        positionGroup.setDatasource(assignmentDs, positionGroupConfig.getProperty());
        positionGroup.addAction(new AbstractAction("customLookup") {
            @Override
            public void actionPerform(Component component) {
                openPositionStructureBrowse();
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });

        positionGroup.setWidth("100%");
        positionGroup.setCaptionMode(CaptionMode.PROPERTY);
        positionGroup.setCaptionProperty("position");
        positionGroup.setRequired(true);
        positionGroupConfig.setComponent(positionGroup);
    }

    private void openPositionStructureBrowse() {
        AbstractLookup hierarchyElementBrowse = openLookup("tsadv$PositionStructure.browse", new Window.Lookup.Handler() {
            @Override
            public void handleLookup(Collection items) {
                for (Object o : items) {
                    PositionStructure ps = (PositionStructure) o;
                    PositionGroupExt positionGroup = dataManager.reload(ps.getPositionGroup(), "positionGroup.list");

                    AssignmentExt assignment = assignmentDs.getItem();

                    assignment.setPositionGroup(positionGroup);
                    assignment.setOrganizationGroup(ps.getOrganizationGroup());
                    assignment.setJobGroup(positionGroup.getPosition().getJobGroup());
                    assignment.setGradeGroup(positionGroup.getPosition().getGradeGroup());
                    assignment.setLocation(positionGroup.getPosition().getLocation());
                }
            }
        }, WindowManager.OpenType.DIALOG);
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
    }
}
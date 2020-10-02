package kz.uco.tsadv.web.modules.personal.successionplanning;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.personal.model.SuccessionPlanning;
import kz.uco.tsadv.modules.personal.model.Successor;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SuccessionPlanningEdit extends AbstractEditor<SuccessionPlanning> {

    protected static final String IMAGE_CELL_HEIGHT = "50px";

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected GroupDatasource<Successor, UUID> successorsDs;

    @Inject
    protected Datasource<SuccessionPlanning> successionPlanningDs;

    @Named("successorsDsTable.create")
    protected CreateAction successorsDsTableCreate;
    @Named("successorsDsTable.edit")
    protected EditAction successorsDsTableEdit;
    @Inject
    protected FieldGroup fieldGroup;

    public Component generatePersonImageCell(Successor entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    protected List newSuccessors;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        successorsDsTableCreate.setWindowParams(ParamsMap.of("fromSuccessorPlanning", true));
        successorsDsTableEdit.setWindowParams(ParamsMap.of("fromSuccessorPlanning", true));

        if (params.containsKey(StaticVariable.NEW_SUCCESSORS)) {
            this.newSuccessors = (List) params.get(StaticVariable.NEW_SUCCESSORS);
        }

        FieldGroup.FieldConfig positionGroupConfig = fieldGroup.getField("positionGroup");
        PickerField positionGroupPickerField = componentsFactory.createComponent(PickerField.class);
        positionGroupPickerField.setDatasource(successionPlanningDs, positionGroupConfig.getProperty());
        positionGroupPickerField.addAction(new AbstractAction("customLookup") {
            @Override
            public void actionPerform(Component component) {
                openLookup("tsadv$PositionStructure.browse", (items) -> {
                    for (Object o : items) {
                        PositionExt ps = (PositionExt) o;
                        getItem().setPositionGroup(ps.getGroup());
                    }
                }, WindowManager.OpenType.DIALOG);
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        positionGroupPickerField.setWidth("100%");
        positionGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        positionGroupPickerField.setCaptionProperty("position");
        positionGroupConfig.setComponent(positionGroupPickerField);
    }

    @Override
    public void ready() {
        super.ready();

        if (newSuccessors != null) {
            newSuccessors.forEach(o -> successorsDs.addItem((Successor) o));
        }
    }
}
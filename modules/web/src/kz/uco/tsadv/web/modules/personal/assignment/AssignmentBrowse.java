package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class AssignmentBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "100px";

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupDatasource<AssignmentExt, UUID> assignmentsDs;

    @Inject
    private GroupTable<AssignmentExt> assignmentsTable;

    @Inject
    private LookupPickerField<Entity> organizationGroupLookupId;

    @Inject
    private Label organizationGroupIdStr;

    @Inject
    private DataManager dataManager;

    @Inject
    private TextField personNameId;

    @Inject
    private TextField positionNameId;

    @Inject
    private DateField filterDate;

    @Override
    public void init(Map<String, Object> params) {
        filterDate.setValue(params.containsKey("filterDate") ? (Date) params.get("filterDate") : CommonUtils.getSystemDate());

        assignmentsTable.setSettingsEnabled(false);

        if (WindowParams.MULTI_SELECT.getBool(getContext())) {
            assignmentsTable.setMultiSelect(true);
        }

        personNameId.addValueChangeListener((e) -> {
            assignmentsDs.refresh();
        });

        positionNameId.addValueChangeListener((e) -> {
            assignmentsDs.refresh();
        });

        organizationGroupLookupId.removeAction(organizationGroupLookupId.addOpenAction());
        organizationGroupLookupId.addValueChangeListener((e) -> {
            organizationGroupIdStr.setValue(e.getValue() != null ? ((OrganizationGroupExt) e.getValue()).getId() + "" : "");
            assignmentsDs.refresh();
        });

        super.init(params);
    }

    public Component generatePersonImageCell(AssignmentExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }
}

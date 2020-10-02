package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.v7.ui.Table;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssignmentMyTeamBrowseOld extends AbstractLookup {
   /* private static final String IMAGE_CELL_HEIGHT = "70px";

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupDatasource<AssignmentExt, UUID> assignmentsDs;

    @Inject
    private GroupTable<AssignmentExt> assignmentsTable;

    @Inject
    private LookupPickerField organizationGroupLookupId;

    @Inject
    private Label organizationGroupIdStr;

    @Inject
    private DataManager dataManager;

    @Inject
    private OptionsGroup myTeamOptionGroupId;

    @Inject
    private TextField personNameId;

    @Inject
    private Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        assignmentsTable.setSettingsEnabled(false);
        Map<String, Boolean> myTeamOptionGroupMap = new HashMap<>();
        myTeamOptionGroupMap.put(getMessage("AssignmentMyTeamBrowse.ShowMyTeamOnly"), Boolean.TRUE);
        myTeamOptionGroupMap.put(getMessage("AssignmentMyTeamBrowse.ShowAll"), Boolean.FALSE);
        myTeamOptionGroupId.setOptionsMap(myTeamOptionGroupMap);
        myTeamOptionGroupId.setValue(Boolean.TRUE);

        myTeamOptionGroupId.addValueChangeListener((e) -> {
            assignmentsDs.refresh();
        });

        personNameId.addValueChangeListener((e) -> {
            assignmentsDs.refresh();
        });

        organizationGroupLookupId.removeAction(organizationGroupLookupId.addOpenAction());
        organizationGroupLookupId.addValueChangeListener((e) -> {
            organizationGroupIdStr.setValue(e.getValue() != null ? ((OrganizationGroupExt) e.getValue()).getId() + "" : "");
            assignmentsDs.refresh();
        });

        Table table = assignmentsTable.unwrap(Table.class);
        table.addItemClickListener((e) -> {
            if (e.isDoubleClick())
                viewProfile();
        });

        super.init(params);
    }

    public Component generatePersonImageCell(AssignmentExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    public Component generateAssignmentG1Cell(AssignmentExt entity) {
        return generateAssignmentGroupCell(entity, "G1");
    }

    public Component generateAssignmentG2Cell(AssignmentExt entity) {
        return generateAssignmentGroupCell(entity, "G2");
    }

    public Component generateAssignmentG3Cell(AssignmentExt entity) {
        return generateAssignmentGroupCell(entity, "G3");
    }


    public Component generateAssignmentGroupCell(AssignmentExt entity, String group) {
        CssLayout wrapper = componentsFactory.createComponent(CssLayout.class);

        *//*Label label = componentsFactory.createComponent(Label.class);
        label.setHtmlEnabled(true);*//*

        switch (group) {
            case "G1":

                VBoxLayout vBoxLayout1 = componentsFactory.createComponent(VBoxLayout.class);

                LinkButton personName = componentsFactory.createComponent(LinkButton.class);
                personName.setCaption(entity.getPersonGroup().getPerson().getFioWithEmployeeNumber());
                personName.setStyleName("ss-div-blue");
                personName.setAction(new BaseAction("redirect-card") {
                    @Override
                    public void actionPerform(Component component) {
                        openEditor("person-card", entity, WindowManager.OpenType.THIS_TAB);
                    }
                });

                vBoxLayout1.add(personName);

                Label position = componentsFactory.createComponent(Label.class);
                if (entity.getPositionGroup().getPosition() != null)
                    position.setValue(entity.getPositionGroup().getPosition().getPositionName());
                position.setStyleName("ss-div-dimgrey");

                vBoxLayout1.add(position);

                if (entity.getPositionGroup().getPosition() != null && entity.getPositionGroup().getPosition().getManagerFlag()) {
                    Label positionStructureInfoLabel = componentsFactory.createComponent(Label.class);
                    positionStructureInfoLabel.setValue(getPositionStructureInfo(entity));
                    positionStructureInfoLabel.setStyleName("ss-icon ss-div-dimgrey");
                    positionStructureInfoLabel.setIcon("font-icon:USERS");

                    vBoxLayout1.add(positionStructureInfoLabel);
                }

                wrapper.add(vBoxLayout1);
                break;
            case "G2":
                VBoxLayout vBoxLayout2 = componentsFactory.createComponent(VBoxLayout.class);

                HBoxLayout hb1 = componentsFactory.createComponent(HBoxLayout.class);
                HBoxLayout hb2 = componentsFactory.createComponent(HBoxLayout.class);
                HBoxLayout hb3 = componentsFactory.createComponent(HBoxLayout.class);

                Label l11 = componentsFactory.createComponent(Label.class);
                l11.setValue(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Assignment.grade"));
                l11.setWidth("120px");
                l11.setStyleName("ss-textright ss-div-grey");
                hb1.add(l11);

                Label l12 = componentsFactory.createComponent(Label.class);
                l12.setValue((entity.getGradeGroup() != null ? entity.getGradeGroup().getGrade().getGradeName() : ""));
                l12.setStyleName("ss-textleft ss-div-dimgrey");
                hb1.add(l12);

                vBoxLayout2.add(hb1);

                Label l21 = componentsFactory.createComponent(Label.class);
                l21.setValue(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Assignment.location"));
                l21.setWidth("120px");
                l21.setStyleName("ss-textright ss-div-grey");
                hb2.add(l21);

                Label l22 = componentsFactory.createComponent(Label.class);
                l22.setValue((entity.getLocation() != null ? entity.getLocation().getLangValue() : ""));
                l22.setStyleName("ss-textleft ss-div-dimgrey");
                hb2.add(l22);

                vBoxLayout2.add(hb2);

                Label l31 = componentsFactory.createComponent(Label.class);
                l31.setValue(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Assignment.organization"));
                l31.setWidth("120px");
                l31.setStyleName("ss-textright ss-div-grey");
                hb3.add(l31);

                Label l32 = componentsFactory.createComponent(Label.class);
                if (entity.getOrganizationGroup() != null){
                    l32.setValue((entity.getOrganizationGroup().getOrganization() != null ? entity.getOrganizationGroup().getOrganization().getOrganizationName() : ""));
                }
                l32.setStyleName("ss-textleft ss-div-dimgrey");
                hb3.add(l32);

                vBoxLayout2.add(hb3);

                wrapper.add(vBoxLayout2);

                break;
            case "G3":
                break;
        }

        return wrapper;
    }

    public void viewProfile() {
        openEditor("person-card", assignmentsDs.getItem(), WindowManager.OpenType.THIS_TAB);
    }

    @Nullable
    private String getPositionStructureInfo(AssignmentExt entity) {
        String positionStructureInfo = null;
        int direct = 0;
        int total = 0;

        LoadContext<AssignmentExt> positionStructureLoadContext = LoadContext.create(AssignmentExt.class)
                .setQuery(
                        LoadContext.createQuery("select e " +
                                " from base$AssignmentExt e " +
                                " where :systemDate between e.startDate and e.endDate " +
                                " and e.positionGroup.id in (select ps.positionGroup.id " +
                                " from tsadv$PositionStructure ps " +
                                " where ps.parentPositionGroup.id = :positionGroupId " +
                                " and :systemDate between ps.startDate and ps.endDate " +
                                " and :systemDate between ps.posStartDate and ps.posEndDate)")
                                .setParameter("positionGroupId", entity.getPositionGroup().getId())
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                );

        direct = dataManager.loadList(positionStructureLoadContext).size();

        positionStructureLoadContext = LoadContext.create(AssignmentExt.class)
                .setQuery(
                        LoadContext.createQuery("select e from base$AssignmentExt e " +
                                " where :systemDate between e.startDate and e.endDate " +
                                " and e.positionGroup.id in (select ps.positionGroup.id " +
                                " from tsadv$PositionStructure ps " +
                                " where ps.positionGroupPath like concat('%',concat(:positionGroupId, '%')) " +
                                " and :systemDate between ps.startDate and ps.endDate" +
                                " and :systemDate between ps.posStartDate and ps.posEndDate)")
                                .setParameter("positionGroupId", entity.getPositionGroup().getId().toString())
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                );
        total = dataManager.loadList(positionStructureLoadContext).size() - 1;

        positionStructureInfo = String.format(getMessage("AssignmentMyTeamBrowse.positionStructureInfo"), direct, total);

        return positionStructureInfo;
    }*/
}
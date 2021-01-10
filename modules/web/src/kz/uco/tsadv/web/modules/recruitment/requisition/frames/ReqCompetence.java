package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.modules.personal.model.Scale;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.tsadv.modules.recruitment.enums.CompetenceCriticalness;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionCompetence;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.persistence.Persistence;
import java.util.*;

/**
 * @author veronika.buksha
 */
public class ReqCompetence extends AbstractFrame {
    public CollectionDatasource<RequisitionCompetence, UUID> competencesDs;
    public Datasource<Requisition> requisitionDs;

    private boolean browseOnly;

    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private Persistence persistence;
    @Inject
    private Button fillOrgCompetences;
    @Inject
    private DataGrid<RequisitionCompetence> competencesTable;
    @Inject
    protected UserSession userSession;
    @Inject
    private CommonService commonService;
    @Inject
    private ComponentsFactory componentsFactory;
    LookupField scaleLevelLookUp;

    @Override
    public void init(Map<String, Object> params) {
        if (params.get("browseOnly") != null)
            browseOnly = true;
        super.init(params);
        competencesDs = (CollectionDatasource<RequisitionCompetence, UUID>) getDsContext().get("competencesDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");
        fillOrgCompetences.setEnabled(requisitionDs.getItem().getOrganizationGroup() != null || requisitionDs.getItem().getPositionGroup() != null);
        if (browseOnly) {
            fillOrgCompetences.setVisible(false);

            for (Action a : competencesTable.getActions()) {
                a.setVisible(false);
                a.setEnabled(false);
            }

        }
        hideButtonPanel();

        DataGrid.Column competenceGroup = competencesTable.addGeneratedColumn("competenceGroup", new DataGrid.ColumnGenerator<RequisitionCompetence, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<RequisitionCompetence> event) {
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(event.getItem().getCompetenceGroup().getCompetence().getCompetenceName());
                return label;
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        }, 0);
        competenceGroup.setRenderer(new WebComponentRenderer());

        DataGrid.Column scaleLevel = competencesTable.addGeneratedColumn("scaleLevel", new DataGrid.ColumnGenerator<RequisitionCompetence, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<RequisitionCompetence> event) {
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(event.getItem().getScaleLevel());
                return label;
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        }, 1);
        competenceGroup.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "competenceColumn"));
        scaleLevel.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "scaleLevelColumn"));
        scaleLevel.setRenderer(new WebComponentRenderer());

        competencesTable.getColumnNN("competenceGroup").setEditorFieldGenerator((datasource, property) -> {
            PickerField<Entity> field = componentsFactory.createComponent(PickerField.class);
            field.setDatasource(competencesDs, "competenceGroup");
            field.addLookupAction();
            field.setCaptionProperty("competence");
            field.setCaptionMode(CaptionMode.PROPERTY);
            Utils.customizeLookup(field, "tsadv$CompetenceGroup.browse", WindowManager.OpenType.DIALOG, null);
            field.addClearAction();
            field.addValueChangeListener(e -> {
                Map map = new HashMap();
                map.put("competenceGroup", ((CompetenceGroup)e.getValue()).getId());
                map.put("systemDate", kz.uco.tsadv.global.common.CommonUtils.getSystemDate());
                ((CollectionDatasource<ScaleLevel, UUID>) getDsContext().get("scaleLevelsDs")).setQuery("select e from tsadv$ScaleLevel e\n" +
                                " where e.scale.id = (select e2.scale.id from tsadv$Competence e2\n" +
                                " where e2.group.id = :custom$competenceGroup \n" +
                                " and :custom$systemDate between e2.startDate and e2.endDate)");
                ((CollectionDatasource<ScaleLevel, UUID>) getDsContext().get("scaleLevelsDs")).refresh(map);
                scaleLevelLookUp.setValue(null);

            });
            return field;
        });

        competencesTable.getColumnNN("scaleLevel").setEditorFieldGenerator((datasource, property) -> {
            scaleLevelLookUp = componentsFactory.createComponent(LookupField.class);
            scaleLevelLookUp.setDatasource(competencesDs, "scaleLevel");
            scaleLevelLookUp.setOptionsDatasource((CollectionDatasource<ScaleLevel, UUID>) getDsContext().get("scaleLevelsDs"));
            return scaleLevelLookUp;
        });

        competencesTable.addEditorPreCommitListener(event -> {
            competencesTable.repaint();
        });


    }

    public void hideButtonPanel() {
        //скрывает панель кнопок для руководителя
        Map<String, Object> map = new HashMap<>();
        map.put("userSessionID", userSession.getUser().getId());
        map.put("userSessionManagerID", requisitionDs.getItem().getManagerPersonGroup() != null ?
                requisitionDs.getItem().getManagerPersonGroup().getId() : null);
        String query = "SELECT e FROM tsadv$UserExt e " +
                "JOIN base$PersonGroupExt t ON t.id = e.personGroupId.id " +
                "where e.id = :userSessionID AND e.personGroupId.id = :userSessionManagerID";
        List<UserExt> extList = commonService.getEntities(UserExt.class, query, map, null);
        ButtonsPanel panel = (ButtonsPanel) getComponent("competencesButtonsPanel");
        if (extList.size() > 0) {
            competencesTable.setEditorEnabled(false);
            panel.setVisible(false);
        } else {
            competencesTable.setEditorEnabled(true);
            panel.setVisible(true);
        }
    }

    public void fillOrgCompetences() {
        OrganizationGroupExt organizationGroup = requisitionDs.getItem().getOrganizationGroup();
        PositionGroupExt positionGroup = requisitionDs.getItem().getPositionGroup();

        LoadContext.Query loadContextQuery;
        String query = "select e from tsadv$CompetenceElement e where ";
        boolean added = true;

        if (organizationGroup != null && positionGroup != null) {
            query = String.format(query + " e.organizationGroup.id = '%s' or e.positionGroup.id = '%s'",
                    organizationGroup.getId(), positionGroup.getId());
        } else if (organizationGroup != null) {
            query = String.format(query + " e.organizationGroup.id = '%s'",
                    organizationGroup.getId());
            showNotification("Операция недоступна для данного статуса заявки", NotificationType.TRAY);
        } else if (positionGroup != null) {
            query = String.format(query + " e.positionGroup.id = '%s'",
                    positionGroup.getId());
        } else {
            added = false;
        }

        if (added) {
            loadContextQuery = LoadContext.createQuery(query);

            executeQuery(loadContextQuery);
            competencesDs.commit();
        }
    }

    private void executeQuery(LoadContext.Query loadContextQuery) {
        List<CompetenceElement> competences = getCompetenceElements(loadContextQuery);

        if (!competences.isEmpty()) {
            Collection<RequisitionCompetence> rCompetences = competencesDs.getItems();

            for (CompetenceElement competence : competences) {
                boolean add = false;

                if (rCompetences.isEmpty()) {
                    add = true;
                } else {
                    boolean hasCompetence = hasCompetence(rCompetences, competence.getCompetenceGroup(), competence.getScaleLevel());

                    if (!hasCompetence) add = true;
                }

                if (add) {
                    RequisitionCompetence model = metadata.create(RequisitionCompetence.class);
                    model.setRequisition(requisitionDs.getItem());
                    model.setCompetenceGroup(competence.getCompetenceGroup());
                    model.setScaleLevel(competence.getScaleLevel());
                    model.setCriticalness(CompetenceCriticalness.CRITICAL);
                    competencesDs.addItem(model);
                }
            }
        }
    }

    private List<CompetenceElement> getCompetenceElements(LoadContext.Query loadContextQuery) {
        LoadContext<CompetenceElement> loadContext = LoadContext.create(CompetenceElement.class);
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("competenceElement-view");
        return dataManager.loadList(loadContext);
    }

    private boolean hasCompetence(Collection<RequisitionCompetence> rCompetences, CompetenceGroup competenceGroup, ScaleLevel scaleLevel) {
        boolean has = false;

        List<RequisitionCompetence> forDelete = new ArrayList<>();

        for (RequisitionCompetence rCompetence : rCompetences) {
            if (rCompetence.getCompetenceGroup().getId().equals(competenceGroup.getId())) {
                has = scaleLevel.getLevelNumber() <= rCompetence.getScaleLevel().getLevelNumber();

                if (!has) {
                    forDelete.add(rCompetence);
                }
            }
        }

        if (!forDelete.isEmpty()) {
            for (RequisitionCompetence dCompetence : forDelete) competencesDs.removeItem(dCompetence);
        }

        return has;
    }


    public void createCompetence() {
        Map<String, Object> comParams = new HashMap<>();
        comParams.put("req", competencesDs);
        openLookup("tsadv$CompetenceGroup.lookup", new Window.Lookup.Handler() {
            @Override
            public void handleLookup(Collection items) {
                for (CompetenceGroup competenceGroup : (Collection<CompetenceGroup>) items) {
                    RequisitionCompetence requisitionCompetence = metadata.create(RequisitionCompetence.class);
                    requisitionCompetence.setCompetenceGroup(competenceGroup);
                    requisitionCompetence.setCriticalness(CompetenceCriticalness.DESIRABLE);
                    requisitionCompetence.setScaleLevel(getMinScaleLevel(competenceGroup.getCompetence().getScale()));
                    requisitionCompetence.setRequisition(requisitionDs.getItem());
                    competencesDs.addItem(requisitionCompetence);

                }
            }
        }, WindowManager.OpenType.DIALOG, comParams);
    }

    private ScaleLevel getMinScaleLevel(Scale scale) {
        Map<String, Object> map = new HashMap<>();
        map.put("scaleId", scale);
        return  commonService.getEntity(ScaleLevel.class, "select e from tsadv$ScaleLevel e\n" +
                " where e.scale.id = :scaleId and e.levelNumber = (select min(e2.levelNumber) from tsadv$ScaleLevel e2 where e2.scale.id = :scaleId)", map, null);
    }
}

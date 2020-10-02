package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.ui.*;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dictionary.DicNineBoxLevel;
import kz.uco.tsadv.modules.performance.model.NotPersistEntity;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.gui.components.WebFontRateStars;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.fontratestarscomponent.FontRateStarsComponent;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;
import kz.uco.tsadv.service.PerformanceService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssignmentBrowseForManager extends AbstractLookup {

    private static final String IMAGE_CELL_HEIGHT = "50px";

    @Inject
    private ComponentsFactory componentsFactory;


    @Inject
    private Button button;

    @Inject
    private GroupTable<AssignmentExt> assignmentsTable;

    @Inject
    private Metadata metadata;

    @Inject
    private PerformanceService performanceService;

    private Table<NotPersistEntity> table;
    @Inject
    private UserSession userSession;

    private String[] messages;
    @Inject
    private DataManager dataManager;
    @Inject
    private Filterframe filterFrame;

    private CollectionDatasource assignmentAssessmentDs;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFilterFrame();
        messages = new String[]{"", "", "", ""}; //getNineBoxValues();
        //create collectionDatasource
        assignmentAssessmentDs = DsBuilder.create(getDsContext())
                .setJavaClass(NotPersistEntity.class)
                .setId("assignmentAssessmentDs")
                .buildCollectionDatasource();
        //fill collectionDatasource
        fillDs(true);
        //create table
        table = componentsFactory.createComponent(Table.class);
        //set datasource to table
        table.setDatasource(assignmentAssessmentDs);
        //remove default table columns
        while (table.getColumns().iterator().hasNext())
            table.removeColumn(table.getColumns().iterator().next());
        //add custom column in table
        table.addGeneratedColumn("personPhoto",
                entity -> {
                    Image image = WebCommonUtils.setImage(entity.getPerson().getImage(), null, "90px");
                    image.addStyleName("circle-image");
                    return image;
                });
        table.addGeneratedColumn("person", entity -> {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);

            LinkButton personName = componentsFactory.createComponent(LinkButton.class);
            personName.setCaption(entity.getPerson().getFioWithEmployeeNumber());
            personName.setStyleName("ss-div-blue");
            personName.setAction(new BaseAction("redirect-card") {
                @Override
                public void actionPerform(Component component) {
                    UUID assignmentId = entity.getAssignmentId();
                    if (assignmentId != null) {
                        AssignmentExt assignment = commonService.getEntity(AssignmentExt.class, assignmentId);

                        if (assignment != null) {
                            openEditor("person-card", assignment, WindowManager.OpenType.THIS_TAB);
                        } else {
                            showNotification("Assignment is NULL!");
                        }
                    } else {
                        showNotification("PersonID is null!");
                    }
                }
            });

            vBoxLayout.add(personName);

            Label position = componentsFactory.createComponent(Label.class);
            position.setValue(entity.getPosition().getPositionName());
            position.setStyleName("ss-div-dimgrey");

            vBoxLayout.add(position);

            if (entity.getPosition().getManagerFlag()) {
                Label positionStructureInfoLabel = componentsFactory.createComponent(Label.class);
                positionStructureInfoLabel.setValue(String.format(getMessage("AssignmentMyTeamBrowse.positionStructureInfo"), entity.getDirect(), entity.getTotal()));
                positionStructureInfoLabel.setStyleName("ss-icon ss-div-dimgrey");
                positionStructureInfoLabel.setIcon("font-icon:USERS");

                vBoxLayout.add(positionStructureInfoLabel);
            }
            return vBoxLayout;
        });
        table.addGeneratedColumn("rating", entity -> {
            GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
            gridLayout.setColumns(2);
            gridLayout.setRows(3);
            gridLayout.setSpacing(true);
            gridLayout.setWidth("100%");
            gridLayout.setColumnExpandRatio(0, 1);
            gridLayout.setColumnExpandRatio(1, 2);
            Label label;
            WebRateStars webRateStars;
            Double rating = 0d;
            for (int i = 0; i < 3; i++) {
                label = componentsFactory.createComponent(Label.class);
                webRateStars = componentsFactory.createComponent(WebRateStars.class);

                label.setAlignment(Alignment.TOP_RIGHT);
                RateStarsComponent competenceRateStars = (RateStarsComponent) webRateStars.getComponent();
                switch (i) {
                    case 0:
                        label.setValue(getMessage("AssessmentPersons.competenceRating"));
                        rating = entity.getAssessment().getCompetenceRating();
                        break;
                    case 1:
                        label.setValue(getMessage("AssessmentPersons.goalRating"));
                        rating = entity.getAssessment().getGoalRating();
                        break;
                    case 2:
                        label.setValue(getMessage("AssessmentPersons.overallRating"));
                        rating = entity.getAssessment().getGoalRating();
                }
                competenceRateStars.setValue(rating == null ? 0d : rating);
                competenceRateStars.setReadOnly(true);
                competenceRateStars.setListener(newValue -> {
                });
                gridLayout.add(label, 0, i);
                gridLayout.add(webRateStars, 1, i);
            }
            return gridLayout;
        });
        table.addGeneratedColumn("performance", entity -> {
            GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
            gridLayout.setWidth("100%");
            gridLayout.setSpacing(true);
            gridLayout.setMargin(true, false, true, false);
            gridLayout.setColumns(2);
            gridLayout.setRows(4);
            gridLayout.setColumnExpandRatio(0, 1);
            gridLayout.setColumnExpandRatio(1, 12);
            Label label;
            WebFontRateStars webFontRateStars;
            FontRateStarsComponent fontRateStarsComponent;
            for (int i = 0; i < 4; i++) {
                label = componentsFactory.createComponent(Label.class);
                webFontRateStars = componentsFactory.createComponent(WebFontRateStars.class);
                fontRateStarsComponent = (FontRateStarsComponent) webFontRateStars.getComponent();

                label.setAlignment(Alignment.MIDDLE_RIGHT);
                fontRateStarsComponent.setMessages(messages);
                fontRateStarsComponent.setListener(fontRateStarsComponent::setValue);

                Double rate = 0d;
                switch (i) {
                    case 0:
                        label.setValue(getMessage("AssignmentBrowseForManager.performance"));
                        rate = entity.getAssessment().getPerformance();
                        break;
                    case 1:
                        label.setValue(getMessage("AssignmentBrowseForManager.potential"));
                        rate = entity.getAssessment().getPotential();
                        break;
                    case 2:
                        label.setValue(getMessage("AssignmentBrowseForManager.riskOfLoss"));
                        rate = entity.getAssessment().getRiskOfLoss();
                        break;
                    case 3:
                        label.setValue(getMessage("AssignmentBrowseForManager.impactOfLoss"));
                        rate = entity.getAssessment().getImpactOfLoss();
                }
                fontRateStarsComponent.setValue(rate == null ? 1 : (int) Math.round(rate));
                webFontRateStars.setEnabled(true);

                gridLayout.add(label, 0, i);
                gridLayout.add(webFontRateStars, 1, i);
            }
            return gridLayout;
        });
        table.addGeneratedColumn("zp", entity -> {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            Label label = componentsFactory.createComponent(Label.class);
            Label label1 = componentsFactory.createComponent(Label.class);

            hBoxLayout.setAlignment(Alignment.MIDDLE_CENTER);
            label.setValue(entity.getAmount());
            label.addStyleName("bold");
            label1.setValue(entity.getCurrency() == null ? "" : entity.getCurrency().getLangValue());

            hBoxLayout.add(label);
            hBoxLayout.add(label1);
            vBoxLayout.add(hBoxLayout);

            return vBoxLayout;
        });
        table.setColumnHeaderVisible(false);
        table.setWidth("100%");
        table.setHeight("100%");
//        table.setColumnWidth("personPhoto", 115);
//        table.setColumnWidth("person", 360);
//        table.setColumnWidth("rating", 340);
//        table.setColumnWidth("performance", 320);
        RowsCount rowsCount = componentsFactory.createComponent(RowsCount.class);
        rowsCount.setDatasource(assignmentAssessmentDs);
        table.setRowsCount(rowsCount);
        com.vaadin.v7.ui.Table tableVaadin = table.unwrap(com.vaadin.v7.ui.Table.class);
        tableVaadin.addItemClickListener(event -> {
            if (event.isDoubleClick()) {
                LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
                loadContext.setQuery(LoadContext.createQuery("select e from base$AssignmentExt e where e.id = :id")
                        .setParameter("id", ((NotPersistEntity) assignmentAssessmentDs.getItem()).getAssignmentId()))
                        .setView("assignment.card");
                AssignmentExt assignment = dataManager.load(loadContext);
                if (assignment != null) {
                    openEditor("person-card", assignment, WindowManager.OpenType.THIS_TAB, Collections.singletonMap("fromPersonsAssessments", null));
                }
            }
        });

        add(table);
        expand(table);
    }

    private AssignmentExt getAssignment(UUID assignmentId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "and e.id = :aId")
                .setParameter("aId", assignmentId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }

    private void fillDs(boolean myTeam) {
        assignmentAssessmentDs.clear();
        String language = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
        performanceService.getNotPersistEntityList(myTeam, language)
                .forEach(notPersistEntity -> assignmentAssessmentDs.includeItem(notPersistEntity));
    }

    private void initFilterFrame() {
        ((OptionsGroup<Object, Object>) filterFrame.getComponent("myTeamOptionGroupId"))
                .addValueChangeListener((e) -> {
                    fillDs((boolean) e.getValue());
                });
    }

    public Component generateUserImageCell(AssignmentExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    private String[] getNineBoxValues() {
        LoadContext<DicNineBoxLevel> loadContext = LoadContext.create(DicNineBoxLevel.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$DicNineBoxLevel e"));
        List<DicNineBoxLevel> list = dataManager.loadList(loadContext);
        String[] result = new String[list.size() + 1];
        result[0] = getMessage("Font.Rate.empty");
        for (DicNineBoxLevel dnbl : list) {
            result[Integer.valueOf(dnbl.getCode())] = dnbl.getLangValue();
        }
        return result;
    }
}
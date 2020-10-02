package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.amcharts.model.Color;
import com.haulmont.charts.gui.components.charts.RadarChart;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.entity.CompetenceChartEntity;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.service.StatisticsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfCompetence extends EditableFrame {

    @Inject
    protected StatisticsService statisticsService;

    @Inject
    protected ButtonsPanel buttonsPanel;

    public CollectionDatasource competenceChartEntitiesDs;
    public Datasource<AssignmentExt> assignmentDs;
    public CollectionDatasource<CompetenceElement, UUID> competencePersonDs;
    @Named("personCompetenceTable.create")
    protected CreateAction personCompetenceTableCreate;
    @Named("personCompetenceTable.edit")
    protected EditAction personCompetenceTableEdit;
    @Named("personCompetenceTable.remove")
    protected RemoveAction personCompetenceTableRemove;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected RadarChart competenceChartId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        competenceChartId.getGraphs().get(0).setFillColors(Arrays.asList(new Color("#FEC465")));
        competenceChartId.getGraphs().get(1).setFillColors(Arrays.asList(new Color("LEMONCHIFFON")));
        personCompetenceTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
        if (assignmentDs != null) {
            UUID assignmentGroupId = assignmentDs.getItem().getGroup().getId();

            initCompetenceChart(assignmentGroupId);
            personCompetenceTableCreate.setAfterCommitHandler(e ->
                    initCompetenceChart(assignmentGroupId));
            personCompetenceTableEdit.setAfterCommitHandler(e ->
                    initCompetenceChart(assignmentGroupId));
            personCompetenceTableRemove.setAfterRemoveHandler(e ->
                    initCompetenceChart(assignmentGroupId));
        }
    }

    protected void initCompetenceChart(UUID assignmentGroupId) {
        competenceChartEntitiesDs.refresh();
        String language = userSessionSource.getLocale().getLanguage();
        List<CompetenceChartEntity> competenceChartList = statisticsService.getCompetenceChart(assignmentGroupId, language);

        for (CompetenceChartEntity competenceChartEntity : competenceChartList) {
            competenceChartEntitiesDs.includeItem(competenceChartEntity);
        }
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assignmentDs = getDsContext().get("assignmentDs");
        competenceChartEntitiesDs = (CollectionDatasource) getDsContext().get("competenceChartEntitiesDs");
        competencePersonDs = (CollectionDatasource<CompetenceElement, UUID>) getDsContext().get("competencePersonDs");
    }
}
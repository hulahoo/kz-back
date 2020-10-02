package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursDetailed;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursSummary;
import kz.uco.tsadv.service.TimecardService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_WorkedHoursDetailedListener")
public class WorkedHoursDetailedListener implements BeforeInsertEntityListener<WorkedHoursDetailed>, BeforeDeleteEntityListener<WorkedHoursDetailed> {

    @Inject
    private DataManager dataManager;
    @Inject
    private TimecardService timecardService;

    @Override
    public void onBeforeInsert(WorkedHoursDetailed entity, EntityManager entityManager) {
        WorkedHoursSummary workedHoursSummaryFull = getFilledEntity(entity.getWorkedHoursSummary());

        if (workedHoursSummaryFull != null) {
            entity.setWorkedHoursSummary(workedHoursSummaryFull);
            entity.setTimecardHeader(workedHoursSummaryFull.getTimecardHeader());
        }

        if (entity.getIsNeedToCheckAndCreateAdditionalHours() != null && entity.getIsNeedToCheckAndCreateAdditionalHours()) {
            timecardService.checkAndCreateSpecialHours(entity);

            /* adds hours to summary - only from created detail - not created night, holiday, etc. details */
            WorkedHoursSummary summary = entity.getWorkedHoursSummary();
            summary.setHours(summary.getHours() + entity.getHours());
            entityManager.merge(summary);
        }

    }

    private WorkedHoursSummary getFilledEntity(WorkedHoursSummary workedHoursSummary) {
        LoadContext<WorkedHoursSummary> loadContext = LoadContext.create(WorkedHoursSummary.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$WorkedHoursSummary e" +
                " where e.id = :entityId")
                .setParameter("entityId", workedHoursSummary.getId()))
                .setView("workedHoursSummary-with-timecardHeader");
        return dataManager.load(loadContext);
    }

    @Override
    public void onBeforeDelete(WorkedHoursDetailed entity, EntityManager entityManager) {
        WorkedHoursSummary summary = entity.getWorkedHoursSummary();
        if (entity.getScheduleElementType().getCode().equals("WORK_HOURS")) {
            summary.setHours(summary.getHours() - entity.getHours());
            entityManager.merge(summary);
        }
    }
}
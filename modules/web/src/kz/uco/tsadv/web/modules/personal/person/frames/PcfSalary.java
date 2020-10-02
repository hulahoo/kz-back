package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.service.SalariesPeriodChangerService;
import kz.uco.tsadv.service.StatisticsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class PcfSalary extends EditableFrame {

    @Inject
    protected Metadata metadata;

    @Inject
    protected StatisticsService statisticsService;

    @Inject
    protected ButtonsPanel buttonsPanel;

    @Named("personSalaryTable.create")
    protected CreateAction personSalaryTableCreate;

    @Named("personSalaryTable.remove")
    protected RemoveAction personSalaryTableRemove;

    @Inject
    protected Table<Salary> personSalaryTable;

    @Inject
    protected SalariesPeriodChangerService salariesPeriodChangerService;

    @Named("personSalaryTable.edit")
    protected EditAction personSalaryTableEdit;

    public CollectionDatasource<Salary, UUID> salaryDs;
    //public CollectionDatasource salaryChartEntitiesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        personSalaryTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("assignmentGroupDs") != null
                        ? ParamsMap.of("assignmentGroup", getDsContext().get("assignmentGroupDs").getItem())
                        : null);

        Consumer cons = (Consumer) params.get("consumer");

        personSalaryTableCreate.setAfterCommitHandler(entity -> {
            List<Salary> list = salariesPeriodChangerService.getExistingSalaries((Salary) entity);
            for (Salary salary : list) {
                if (!salary.equals(entity)) {
                    changeExistingSalary(salary, (Salary) entity);
                    salaryDs.commit();
                    salaryDs.refresh();
                }
            }
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        });

        personSalaryTableEdit.setAfterCommitHandler(entity ->

        {

            List<Salary> list = salariesPeriodChangerService.getExistingSalaries((Salary) entity);

            if (list.size() > 1) {
                for (Salary salary : list) {
                    if (!salary.equals(entity)) {
                        changeExistingSalary(salary, (Salary) entity);
                        salaryDs.commit();
                        salaryDs.refresh();
                    }
                }
            } else {
                showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
            }
        });

        salaryDs.addItemChangeListener(e -> {
            Salary salary = salaryDs.getItems().stream().max((o1, o2) ->
                    o1.getStartDate().after(o2.getStartDate()) ? 1 : -1).get();
            personSalaryTableRemove.setEnabled(e.getItem() != null &&
                    e.getItem().getId().equals(salary.getId()));
        });

        personSalaryTableRemove.setAfterRemoveHandler(removedItems -> {
            Object removedItem = removedItems.stream().findFirst().orElse(null);
            Date endDate = ((Salary) removedItem).getEndDate();
            Salary salary = salaryDs.getItems().stream().max((o1, o2) ->
                    o1.getStartDate().after(o2.getStartDate()) ? 1 : -1).get();
            salary.setEndDate(endDate);
            dataManager.commit(salary);
            salaryDs.refresh();
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        salaryDs = (CollectionDatasource<Salary, UUID>) getDsContext().get("salaryDs");
    }


    public void changeExistingSalary(Salary existingSalary, Salary newSalary) {
        Date newSalaryStartDate = newSalary.getStartDate();
        Date newSalaryEndDate = newSalary.getEndDate();
        Date existingSalaryStartDate = existingSalary.getStartDate();
        Date existingSalaryEndDate = existingSalary.getEndDate();
        excludeFromIntegration(existingSalary);
        if ((newSalaryStartDate.before(existingSalaryStartDate) || newSalaryStartDate.equals(existingSalaryStartDate)) &&
                (newSalaryEndDate.after(existingSalaryEndDate) || newSalaryEndDate.equals(existingSalaryEndDate))) {
            salaryDs.removeItem(existingSalary);

        } else if ((newSalaryStartDate.before(existingSalaryEndDate) || newSalaryStartDate.equals(existingSalaryEndDate)) &&
                (newSalaryEndDate.after(existingSalaryEndDate) || newSalaryEndDate.equals(existingSalaryEndDate))) {
            Date existingSalaryEndDateChanged = subtractOneDayFromDate(newSalaryStartDate);
            Salary newExistingSalary = createSecondExistingSalary(existingSalary);
            newExistingSalary.setEndDate(existingSalaryEndDateChanged);
            excludeFromIntegration(newExistingSalary);
            salaryDs.removeItem(existingSalary);
            salaryDs.addItem(newExistingSalary);

        } else if ((newSalaryStartDate.before(existingSalaryStartDate) || newSalaryStartDate.equals(existingSalaryStartDate)) &&
                (newSalaryEndDate.after(existingSalaryStartDate) || newSalaryEndDate.equals(existingSalaryStartDate))) {
            Salary newExistingSalary = createSecondExistingSalary(existingSalary);
            Date existingSalaryStartDayChanged = addOneDayToDate(newSalaryEndDate);
            newExistingSalary.setStartDate(existingSalaryStartDayChanged);
            excludeFromIntegration(newExistingSalary);
            salaryDs.removeItem(existingSalary);
            salaryDs.addItem(newExistingSalary);

        } else if (newSalaryStartDate.after(existingSalaryStartDate) && newSalaryEndDate.before(existingSalaryEndDate)) {
            Salary firstExistingSalary = createSecondExistingSalary(existingSalary);
            Salary secondExistingSalary = createSecondExistingSalary(existingSalary);
            firstExistingSalary.setEndDate(subtractOneDayFromDate(newSalaryStartDate));
            secondExistingSalary.setStartDate(addOneDayToDate(newSalaryEndDate));
            excludeFromIntegration(firstExistingSalary, secondExistingSalary);
            salaryDs.addItem(firstExistingSalary);
            salaryDs.addItem(secondExistingSalary);
            salaryDs.removeItem(existingSalary);

        }

    }

    protected void excludeFromIntegration(StandardEntity... entities) { //todo transfer to service
        if (entities !=null) {
            for (StandardEntity e : entities) {
                e.setUpdatedBy(e.getUpdatedBy() + "_integrationOff"); //todo вынести в какую нибудь глобальную переменную
            }
        }
    }

    protected Date subtractOneDayFromDate(Date date) {
        Date result;
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate().minusDays(1);
        result = java.sql.Date.valueOf(localDate);
        return result;
    }

    protected Date addOneDayToDate(Date date) {
        Date result;
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate().plusDays(1);
        result = java.sql.Date.valueOf(localDate);
        return result;
    }

    protected Salary createSecondExistingSalary(Salary existingSalary) {
        Salary salary = metadata.create(Salary.class);
        salary.setReason(existingSalary.getReason());
        salary.setAgreement(existingSalary.getAgreement());
        salary.setOrderGroup(existingSalary.getOrderGroup());
        salary.setStartDate(existingSalary.getStartDate());
        salary.setEndDate(existingSalary.getEndDate());
        salary.setNetGross(existingSalary.getNetGross());
        salary.setOrdAssignment(existingSalary.getOrdAssignment());
        salary.setAssignmentGroup(existingSalary.getAssignmentGroup());
        salary.setAmount(existingSalary.getAmount());
        salary.setCurrency(existingSalary.getCurrency());
        salary.setType(existingSalary.getType());
        salary.setLegacyId(existingSalary.getLegacyId());
        return salary;
    }

}
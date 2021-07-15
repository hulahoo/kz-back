package kz.uco.tsadv.web.modules.personal.absencebalance;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.CallStoredFunctionService;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

import javax.inject.Inject;
import java.util.*;

public class AbsenceBalanceBrowse extends EditableFrame {
    @Inject
    protected Button createButton;
    @Inject
    protected Button editButton;
    @Inject
    protected Button removeButton;
    @Inject
    private CollectionDatasource<AbsenceBalance, UUID> absenceBalancesDs;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Inject
    protected Table<AbsenceBalance> absenceBalancesTable;
    @Inject
    protected CommonService commonService;
    @Inject
    protected AbsenceBalanceService absenceBalanceService;
    @Inject
    protected ButtonsPanel buttonsPanel;
    @Inject
    protected Label label;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Label vacationDurationType;

    protected PersonGroupExt personGroup;
    protected com.vaadin.v7.ui.Table unwrappedTable;

    @Override
    public void init(Map<String, Object> params) {
        absenceBalancesDs = (CollectionDatasource<AbsenceBalance, UUID>) getDsContext().get("absenceBalancesDs");
        Objects.requireNonNull(getDsContext().get("absenceBalancesVDs")).refresh();
        absenceBalancesTable.repaint();
        personGroup = (PersonGroupExt) Objects.requireNonNull(getDsContext().get("personGroupDs")).getItem();
//        absenceBalancesVDs = (CollectionDatasource<AbsenceBalanceV, UUID>) getDsContext().get("absenceBalancesVDs");
        unwrappedTable = absenceBalancesTable.unwrap(com.vaadin.v7.ui.Table.class);
        unwrappedTable.addItemClickListener(event -> {
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
        });

        vacationDurationType.setStyleName("float-right");
        vacationDurationType.setValue(getVacationDurationType());

        recount();

        /*absenceBalancesTable.getAction("remove").setEnabled(false);

        absenceBalancesDs.addItemChangeListener(e -> {
            if (e.getItem() != null && isLast(e.getItem())) {
                absenceBalancesTable.getAction("remove").setEnabled(true);
            } else {
                absenceBalancesTable.getAction("remove").setEnabled(false);
            }
        });
        absenceBalancesDs.refresh();
        setHeight("100%");
        Map<String, Object> initParams = new HashMap<>();

        initParams.put("personGroupId", personGroupId);
        Integer balanceDays = absenceBalanceService.getBalanceDays(personGroupId, null);
        Integer additionalBalanceDays = absenceBalanceService.getAdditionalBalanceDays(personGroupId);
        if (additionalBalanceDays != null) {
            additionalBalanceDays = 0;
        }
        initParams.put("balanceDays", balanceDays);
        initParams.put("additionalBalanceDays", additionalBalanceDays);
        ((CreateAction) absenceBalancesTable.getAction("create")).setInitialValues(initParams);
        ((CreateAction) absenceBalancesTable.getAction("create")).setAfterCommitHandler(entity -> {
            recount();
            absenceBalancesDs.refresh();
        });
        ((EditAction) absenceBalancesTable.getAction("edit"))
                .setAfterWindowClosedHandler((window, closeActionId) -> absenceBalancesDs.refresh());
        ((RemoveAction) absenceBalancesTable.getAction("remove")).setAfterRemoveHandler(removedItems -> {
            recount();
            absenceBalancesDs.refresh();
        });*/
    }

    protected VacationDurationType getVacationDurationType() {
        List<VacationConditions> vacationConditionsList = commonService.getEntities(VacationConditions.class,
                "select v from base$AssignmentExt a" +
                        "   join tsadv$VacationConditions v " +
                        "       on v.positionGroup = a.positionGroup " +
                        " where a.personGroup.id = :personGroupId" +
                        "   and :sysDate between a.startDate and a.endDate " +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and :sysDate between v.startDate and v.endDate " +
                        " order by v.startDate desc",
                ParamsMap.of("personGroupId", personGroup.getId(),
                        "sysDate", CommonUtils.getSystemDate()),
                View.LOCAL);

        VacationDurationType vacationDurationType = null;
        Date maxDate = vacationConditionsList.isEmpty() ? null : vacationConditionsList.get(0).getStartDate();

        for (VacationConditions vacationConditions : vacationConditionsList) {
            if (!Objects.equals(maxDate, vacationConditions.getStartDate())) break;

            vacationDurationType = vacationConditions.getVacationDurationType();

            if (VacationDurationType.CALENDAR.equals(vacationDurationType)) break;
        }

        return vacationDurationType != null ? vacationDurationType : VacationDurationType.CALENDAR;
    }

    protected boolean isLast(AbsenceBalance absenceBalance) {
        return absenceBalancesDs.getItems() != null
                && absenceBalancesDs.getItems().stream().noneMatch(ab -> ab.getDateFrom().after(absenceBalance.getDateFrom()));
    }

    @Override
    public void editable(boolean editable) {
        /*buttonsPanel.setVisible(editable);*/
    }

    @Override
    public void initDatasource() {
    }

    /*public void formAbsenceBalances() {
        List<AbsenceBalance> absenceBalances = new ArrayList<>(absenceBalancesDs.getItems());
        AbsenceBalance lastAbsenceBalance = null;
        if (absenceBalances.size() > 0) {
            lastAbsenceBalance = absenceBalances.get(0);
        }
        createNewBalance(lastAbsenceBalance, 0);
        absenceBalancesDs.refresh();
    }*/

    /* using only in button */
    protected void createNewBalance(AbsenceBalance lastAbsenceBalance, int annualDaysSpentFromPrevious) {
        AbsenceBalance newBalance = metadata.create(AbsenceBalance.class);
        Date dateFrom = personGroup.getPerson().getHireDate();
        Date dateTo = absenceBalanceService.getActualDateTo(dateFrom);
        int balanceDays = absenceBalanceService.getBalanceDays(personGroup, null);
//        int additionalBalanceDays = absenceBalanceService.getAdditionalBalanceDays(personGroupId);
        int annualDaysSpent = 0;
        if (lastAbsenceBalance == null) {
//            annualDaysSpent = absenceBalanceService.getAnnualDaysSpent(personGroupId);
        } else {
            annualDaysSpent = annualDaysSpentFromPrevious;
        }
        int daysLeft = balanceDays - annualDaysSpent;
//        int extraDaysSpent = absenceBalanceService.getAdditionalDaysSpent(personGroupId);
        int extraDaysSpent = 0;
//        int extraDaysLeft = additionalBalanceDays - extraDaysSpent;

        newBalance.setPersonGroup(personGroup);
        newBalance.setDateFrom(dateFrom);
        newBalance.setDateTo(dateTo);
        newBalance.setBalanceDays((double) balanceDays);
//        newBalance.setAdditionalBalanceDays(additionalBalanceDays);
        newBalance.setDaysSpent((double) annualDaysSpent);
        newBalance.setExtraDaysSpent((double) extraDaysSpent);
//        newBalance.setExtraDaysLeft(extraDaysLeft);
        if (daysLeft < 0) {
            daysLeft = 0;
        }
        newBalance.setDaysLeft((double) daysLeft);
        dataManager.commit(newBalance);
    }

    public void recount() {
        if (personGroup != null) {
//            callRefreshPersonBalanceSqlFunction();
            absenceBalancesDs.refresh();
            absenceBalancesTable.repaint();
        }
    }

    public void createAbsenceBalance() {
        AbsenceBalance absenceBalance = metadata.create(AbsenceBalance.class);
        absenceBalancesDs.getItems().stream().max((o1, o2) ->
                o1.getDateFrom().after(o2.getDateFrom()) ? 1 : -1).ifPresent(absenceBalanceV -> {
            absenceBalance.setBalanceDays(absenceBalanceV.getBalanceDays());
            absenceBalance.setAdditionalBalanceDays(absenceBalanceV.getAdditionalBalanceDays());
        });
        absenceBalance.setPersonGroup(personGroup);
        Window.Editor absenceBalanceEditor = openEditor(absenceBalance, WindowManager.OpenType.THIS_TAB);
        absenceBalanceEditor.addCloseListener(actionId -> {
            recount();
        });
    }

    public void editAbsenceBalance() {
        if (absenceBalancesTable.getSingleSelected() != null) {
            AbsenceBalance absenceBalance = commonService.getEntity(AbsenceBalance.class, absenceBalancesTable.getSingleSelected().getId());
            if (absenceBalance != null) {
                Window.Editor absenceBalanceEditor = openEditor(absenceBalance, WindowManager.OpenType.THIS_TAB);
                absenceBalanceEditor.addCloseListener(actionId -> {
                    recount();
                });
            }
        }
    }

    protected void callRefreshPersonBalanceSqlFunction() {
        String sql = " select bal.refresh_person_balance('" + personGroup.getId().toString() + "')";
        callStoredFunctionService.execCallSqlFunction(sql);
    }

    public void removeAbsencebalance() {
        if (absenceBalancesTable.getSingleSelected() != null) {
            AbsenceBalance absenceBalance = commonService.getEntity(AbsenceBalance.class, absenceBalancesTable.getSingleSelected().getId());
            if (absenceBalance != null) {
                dataManager.remove(absenceBalance);
                recount();
                editButton.setEnabled(false);
                removeButton.setEnabled(false);
            }
        }
    }

    public void refresh() {
        recount();
    }
}
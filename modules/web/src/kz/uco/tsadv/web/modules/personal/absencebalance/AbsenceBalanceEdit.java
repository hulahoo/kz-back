package kz.uco.tsadv.web.modules.personal.absencebalance;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.service.CallStoredFunctionService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class AbsenceBalanceEdit extends AbstractEditor<AbsenceBalance> {

    @Inject
    protected CommonService commonService;
    @Inject
    protected FieldGroup fieldGroup;
    @Named("fieldGroup.balanceDays")
    protected TextField<Integer> balanceDaysField;

    protected boolean tooShortAbsenceBalance;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        fieldGroup.getFieldNN("dateFrom").addValidator(value -> {
            if (value != null && getItem().getDateFrom() != null) {
                Date dateFrom = (Date) value;
                if (dateFrom.after(getItem().getDateTo()))
                    throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));
            }
        });

        fieldGroup.getFieldNN("dateTo").addValidator(value -> {
            if (value != null && getItem().getDateTo() != null) {
                Date dateTo = (Date) value;
                if (dateTo.before(getItem().getDateFrom()))
                    throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
            }
        });

    }

    @Override
    protected void initNewItem(AbsenceBalance item) {
        super.initNewItem(item);
        item.setBalanceDays(0);
        final PersonExt person = item.getPersonGroup().getPerson();
        if (item.getAdditionalBalanceDays() != null && item.getAdditionalBalanceDays() == 0) {
            item.setAdditionalBalanceDays(0);
        }
        if (person != null) {
            final List<AbsenceBalance> balances = commonService.getEntities(AbsenceBalance.class, "select e from tsadv$AbsenceBalance e\n" +
                    "                where e.personGroup.id = :id\n" +
                    "                order by e.dateTo DESC", ParamsMap.of("id", item.getPersonGroup().getId()), View.LOCAL);
            if (balances.isEmpty()) {
                this.setDatesOnEmptyBalances(person.getHireDate(), item);
            } else {
                this.setDatesOnNotEmptyBalances(balances, item);
            }
        }
    }

    @Override
    public void ready() {
        super.ready();
        /*if (overallBalanceDaysField.getValue()==null) {
            overallBalanceDaysField.setValue(getItem().getBalanceDays());
        }*/
        balanceDaysField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                Integer overallBalanceDays = e.getValue();
                Integer difference = 0;
                if (getItem().getOverallBalanceDays() != null && getItem().getOverallBalanceDays() != 0) {
                    difference = getItem().getOverallBalanceDays() - getItem().getBalanceDays();
                    if (difference < 0) {
                        difference = 0;
                    }
                }
                if (overallBalanceDays < difference) {
                    tooShortAbsenceBalance = true;
                } else {
                    tooShortAbsenceBalance = false;
                    getItem().setBalanceDays(overallBalanceDays - difference);
                }
            }
        });

//        setInvisibleFields();
    }

    protected void setInvisibleFields() {
        if (isInvisible()) {
            FieldGroup fieldGroup = (FieldGroup) getComponentNN("fieldGroup");
            fieldGroup.getFieldNN("daysSpent").setVisible(false);
            fieldGroup.getFieldNN("daysLeft").setVisible(false);
            fieldGroup.getFieldNN("extraDaysSpent").setVisible(false);
            fieldGroup.getFieldNN("extraDaysLeft").setVisible(false);
        }
    }

    protected boolean isInvisible() {
        return !PersistenceHelper.isNew(getItem());
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (tooShortAbsenceBalance) {
            errors.add(getMessage("tooShortAbsenceErrorMessage"));
        }
    }

    private void setDatesOnNotEmptyBalances(final List<AbsenceBalance> balances, final AbsenceBalance item) {
        final AbsenceBalance absenceBalance = balances.get(0);
        final Date dateFrom = DateUtils.addDays(DateUtils.addYears(absenceBalance.getDateFrom(), 1), 0);
        final Date dateTo = DateUtils.addDays(DateUtils.addYears(dateFrom, 1), -1);
        item.setDateFrom(dateFrom);
        item.setDateTo(dateTo);
    }

    private void setDatesOnEmptyBalances(final Date hireDate, final AbsenceBalance item) {
        Optional.ofNullable(hireDate)
                .map(date -> {
                    item.setDateFrom(date);
                    return date;
                })
                .map(date -> DateUtils.addDays(DateUtils.addYears(date, 1), -1))
                .ifPresent(item::setDateTo);
    }
    private void callRefreshPersonBalanceSqlFunction() {
        String sql = " select bal.refresh_person_balance('" + getItem().getPersonGroup().getId().toString() + "')";
        callStoredFunctionService.execCallSqlFunction(sql);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
//        callRefreshPersonBalanceSqlFunction();
        if (committed && close) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }

}
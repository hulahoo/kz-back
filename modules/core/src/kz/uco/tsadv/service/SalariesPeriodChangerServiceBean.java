package kz.uco.tsadv.service;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import kz.uco.tsadv.modules.personal.model.Salary;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(SalariesPeriodChangerService.NAME)
public class SalariesPeriodChangerServiceBean implements SalariesPeriodChangerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private MetadataTools metadataTools;

    @Override
    public List<Salary> getExistingSalaries(Salary newSalary) {
        LoadContext<Salary> loadContext = LoadContext.create(Salary.class);
        loadContext.setQuery(LoadContext.createQuery("select o " +
                "from tsadv$Salary o " +
                "where o.assignmentGroup.id = :assignmentGroupId " +
                "and ((o.startDate <= :endDate and o.startDate >= :startDate) " +
                "or (o.endDate <= :endDate and o.endDate >= :startDate) " +
                "or (o.startDate >= :startDate and o.endDate <= :endDate) " +
                "or (o.startDate <= :endDate and o.endDate >= :endDate))")
                .setParameter("assignmentGroupId", newSalary.getAssignmentGroup().getId())
                .setParameter("startDate", newSalary.getStartDate())
                .setParameter("endDate", newSalary.getEndDate()))
                .setView("salary.view");
        return dataManager.loadList(loadContext);
    }

    @Override
    public void changeExistingSalary(Salary existingSalary, Salary newSalary) {
        /*List<Entity> commitInstances = new ArrayList<>();
        Date newSalaryStartDate = newSalary.getStartDate();
        Date newSalaryEndDate = newSalary.getEndDate();
        Date existingSalaryStartDate = existingSalary.getStartDate();
        Date existingSalaryEndDate = existingSalary.getEndDate();
        if ((newSalaryStartDate.before(existingSalaryStartDate) || newSalaryStartDate.equals(existingSalaryStartDate)) &&
                (newSalaryEndDate.after(existingSalaryEndDate) || newSalaryEndDate.equals(existingSalaryEndDate))) {

            System.out.println("Case 1");
            dataManager.remove(existingSalary);

        } else if ((newSalaryStartDate.before(existingSalaryEndDate) || newSalaryStartDate.equals(existingSalaryEndDate)) &&
                (newSalaryEndDate.after(existingSalaryEndDate) || newSalaryEndDate.equals(existingSalaryEndDate))) {

            System.out.println("Case 2");
            Date existingSalaryEndDateChanged = subtractOneDayFromDate(newSalaryStartDate);
            existingSalary.setEndDate(existingSalaryEndDateChanged);
            commitInstances.add(existingSalary);

        } else if ((newSalaryStartDate.before(existingSalaryStartDate) || newSalaryStartDate.equals(existingSalaryStartDate)) &&
                (newSalaryEndDate.after(existingSalaryStartDate) || newSalaryEndDate.equals(existingSalaryStartDate))) {

            System.out.println("Case 3");
            Date existingSalaryStartDayChanged = addOneDayToDate(newSalaryEndDate);
            existingSalary.setStartDate(existingSalaryStartDayChanged);
            commitInstances.add(existingSalary);

        } else if (newSalaryStartDate.after(existingSalaryStartDate) && newSalaryEndDate.before(existingSalaryEndDate)) {

            System.out.println("Case 4");
            Salary firstExistingSalary = createSecondExistingSalary(existingSalary);
            Salary secondExistingSalary = createSecondExistingSalary(existingSalary);
            firstExistingSalary.setEndDate(subtractOneDayFromDate(newSalaryStartDate));
            secondExistingSalary.setStartDate(addOneDayToDate(newSalaryEndDate));
            commitInstances.add(firstExistingSalary);
            commitInstances.add(secondExistingSalary);
            dataManager.remove(existingSalary);

        }

        if (!commitInstances.isEmpty()) {
            System.out.println("Commit instances size: " + commitInstances.size());
            dataManager.commit(new CommitContext(commitInstances));
        }*/

    }

    /*private Date subtractOneDayFromDate(Date date) {
        Date result;
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate().minusDays(1);
        result = java.sql.Date.valueOf(localDate);
        return result;
    }

    private Date addOneDayToDate(Date date) {
        Date result;
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate().plusDays(1);
        result = java.sql.Date.valueOf(localDate);
        return result;
    }

    private void persistNewSalaries(List<Salary> salaries) {
        CommitContext commitContext = new CommitContext(salaries);
        dataManager.commit(commitContext);
    }

    private Salary createSecondExistingSalary(Salary existingSalary) {
        System.out.println("Creating second existing salary");
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
        return salary;
    }*/


}

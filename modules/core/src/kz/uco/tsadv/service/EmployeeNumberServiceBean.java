package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.AssignmentRequest;
import kz.uco.tsadv.entity.AssignmentSalaryRequest;
import kz.uco.tsadv.entity.generator.GeneratorEmployeeNumber;
import kz.uco.tsadv.entity.tb.TemporaryTranslationRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service(EmployeeNumberService.NAME)
public class EmployeeNumberServiceBean implements EmployeeNumberService {

    public static final String BPM_REQUEST_NUMBER = "bpmRequestNumber";

    private static final List<Class<? extends Entity<?>>> listClassWithRequestNumber =
            Arrays.asList(PositionChangeRequest.class,
                    AssignmentRequest.class,
                    AssignmentSalaryRequest.class,
                    SalaryRequest.class,
                    TemporaryTranslationRequest.class,
                    PersonalDataRequest.class,
                    AddressRequest.class,
                    AbsenceRequest.class);

    @Inject
    protected CommonService commonService;

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected UniqueNumbersService uniqueNumbersService;

    @Override
    public String generateEmployeeNumber(DicPersonType type) {
        GeneratorEmployeeNumber generatorEmployeeNumber = getGeneratorEmployeeNumber(type);

        String queryString = "select MAX(e.employeeNumber) from base$PersonExt e " +
                "where e.employeeNumber REGEXP :reg " +
                " and LENGTH(e.employeeNumber) = ( select MAX(LENGTH(p.employeeNumber)) from base$PersonExt p  " +
                "                                   where p.employeeNumber REGEXP :reg )";
        Map<String, Object> params = new HashMap<>();
        params.put("reg", getRegularExpressionForEmployeeNumber(generatorEmployeeNumber));

        String employeeNumber = commonService.emQuerySingleRelult(String.class, queryString, params);

        StringBuilder nextEmployeeNumber = new StringBuilder();
        if (generatorEmployeeNumber != null && generatorEmployeeNumber.getPrefix() != null) {
            nextEmployeeNumber.append(generatorEmployeeNumber.getPrefix());
        }

        int number = 0;
        if (employeeNumber != null) {
            number = Integer.parseInt(employeeNumber.substring((
                            generatorEmployeeNumber != null ? generatorEmployeeNumber.getPrefix() != null ?
                                    generatorEmployeeNumber.getPrefix().length() : 0 : 0),
                    employeeNumber.length() - (generatorEmployeeNumber != null ? generatorEmployeeNumber.getSuffix() != null ?
                            generatorEmployeeNumber.getSuffix().length() : 0 : 0)));
        }
        nextEmployeeNumber.append(1 + number);
        if (generatorEmployeeNumber != null && generatorEmployeeNumber.getSuffix() != null) {
            nextEmployeeNumber.append(generatorEmployeeNumber.getSuffix());
        }

        return nextEmployeeNumber.toString();
    }

    @Override
    public boolean hasEmployeeNumber(String employeeNumber) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select count(e) from base$PersonExt e " +
                            "where e.employeeNumber = :employeeNumber ");
            query.setParameter("employeeNumber", employeeNumber);
            query.setParameter("date", CommonUtils.getEndOfTime());
            return (Long) query.getSingleResult() > 0;
        });
    }

    @Override
    public boolean hasEmployeeNumber(String employeeNumber, PersonExt personExt) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select count(e) from base$PersonExt e " +
                            "where e.employeeNumber = :employeeNumber and e.group.id <> :id ");
            query.setParameter("employeeNumber", employeeNumber);
            query.setParameter("id", personExt.getGroup().getId());
            return (Long) query.getSingleResult() > 0;
        });
    }

    @Override
    public String getRegularExpressionForEmployeeNumber(DicPersonType type) {
        GeneratorEmployeeNumber generatorEmployeeNumber = getGeneratorEmployeeNumber(type);
        return getRegularExpressionForEmployeeNumber(generatorEmployeeNumber);
    }

    @Override
    public GeneratorEmployeeNumber getGeneratorEmployeeNumber(DicPersonType type) {
        return commonService.getEntity(
                GeneratorEmployeeNumber.class,
                "select e.generatorEmployeeNumber from tsadv$GeneratorEmployeeNumberDefiner e " +
                        "where e.personType.id = :typeId ",
                ParamsMap.of("typeId", type.getId()),
                View.LOCAL
        );
    }

    @Override
    public String increaseAssignmentNumber(String previousAssignmentNumber) {
        if (StringUtils.isBlank(previousAssignmentNumber)) {
            return "";
        }
        String[] splitted = previousAssignmentNumber.split("-");

        if (splitted.length == 1) {
            return splitted[0] + "-" + "1";
        }

        if (splitted.length > 1) {
            splitted[splitted.length - 1] = String.valueOf((Integer.parseInt(splitted[splitted.length - 1])) + 1);
            return splitted[0] + "-" + splitted[splitted.length - 1];
        } else {
            return "";
        }
    }

    @Override
    public String getLastAssignmentNumber(PersonGroupExt personGroupId) {
        if (personGroupId == null) {
            return "";
        }
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "   SELECT e.assignmentNumber\n" +
                            "   FROM base$AssignmentGroupExt e\n" +
                            "   JOIN base$AssignmentExt a ON a.group.id = e.id\n" +
                            "   WHERE a.personGroup.id = :personGroupId");
            query.setParameter("personGroupId", personGroupId.getId());
            List<String> results = ((List<String>) query.getResultList())
                    .stream().filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!results.isEmpty()) {
                return Collections.max(results, String.CASE_INSENSITIVE_ORDER);
            } else return "";
        });
    }

    @Override
    public String getRegularExpressionForEmployeeNumber(GeneratorEmployeeNumber generatorEmployeeNumber) {
        StringBuilder reg = new StringBuilder();
        reg.append("^");
        if (generatorEmployeeNumber != null && generatorEmployeeNumber.getPrefix() != null) {
            reg.append(generatorEmployeeNumber.getPrefix());
        }
        reg.append("[[:digit:]]+");
        if (generatorEmployeeNumber != null && generatorEmployeeNumber.getSuffix() != null) {
            reg.append(generatorEmployeeNumber.getSuffix());
        }
        reg.append("$");
        return reg.toString();
    }

    @Override
    public Long generateNextRequestNumber() {
        return persistence.callInTransaction(em ->
                uniqueNumbersService.getNextNumber(BPM_REQUEST_NUMBER));
    }

    @Override
    public boolean hasRequestNumber(Long requestNumber, UUID entityId) {
        return persistence.callInTransaction(em -> {
            boolean needCheckId = entityId != null;

            Query query = em.createNativeQuery(getQuery(needCheckId));
            query.setParameter(1, requestNumber);
            if (needCheckId) {
                query.setParameter(2, entityId);
            }

            return (Long) query.getSingleResult() > 0;
        });
    }

    protected String getQuery(boolean needCheckId) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select ");
        for (int i = 0; i < listClassWithRequestNumber.size(); i++) {
            queryBuilder.append(i != 0 ? " + " : "")
                    .append(" ( select count(*) from ")
                    .append(metadata.getTools().getDatabaseTable(metadata.getClassNN(listClassWithRequestNumber.get(i))))
                    .append(" where request_number = ?1 ")
                    .append(needCheckId ? " and id <> ?2 " : " ")
                    .append(')').append('\n');
        }
        return queryBuilder.toString();
    }

    @Override
    public String getConvertedEmployeeNumber(@Nullable String employeeNumber) {
        if (StringUtils.isBlank(employeeNumber)) return employeeNumber;
        int length = employeeNumber.length();
        switch (length) {
            case 1: {
                return "000" + employeeNumber;
            }
            case 2: {
                return "00" + employeeNumber;
            }
            case 3: {
                return "0" + employeeNumber;
            }
            default: {
                return employeeNumber;
            }
        }
    }
}
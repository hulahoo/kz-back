package kz.uco.tsadv.importer;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */
@Component(AbsenceImporter.NAME)
@Scope("prototype")
public class AbsenceImporter extends XlsImporter {

    @Inject
    private CommonService commonService;

    public static final String NAME = "tsadv_AbsenceImporter";

    public static final String PERSON_NAME = "personName";
    public static final String EMPLOYEE_NUMBER = "employeeNumber";
    public static final String TYPE = "type";
    public static final String DATE_FROM = "dateFrom";
    public static final String DATE_TO = "dateTo";
    public static final String ABSENCE_DAYS = "absenceDays";

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(PERSON_NAME, i++);
        columns.put(EMPLOYEE_NUMBER, i++);
        columns.put(TYPE, i++);
        columns.put(DATE_FROM, i++);
        columns.put(DATE_TO, i++);
        columns.put(ABSENCE_DAYS, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        return null;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, PERSON_NAME);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();

        Absence absence = metadata.create(Absence.class);
        /*DbHelper.existedEntity(em, Absence.class,
                ParamsMap.of(DOCUMENT, XlsHelper.getParameterValue(values, DOCUMENT)))


        create absence if not found in the database
        if (absence == null) {
            absence = metadata.create(Absence.class);
        }*/

        absence.setPersonGroup(parsePersonGroup(XlsHelper.getParameterValue(values, PERSON_NAME), XlsHelper.getParameterValue(values, EMPLOYEE_NUMBER)));
        absence.setType(parseType(XlsHelper.getParameterValue(values, TYPE)));
        absence.setDateFrom(XlsHelper.getParameterValue(values, DATE_FROM));
        absence.setDateTo(XlsHelper.getParameterValue(values, DATE_TO));
        absence.setAbsenceDays(XlsHelper.getParameterValue(values, ABSENCE_DAYS));

        result.add(absence);

        return result;
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            logHelper.info("Created absence: ", "tsadv$Absence", "tsadv$Absence.edit", (UUID) e.getId());
        }
    }

    private PersonGroupExt parsePersonGroup(String fullName, String employeeNumber) {
        Map<String, Object> params = new HashMap<>();
        String[] names = fullName.toLowerCase().split("\\s");
        params.put("lastName", names[0]);
        params.put("firstName", names[1]);
        if (names.length > 2)
            params.put("middleName", names[2]);
        params.put("sysdate", CommonUtils.getSystemDate());
        if (employeeNumber != null && employeeNumber.trim().length() > 0)
            params.put("employeeNumber", employeeNumber);

        String queryString = "select e " +
                " from base$PersonExt e " +
                " where lower(e.lastName) = :lastName " +
                " and lower(e.firstName) = :firstName " +
                (params.containsKey("middleName") ? " and lower(e.middleName) = :middleName " : " and e.middleName is null ") +
                " and :sysdate between e.startDate and e.endDate " +
                (params.containsKey("employeeNumber") ? " and e.employeeNumber = :employeeNumber " : " and e.employeeNumber is null ");

        PersonExt person = commonService.getEntity(PersonExt.class,
                queryString,
                params,
                "person.full");

        return person == null ? null : person.getGroup();
    }

    private DicAbsenceType parseType(String code) {
        return commonService.getEntity(DicAbsenceType.class, code);
    }
}

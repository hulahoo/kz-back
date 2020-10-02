package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.importer.utils.DbHelper;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.dictionary.DicCitizenship;
import kz.uco.tsadv.modules.personal.dictionary.DicMaritalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author veronika.buksha
 */
@Component(PersonImporter.NAME)
@Scope("prototype")
public class PersonImporter extends XlsImporter {

    public static final String NAME = "tsadv_PersonImporter";

    public static final String LEGACY_ID = "legacyID";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String FIRST_NAME_LATIN = "firstNameLatin";
    public static final String LAST_NAME_LATIN = "lastNameLatin";
    public static final String MIDDLE_NAME_LATIN = "middleNameLatin";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String NATIONAL_IDENTIFIER = "nationalIdentifier";
    public static final String PERSON_TYPE_CODE = "personTypeCode";
    public static final String EMPLOYEE_NUMBER = "employeeNumber";
    public static final String SEX_CODE = "sexCode";
    public static final String MARITAL_STATUS_CODE = "maritalStatusCode";
    public static final String NATIONALITY_CODE = "nationalityCode";
    public static final String CITIZENSHIP_CODE = "citizenshipCode";
    public static final String HIRE_DATE = "hireDate";

    @Inject
    protected CommonService commonService;

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(LEGACY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(FIRST_NAME, i++);
        columns.put(LAST_NAME, i++);
        columns.put(MIDDLE_NAME, i++);
        columns.put(FIRST_NAME_LATIN, i++);
        columns.put(LAST_NAME_LATIN, i++);
        columns.put(MIDDLE_NAME_LATIN, i++);
        columns.put(DATE_OF_BIRTH, i++);
        columns.put(NATIONAL_IDENTIFIER, i++);
        columns.put(PERSON_TYPE_CODE, i++);
        columns.put(EMPLOYEE_NUMBER, i++);
        columns.put(SEX_CODE, i++);
        columns.put(MARITAL_STATUS_CODE, i++);
        columns.put(NATIONALITY_CODE, i++);
        columns.put(CITIZENSHIP_CODE, i++);
        columns.put(HIRE_DATE, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(START_DATE, CommonUtils.getSystemDate());
        defaults.put(END_DATE, CommonUtils.getEndOfTime());
        return defaults;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, LEGACY_ID);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();
        Persistence persistence = getPersistence();

        try (Transaction trx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            List<PersonExt> persons = DbHelper.existedEntities(em,
                    PersonExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                            "deleteTs", null));

            PersonGroupExt personGroup = persons != null && !persons.isEmpty() ? persons.get(0).getGroup() : null;
            PersonExt person = DbHelper.existedEntity(em, PersonExt.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    START_DATE, XlsHelper.getParameterValue(values, START_DATE),
                    END_DATE, XlsHelper.getParameterValue(values, END_DATE),
                    "deleteTs", null));

            if (personGroup == null) {
                personGroup = metadata.create(PersonGroupExt.class);
            }
            if (person == null) {
                person = metadata.create(PersonExt.class);
                person.setGroup(personGroup);
            }

            person.setStartDate(XlsHelper.getParameterValue(values, START_DATE));
            person.setEndDate(XlsHelper.getParameterValue(values, END_DATE));
            person.setLegacyId(XlsHelper.getParameterStringValue(values, LEGACY_ID));

            person.setFirstName(XlsHelper.getParameterStringValue(values, FIRST_NAME));
            person.setLastName(XlsHelper.getParameterStringValue(values, LAST_NAME));
            person.setMiddleName(XlsHelper.getParameterStringValue(values, MIDDLE_NAME));

            person.setFirstNameLatin(XlsHelper.getParameterStringValue(values, FIRST_NAME_LATIN));
            person.setLastNameLatin(XlsHelper.getParameterStringValue(values, LAST_NAME_LATIN));
            person.setMiddleNameLatin(XlsHelper.getParameterStringValue(values, MIDDLE_NAME_LATIN));

            person.setDateOfBirth(XlsHelper.getParameterValue(values, DATE_OF_BIRTH));
            person.setNationalIdentifier(XlsHelper.getParameterStringValue(values, NATIONAL_IDENTIFIER));
            person.setType(commonService.getEntity(DicPersonType.class, XlsHelper.getParameterStringValue(values, PERSON_TYPE_CODE)));
            person.setEmployeeNumber(XlsHelper.getParameterStringValue(values, EMPLOYEE_NUMBER));
            person.setSex(commonService.getEntity(DicSex.class, XlsHelper.getParameterStringValue(values, SEX_CODE)));
            person.setMaritalStatus(commonService.getEntity(DicMaritalStatus.class, XlsHelper.getParameterStringValue(values, MARITAL_STATUS_CODE)));
            person.setNationality(commonService.getEntity(DicNationality.class, XlsHelper.getParameterStringValue(values, NATIONALITY_CODE)));
            person.setCitizenship(commonService.getEntity(DicCitizenship.class, XlsHelper.getParameterStringValue(values, CITIZENSHIP_CODE)));
            person.setHireDate(XlsHelper.getParameterValue(values, HIRE_DATE));

            result.add(personGroup);
            result.add(person);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof PersonExt)
                logHelper.info("Created person: " + e.getId(), null, null, null);
            else if (e instanceof PersonGroupExt)
                logHelper.info("Created personGroup: " + e.getId(), null, null, null);
        }
    }
}

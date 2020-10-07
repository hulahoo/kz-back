package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.DbHelper;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.dictionary.DicPositionStatus;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.GradeRule;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */
@Component(PositionImporter.NAME)
@Scope("prototype")
public class PositionImporter extends XlsImporter {

    public static final String NAME = "tsadv_PositionImporter";

    public static final String LEGACY_ID = "legacyId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String POSITION_NAME_RU = "positionNameRu";
    public static final String POSITION_NAME_KZ = "positionNameKz";
    public static final String POSITION_NAME_EN = "positionNameEn";
    public static final String POSITION_FULL_NAME_RU = "positionFullNameRu";
    public static final String POSITION_FULL_NAME_KZ = "positionFullNameKz";
    public static final String POSITION_FULL_NAME_EN = "positionFullNameEn";
    public static final String LOCATION_CODE = "locationCode";
    public static final String PAYROLL_CODE = "payrollCode";
    public static final String FTE = "fte";
    public static final String MAX_PERSONS = "maxPersons";
    public static final String POSITION_STATUS_CODE = "positionStatusCode";
    public static final String MANAGER_FLAG = "managerFlag";
    public static final String JOB_LEGACY_ID = "jobLegacyId"; //group
    public static final String GRADE_GROUP_ID = "gradeGroupId"; //group
    public static final String GRADE_RULE_ID = "gradeRuleId";
    public static final String ORGANIZATION_LEGACY_ID = "organizationLegacyId";

    @Inject
    protected CommonService commonService;

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(LEGACY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(POSITION_NAME_RU, i++);
        columns.put(POSITION_NAME_KZ, i++);
        columns.put(POSITION_NAME_EN, i++);
        columns.put(POSITION_FULL_NAME_RU, i++);
        columns.put(POSITION_FULL_NAME_KZ, i++);
        columns.put(POSITION_FULL_NAME_EN, i++);
        columns.put(LOCATION_CODE, i++);
        columns.put(PAYROLL_CODE, i++);
        columns.put(FTE, i++);
        columns.put(MAX_PERSONS, i++);
        columns.put(POSITION_STATUS_CODE, i++);
        columns.put(MANAGER_FLAG, i++);
        columns.put(JOB_LEGACY_ID, i++);
        columns.put(GRADE_GROUP_ID, i++);
        columns.put(GRADE_RULE_ID, i++);
        columns.put(ORGANIZATION_LEGACY_ID, i);

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

            List<PositionExt> positions = DbHelper.existedEntities(em,
                    PositionExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                            "deleteTs", null));

            PositionGroupExt positionGroup = positions != null && !positions.isEmpty() ? positions.get(0).getGroup() : null;
            PositionExt position = DbHelper.existedEntity(em, PositionExt.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    START_DATE, XlsHelper.getParameterValue(values, START_DATE),
                    END_DATE, XlsHelper.getParameterValue(values, END_DATE),
                    "deleteTs", null));

            if (positionGroup == null) {
                positionGroup = metadata.create(PositionGroupExt.class);
            }
            if (position == null) {
                position = metadata.create(PositionExt.class);
                position.setGroup(positionGroup);
            }

            position.setPositionNameLang1(XlsHelper.getParameterStringValue(values, POSITION_NAME_RU));
            position.setPositionNameLang2(XlsHelper.getParameterStringValue(values, POSITION_NAME_KZ));
            position.setPositionNameLang3(XlsHelper.getParameterStringValue(values, POSITION_NAME_EN));

            position.setPositionFullNameLang1(XlsHelper.getParameterStringValue(values, POSITION_FULL_NAME_RU));
            position.setPositionFullNameLang2(XlsHelper.getParameterStringValue(values, POSITION_FULL_NAME_KZ));
            position.setPositionFullNameLang3(XlsHelper.getParameterStringValue(values, POSITION_FULL_NAME_EN));

            position.setStartDate(XlsHelper.getParameterValue(values, START_DATE));
            position.setEndDate(XlsHelper.getParameterValue(values, END_DATE));
            position.setLegacyId(XlsHelper.getParameterStringValue(values, LEGACY_ID));
            position.setLocation(commonService.getEntity(DicLocation.class, XlsHelper.getParameterStringValue(values, LOCATION_CODE)));
            position.setPayroll(commonService.getEntity(DicPayroll.class, XlsHelper.getParameterStringValue(values, PAYROLL_CODE)));
            position.setFte(XlsHelper.getParameterDoubleValue(values, FTE));
            position.setMaxPersons(XlsHelper.getParameterIntegerValue(values, MAX_PERSONS));
            position.setPositionStatus(commonService.getEntity(DicPositionStatus.class, XlsHelper.getParameterStringValue(values, POSITION_STATUS_CODE)));
            position.setManagerFlag(Boolean.parseBoolean(XlsHelper.getParameterStringValue(values, MANAGER_FLAG)));

            List<Job> jobs = DbHelper.existedEntities(em,
                    Job.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, JOB_LEGACY_ID),
                            "deleteTs", null));

            position.setJobGroup(jobs != null && !jobs.isEmpty() ? jobs.get(0).getGroup() : null);

            UUID gradeGroupId = XlsHelper.getParameterUUIDValue(values, GRADE_GROUP_ID);
            GradeGroup gradeGroup = gradeGroupId == null ? null : commonService.getEntity(GradeGroup.class, gradeGroupId);
            position.setGradeGroup(gradeGroup);

            UUID gradeRuleId = XlsHelper.getParameterUUIDValue(values, GRADE_RULE_ID);
            GradeRule gradeRule = gradeRuleId == null ? null : commonService.getEntity(GradeRule.class, gradeRuleId);
            position.setGradeRule(gradeRule);

            List<OrganizationExt> organizations = DbHelper.existedEntities(em,
                    OrganizationExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, ORGANIZATION_LEGACY_ID),
                            "deleteTs", null));
            position.setOrganizationGroupExt(organizations != null && !organizations.isEmpty() ? organizations.get(0).getGroup() : null);

            result.add(positionGroup);
            result.add(position);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof PositionExt)
                logHelper.info("Created position: ", "base$PositionExt", "base$Position.edit", (UUID) e.getId());
            else if (e instanceof PositionGroupExt)
                logHelper.info("Created positionGroup: " + e.getId(), null, null, null);
        }
    }
}

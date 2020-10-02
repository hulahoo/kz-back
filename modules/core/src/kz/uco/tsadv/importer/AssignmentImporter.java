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
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.model.*;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */
@Component(AssignmentImporter.NAME)
@Scope("prototype")
public class AssignmentImporter extends XlsImporter {
    public static final String NAME = "tsadv_AssignmentImporter";

    public static final String LEGACY_ID = "legacyID";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ASSIGN_DATE = "assignDate";
    public static final String PERSON_LEGACY_ID = "personLegacyId";
    public static final String POSITION_LEGACY_ID = "positionLegacyId";
    public static final String ORGANIZATION_LEGACY_ID = "organizationLegacyId";
    public static final String JOB_LEGACY_ID = "jobLegacyId";
    public static final String FTE = "fte";
    public static final String GRADE_GROUP_ID = "gradeGroupId";
    public static final String PRIMARY_FLAG = "primaryFlag";
    public static final String LOCATION_CODE = "locationCode";
    public static final String STATUS_CODE = "statusCode";

    @Inject
    private CommonService commonService;

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(LEGACY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(ASSIGN_DATE, i++);
        columns.put(PERSON_LEGACY_ID, i++);
        columns.put(POSITION_LEGACY_ID, i++);
        columns.put(ORGANIZATION_LEGACY_ID, i++);
        columns.put(JOB_LEGACY_ID, i++);
        columns.put(FTE, i++);
        columns.put(GRADE_GROUP_ID, i++);
        columns.put(PRIMARY_FLAG, i++);
        columns.put(LOCATION_CODE, i++);
        columns.put(STATUS_CODE, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(START_DATE, CommonUtils.getSystemDate());
        defaults.put(END_DATE, CommonUtils.getEndOfTime());
        defaults.put(PRIMARY_FLAG, Boolean.TRUE);
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

            List<AssignmentExt> assignments = DbHelper.existedEntities(em,
                    AssignmentExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                            "deleteTs", null));

            AssignmentGroupExt assignmentGroup = assignments != null && !assignments.isEmpty() ? assignments.get(0).getGroup() : null;
            AssignmentExt assignment = DbHelper.existedEntity(em, AssignmentExt.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    START_DATE, XlsHelper.getParameterValue(values, START_DATE),
                    END_DATE, XlsHelper.getParameterValue(values, END_DATE),
                    "deleteTs", null));

            if (assignmentGroup == null) {
                assignmentGroup = metadata.create(AssignmentGroupExt.class);
            }
            if (assignment == null) {
                assignment = metadata.create(AssignmentExt.class);
                assignment.setGroup(assignmentGroup);
            }

            assignment.setStartDate(XlsHelper.getParameterValue(values, START_DATE));
            assignment.setEndDate(XlsHelper.getParameterValue(values, END_DATE));
            assignment.setLegacyId(XlsHelper.getParameterStringValue(values, LEGACY_ID));
            assignment.setAssignDate(XlsHelper.getParameterValue(values, ASSIGN_DATE));

            List<PersonExt> persons = DbHelper.existedEntities(em,
                    PersonExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, PERSON_LEGACY_ID),
                            "deleteTs", null));
            assignment.setPersonGroup(persons != null && !persons.isEmpty() ? persons.get(0).getGroup() : null);

            List<PositionExt> positions = DbHelper.existedEntities(em,
                    PositionExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, POSITION_LEGACY_ID),
                            "deleteTs", null));
            assignment.setPositionGroup(positions != null && !positions.isEmpty() ? positions.get(0).getGroup() : null);

            List<OrganizationExt> organizations = DbHelper.existedEntities(em,
                    OrganizationExt.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, ORGANIZATION_LEGACY_ID),
                            "deleteTs", null));
            assignment.setOrganizationGroup(organizations != null && !organizations.isEmpty() ? organizations.get(0).getGroup() : null);

            List<Job> jobs = DbHelper.existedEntities(em,
                    Job.class,
                    ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, JOB_LEGACY_ID),
                            "deleteTs", null));
            assignment.setJobGroup(jobs != null && !jobs.isEmpty() ? jobs.get(0).getGroup() : null);

            assignment.setFte(XlsHelper.getParameterDoubleValue(values, FTE));

            UUID gradeGroupId = XlsHelper.getParameterUUIDValue(values, GRADE_GROUP_ID);
            GradeGroup gradeGroup = gradeGroupId == null ? null : commonService.getEntity(GradeGroup.class, gradeGroupId);
            assignment.setGradeGroup(gradeGroup);

            String primaryFlag = XlsHelper.getParameterStringValue(values, PRIMARY_FLAG);
            if (primaryFlag != null && primaryFlag.equalsIgnoreCase("y")) {
                primaryFlag = "true";
            }
            assignment.setPrimaryFlag(Boolean.parseBoolean(primaryFlag));
            assignment.setLocation(commonService.getEntity(DicLocation.class, XlsHelper.getParameterStringValue(values, LOCATION_CODE)));
            assignment.setAssignmentStatus(commonService.getEntity(DicAssignmentStatus.class, XlsHelper.getParameterStringValue(values, STATUS_CODE)));

            result.add(assignmentGroup);
            result.add(assignment);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof AssignmentExt)
                logHelper.info("Created assignment: ", "base$AssignmentExt", "base$AssignmentExt.historyEdit", (UUID) e.getId());
            else if (e instanceof AssignmentGroupExt)
                logHelper.info("Created assignmentGroup: " + e.getId(), null, null, null);
        }
    }
}

package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.DbHelper;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicJobCategory;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.Job;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */

@Component(JobImporter.NAME)
@Scope("prototype")
public class JobImporter extends XlsImporter {

    @Inject
    private CommonService commonService;

    public static final String NAME = "tsadv_JobImporter";

    public static final String LEGACY_ID = "legacyId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String JOB_NAME_RU = "jobNameRu";
    public static final String JOB_NAME_KZ = "jobNameKz";
    public static final String JOB_NAME_EN = "jobNameEn";
    public static final String EMPLOYEE_CATEGORY_CODE = "employeeCategoryCode";
    public static final String JOB_CATEGORY_CODE = "jobCategoryCode";
    public static final String CANDIDATE_REQ_RU = "candidateRequirementsRu";
    public static final String CANDIDATE_REQ_KZ = "candidateRequirementsKz";
    public static final String CANDIDATE_REQ_EN = "candidateRequirementsEn";
    public static final String JOB_DESC_RU = "jobDescriptionLangRu";
    public static final String JOB_DESC_KZ = "jobDescriptionLangKz";
    public static final String JOB_DESC_EN = "jobDescriptionLangEn";

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(LEGACY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(JOB_NAME_RU, i++);
        columns.put(JOB_NAME_KZ, i++);
        columns.put(JOB_NAME_EN, i++);
        columns.put(EMPLOYEE_CATEGORY_CODE, i++);
        columns.put(JOB_CATEGORY_CODE, i++);
        columns.put(CANDIDATE_REQ_RU, i++);
        columns.put(CANDIDATE_REQ_KZ, i++);
        columns.put(CANDIDATE_REQ_EN, i++);
        columns.put(JOB_DESC_RU, i++);
        columns.put(JOB_DESC_KZ, i++);
        columns.put(JOB_DESC_EN, i);

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

            List<Job> jobs = DbHelper.existedEntities(em, Job.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    "deleteTs", null));

            JobGroup jobGroup = jobs != null && !jobs.isEmpty() ? jobs.get(0).getGroup() : null;
            Job job = DbHelper.existedEntity(em, Job.class, ParamsMap.of(LEGACY_ID, XlsHelper.getParameterStringValue(values, LEGACY_ID),
                    START_DATE, XlsHelper.getParameterValue(values, START_DATE),
                    END_DATE, XlsHelper.getParameterValue(values, END_DATE),
                    "deleteTs", null));

            if (jobGroup == null) {
                jobGroup = metadata.create(JobGroup.class);
            }
            if (job == null) {
                job = metadata.create(Job.class);
                job.setGroup(jobGroup);
            }

            job.setJobNameLang1(XlsHelper.getParameterStringValue(values, JOB_NAME_RU));
            job.setJobNameLang2(XlsHelper.getParameterStringValue(values, JOB_NAME_KZ));
            job.setJobNameLang3(XlsHelper.getParameterStringValue(values, JOB_NAME_EN));

            job.setStartDate(XlsHelper.getParameterValue(values, START_DATE));
            job.setEndDate(XlsHelper.getParameterValue(values, END_DATE));
            job.setLegacyId(XlsHelper.getParameterStringValue(values, LEGACY_ID));
            job.setEmployeeCategory(commonService.getEntity(DicEmployeeCategory.class, XlsHelper.getParameterStringValue(values, EMPLOYEE_CATEGORY_CODE)));
            job.setJobCategory(commonService.getEntity(DicJobCategory.class, XlsHelper.getParameterStringValue(values, JOB_CATEGORY_CODE)));

            job.setCandidateRequirementsLang1(XlsHelper.getParameterStringValue(values, CANDIDATE_REQ_RU));
            job.setCandidateRequirementsLang2(XlsHelper.getParameterStringValue(values, CANDIDATE_REQ_KZ));
            job.setCandidateRequirementsLang3(XlsHelper.getParameterStringValue(values, CANDIDATE_REQ_EN));

            job.setJobDescriptionLang1(XlsHelper.getParameterStringValue(values, JOB_DESC_RU));
            job.setJobDescriptionLang2(XlsHelper.getParameterStringValue(values, JOB_DESC_KZ));
            job.setJobDescriptionLang3(XlsHelper.getParameterStringValue(values, JOB_DESC_EN));

            result.add(jobGroup);
            result.add(job);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof Job)
                logHelper.info("Created job: ", "tsadv$Job", "tsadv$Job.edit", (UUID) e.getId());
            else if (e instanceof JobGroup)
                logHelper.info("Created jobGroup: " + e.getId(), null, null, null);
        }
    }
}

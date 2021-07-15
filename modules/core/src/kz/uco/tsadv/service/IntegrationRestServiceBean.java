package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.Group;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicLanguage;
import kz.uco.base.entity.dictionary.*;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.entity.tb.PositionHarmfulCondition;
import kz.uco.tsadv.entity.tb.dictionary.DicPersonQualificationType;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.integration.jsonobject.*;
import kz.uco.tsadv.modules.personal.dictionary.DicMaritalStatus;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.PersonEducation;
import kz.uco.tsadv.modules.recruitment.model.PersonExperience;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(IntegrationRestService.NAME)
public class IntegrationRestServiceBean implements IntegrationRestService {

    @Inject
    protected Persistence persistence;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected RestIntegrationLogService restIntegrationLogService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Inject
    protected IntegrationConfig integrationConfig;
    @Inject
    private FileStorageAPI fileStorageAPI;


    @Transactional
    @Override
    public BaseResult createOrUpdateOrganization(OrganizationDataJson organizationData) {
        String methodName = "createOrUpdateOrganization";
        ArrayList<OrganizationJson> organizationJsons = new ArrayList<>();
        if (organizationData.getOrganizations() != null) {
            organizationJsons = organizationData.getOrganizations();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            List<OrganizationGroupExt> organizationGroupExts = new ArrayList<>();
            for (OrganizationJson organizationJson : organizationJsons) {
                if (organizationJson.getLegacyId() == null || organizationJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no legacyId ");
                }
                if (organizationJson.getCompanyCode() == null || organizationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no companyCode");
                }
                if (organizationJson.getStartDate() == null || organizationJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no startDate");
                }
                if (organizationJson.getEndDate() == null || organizationJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no endDate");
                }
                OrganizationGroupExt organizationGroupExt = organizationGroupExts.stream().filter(organizationGroupExt1 ->
                        organizationGroupExt1.getCompany() != null
                                && organizationGroupExt1.getCompany().getLegacyId() != null
                                && organizationGroupExt1.getCompany().getLegacyId().equals(organizationJson.getCompanyCode())
                                && organizationGroupExt1.getLegacyId().equals(organizationJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (organizationGroupExt == null) {
                    organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                            .query("select e from base$OrganizationGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", organizationJson.getLegacyId(),
                                    "company", organizationJson.getCompanyCode()))
                            .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (organizationGroupExt != null) {
                        for (OrganizationExt organizationExt : organizationGroupExt.getList()) {
                            if (formatter.parse(organizationJson.getStartDate()).before(organizationExt.getEndDate())
                                    && formatter.parse(organizationJson.getEndDate()).after(organizationExt.getStartDate())) {
                                commitContext.addInstanceToRemove(organizationExt);
                            }
//                            transactionalDataManager.remove(organizationExt);
                        }
//                        organizationGroupExt.getList().clear();
                        organizationGroupExts.add(organizationGroupExt);
                    }
                }
                if (organizationGroupExt == null) {
                    organizationGroupExt = metadata.create(OrganizationGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organizationJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, organizationData,
                                "no base$DicCompany with legacyId " + organizationJson.getCompanyCode());
                    }
                    organizationGroupExt.setCompany(company);
                    organizationGroupExt.setLegacyId(organizationJson.getLegacyId());
                    organizationGroupExt.setOrganizationNameLang1(organizationJson.getOrganizationNameLang1());
                    organizationGroupExt.setOrganizationNameLang2(organizationJson.getOrganizationNameLang2());
                    organizationGroupExt.setOrganizationNameLang3(organizationJson.getOrganizationNameLang3());
                    organizationGroupExt.setId(UUID.randomUUID());
                    organizationGroupExt.setList(new ArrayList<>());
                    organizationGroupExts.add(organizationGroupExt);
                }
                OrganizationExt organizationExt = metadata.create(OrganizationExt.class);
                organizationExt.setOrganizationNameLang1(organizationJson.getOrganizationNameLang1());
                organizationExt.setOrganizationNameLang2(organizationJson.getOrganizationNameLang2());
                organizationExt.setOrganizationNameLang3(organizationJson.getOrganizationNameLang3());
                DicOrgType type = null;
                if (organizationJson.getOrganizationType() != null && !organizationJson.getOrganizationType().isEmpty()) {
                    type = dataManager.load(DicOrgType.class).query("select e from base$DicOrgType e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organizationJson.getOrganizationType())
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    organizationExt.setType(type);
                }
                DicLocation location = null;
                if (organizationJson.getOrganizationType() != null && !organizationJson.getOrganizationType().isEmpty()) {
                    location = dataManager.load(DicLocation.class).query("select e from base$DicLocation e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organizationJson.getLocation())
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    organizationExt.setLocation(location);
                }
                organizationExt.setStartDate(organizationJson.getStartDate() != null
                        ? formatter.parse(organizationJson.getStartDate()) : null);
                organizationExt.setEndDate(organizationJson.getEndDate() != null
                        ? formatter.parse(organizationJson.getEndDate()) : null);
                organizationExt.setLegacyId(organizationJson.getLegacyId());
                organizationExt.setGroup(organizationGroupExt);
                organizationExt.setWriteHistory(false);
                organizationGroupExt.getList().add(organizationExt);
            }
            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                OrganizationExt lastOrganizationExt = organizationGroupExt.getList().stream().max((o1, o2) ->
                        o1.getStartDate().after(o2.getStartDate()) ? 1 : -1).get();
                organizationGroupExt.setOrganizationNameLang1(lastOrganizationExt.getOrganizationNameLang1());
                organizationGroupExt.setOrganizationNameLang2(lastOrganizationExt.getOrganizationNameLang2());
                organizationGroupExt.setOrganizationNameLang3(lastOrganizationExt.getOrganizationNameLang3());
                organizationGroupExt.setOrganization(lastOrganizationExt);
                organizationGroupExt.setLocation(lastOrganizationExt.getLocation());
                organizationGroupExt.setOrganizationType(lastOrganizationExt.getType());
                commitContext.addInstanceToCommit(organizationGroupExt);
                for (OrganizationExt organizationExt : organizationGroupExt.getList()) {
                    commitContext.addInstanceToCommit(organizationExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, organizationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, organizationData);
    }

    @Override
    public BaseResult deleteOrganization(OrganizationDataJson organizationData) {
        String methodName = "deleteOrganization";
        BaseResult result = new BaseResult();
        ArrayList<OrganizationJson> organizations = new ArrayList<>();
        if (organizationData.getOrganizations() != null) {
            organizations = organizationData.getOrganizations();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            for (OrganizationJson organization : organizations) {
                if (organization.getLegacyId() == null || organization.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no legacyId ");
                }
                if (organization.getCompanyCode() == null || organization.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizationData,
                            "no companyCode");
                }
                OrganizationGroupExt organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                        .query("select e from base$OrganizationGroupExt e " +
                                " where e.legacyId = :legacyId and e.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", organization.getLegacyId(),
                                "company", organization.getCompanyCode()))
                        .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                if (organizationGroupExt == null) {
                    return prepareError(result, methodName, organizationData,
                            "no organization with legacyId and companyCode : "
                                    + organization.getLegacyId() + " , " + organization.getCompanyCode());
                }
                for (OrganizationExt organizationExt : organizationGroupExt.getList()) {
                    entityManager.remove(organizationExt);
                }
                entityManager.remove(organizationGroupExt);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, organizationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, organizationData);
    }

    @Override
    public BaseResult createOrUpdateJob(JobDataJson jobData) {
        String methodName = "createOrUpdateJob";
        ArrayList<JobJson> jobs = new ArrayList<>();
        if (jobData.getJobs() != null) {
            jobs = jobData.getJobs();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            List<JobGroup> jobGroups = new ArrayList<>();
            for (JobJson jobJson : jobs) {
                if (jobJson.getLegacyId() == null || jobJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, jobData,
                            "no legacyId ");
                }
                if (jobJson.getCompanyCode() == null || jobJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, jobData,
                            "no companyCode");
                }
                JobGroup jobGroup = jobGroups.stream().filter(jobGroup1 ->
                        jobGroup1.getCompany().getLegacyId().equals(jobJson.getCompanyCode())
                                && jobGroup1.getLegacyId().equals(jobJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (jobGroup == null) {
                    jobGroup = dataManager.load(JobGroup.class)
                            .query("select e from tsadv$JobGroup e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", jobJson.getLegacyId(),
                                    "company", jobJson.getCompanyCode()))
                            .view("jobGroup-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (jobGroup != null) {
                        for (Job job : jobGroup.getList()) {
                            commitContext.addInstanceToRemove(job);
                        }
                        jobGroup.getList().clear();
                        jobGroups.add(jobGroup);
                    }
                }
                if (jobGroup == null) {
                    jobGroup = metadata.create(JobGroup.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", jobJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, jobData,
                                "no base$DicCompany with legacyId " + jobJson.getCompanyCode());
                    }
                    jobGroup.setCompany(company);
                    jobGroup.setLegacyId(jobJson.getLegacyId());
                    jobGroup.setJobNameLang1(jobJson.getJobNameLang1());
                    jobGroup.setJobNameLang2(jobJson.getJobNameLang2());
                    jobGroup.setJobNameLang3(jobJson.getJobNameLang3());
                    jobGroup.setId(UUID.randomUUID());
                    jobGroup.setList(new ArrayList<>());
                    jobGroups.add(jobGroup);
                }
                Job job = metadata.create(Job.class);
                job.setJobNameLang1(jobJson.getJobNameLang1());
                job.setJobNameLang2(jobJson.getJobNameLang2());
                job.setJobNameLang3(jobJson.getJobNameLang3());
                job.setLegacyId(jobJson.getLegacyId());
                job.setStartDate(jobJson.getStartDate() != null
                        ? formatter.parse(jobJson.getStartDate()) : null);
                job.setEndDate(jobJson.getEndDate() != null
                        ? formatter.parse(jobJson.getEndDate()) : null);
                job.setLegacyId(jobJson.getLegacyId());
                job.setWriteHistory(false);
                job.setGroup(jobGroup);
                jobGroup.getList().add(job);
            }
            for (JobGroup jobGroup : jobGroups) {
                Job lastJob = jobGroup.getList().stream().max((o1, o2) ->
                        o1.getStartDate().after(o2.getStartDate()) ? 1 : -1).get();
                jobGroup.setJobNameLang1(lastJob.getJobNameLang1());
                jobGroup.setJobNameLang2(lastJob.getJobNameLang2());
                jobGroup.setJobNameLang3(lastJob.getJobNameLang3());
                jobGroup.setJob(lastJob);
                commitContext.addInstanceToCommit(jobGroup);
                for (Job job : jobGroup.getList()) {
                    commitContext.addInstanceToCommit(job);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, jobData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, jobData);
    }

    @Override
    public BaseResult deleteJob(JobDataJson jobData) {
        String methodName = "deleteJob";
        BaseResult result = new BaseResult();
        ArrayList<JobJson> jobJsons = new ArrayList<>();
        if (jobData.getJobs() != null) {
            jobJsons = jobData.getJobs();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            for (JobJson jobJson : jobJsons) {
                if (jobJson.getLegacyId() == null || jobJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, jobData,
                            "no legacyId ");
                }
                if (jobJson.getCompanyCode() == null || jobJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, jobData,
                            "no companyCode");
                }
                JobGroup jobGroup = dataManager.load(JobGroup.class)
                        .query("select e from tsadv$JobGroup e " +
                                " where e.legacyId = :legacyId and e.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", jobJson.getLegacyId(),
                                "company", jobJson.getCompanyCode()))
                        .view("jobGroup-for-integration-rest").list().stream().findFirst().orElse(null);
                if (jobGroup == null) {
                    return prepareError(result, methodName, jobData,
                            "no job with legacyId and companyCode : "
                                    + jobJson.getLegacyId() + " , " + jobJson.getCompanyCode());
                }
                for (Job job : jobGroup.getList()) {
                    entityManager.remove(job);
                }
                entityManager.remove(jobGroup);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, jobData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, jobData);
    }

    @Override
    public BaseResult createOrUpdatePosition(PositionDataJson positionData) {
        String methodName = "createOrUpdatePosition";
        ArrayList<PositionJson> positionJsons = new ArrayList<>();
        if (positionData.getPositions() != null) {
            positionJsons = positionData.getPositions();
        }
        BaseResult result = new BaseResult();
        try {
            List<PositionGroupExt> positionGroupExts = new ArrayList<>();
            CommitContext commitContext = new CommitContext();
            for (PositionJson positionJson : positionJsons) {
                if (positionJson.getLegacyId() == null || positionJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no legacyId ");
                }
                if (positionJson.getCompanyCode() == null || positionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no companyCode");
                }
                if (positionJson.getOrganizationLegacyId() == null || positionJson.getOrganizationLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no organizationLegacyId");
                }
                if (positionJson.getJobLegacyId() == null || positionJson.getJobLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no jobLegacyId");
                }
                PositionGroupExt positionGroupExt = positionGroupExts.stream().filter(positionGroupExt1 ->
                        positionGroupExt1.getList().stream().filter(positionExt1 ->
                                positionExt1.getOrganizationGroupExt() != null
                                        && positionExt1.getOrganizationGroupExt().getLegacyId() != null
                                        && positionExt1.getOrganizationGroupExt().getLegacyId()
                                        .equals(positionJson.getOrganizationLegacyId())
                                        && positionExt1.getOrganizationGroupExt().getCompany() != null
                                        && positionExt1.getOrganizationGroupExt().getCompany().getLegacyId() != null
                                        && positionExt1.getOrganizationGroupExt().getCompany().getLegacyId()
                                        .equals(positionJson.getCompanyCode())).findAny().isPresent()
                                && positionGroupExt1.getLegacyId().equals(positionJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (positionGroupExt == null) {
                    positionGroupExt = dataManager.load(PositionGroupExt.class)
                            .query("select e.group from base$PositionExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.organizationGroupExt.company.legacyId = :company " +
                                    " and e.organizationGroupExt.legacyId = :orgLegacyId")
                            .setParameters(ParamsMap.of("legacyId", positionJson.getLegacyId(),
                                    "company", positionJson.getCompanyCode(),
                                    "orgLegacyId", positionJson.getOrganizationLegacyId()))
                            .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (positionGroupExt != null) {
                        for (PositionExt positionExt1 : positionGroupExt.getList()) {
                            if (formatter.parse(positionJson.getStartDate()).before(positionExt1.getEndDate())
                                    && formatter.parse(positionJson.getEndDate()).after(positionExt1.getStartDate())) {
                                commitContext.addInstanceToRemove(positionExt1);
                            }
                        }
//                        positionGroupExt.getList().clear();
//                        positionGroupExt = dataManager.reload(positionGroupExt, "positionGroupExt-for-integration-rest");
                        positionGroupExts.add(positionGroupExt);
                    }
                }
                if (positionGroupExt == null) {
                    positionGroupExt = metadata.create(PositionGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", positionJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, positionData,
                                "no base$DicCompany with legacyId " + positionJson.getCompanyCode());
                    }
                    positionGroupExt.setCompany(company);
                    positionGroupExt.setLegacyId(positionJson.getLegacyId());
                    positionGroupExt.setId(UUID.randomUUID());
                    positionGroupExt.setList(new ArrayList<>());
                    positionGroupExts.add(positionGroupExt);
                }

                PositionExt positionExt = metadata.create(PositionExt.class);
                positionExt.setPositionFullNameLang1(positionJson.getPositionNameLang1());
                positionExt.setPositionFullNameLang2(positionJson.getPositionNameLang2());
                positionExt.setPositionFullNameLang3(positionJson.getPositionNameLang3());
                positionExt.setPositionNameLang1(positionJson.getPositionNameLang1());
                positionExt.setPositionNameLang2(positionJson.getPositionNameLang2());
                positionExt.setPositionNameLang3(positionJson.getPositionNameLang3());
                positionExt.setWriteHistory(false);
                GradeGroup gradeGroup = null;
                if (positionJson.getGradeLegacyId() != null && !positionJson.getGradeLegacyId().isEmpty()) {
                    gradeGroup = dataManager.load(GradeGroup.class).query("select e from tsadv$GradeGroup e " +
                            " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getGradeLegacyId()))
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    if (gradeGroup == null) {
                        return prepareError(result, methodName, positionData,
                                "no GradeGroup with legacyId " + positionJson.getGradeLegacyId());
                    }
                    positionExt.setGradeGroup(gradeGroup);
                }
                JobGroup jobGroup = null;
                if (positionJson.getJobLegacyId() != null && !positionJson.getJobLegacyId().isEmpty()) {
                    jobGroup = dataManager.load(JobGroup.class).query("select e from tsadv$JobGroup e " +
                            " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getJobLegacyId()))
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    if (jobGroup == null) {
                        return prepareError(result, methodName, positionData,
                                "no JobGroup with legacyId " + positionJson.getJobLegacyId());
                    }
                    positionExt.setJobGroup(jobGroup);
                }
                OrganizationGroupExt organizationGroupExt = null;
                if (positionJson.getOrganizationLegacyId() != null && !positionJson.getOrganizationLegacyId().isEmpty()) {
                    organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                            .query("select e from base$OrganizationGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getOrganizationLegacyId()))
                            .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (organizationGroupExt == null) {
                        return prepareError(result, methodName, positionData,
                                "no OrganizationGroupExt with legacyId " + positionJson.getOrganizationLegacyId());
                    }
                    positionExt.setOrganizationGroupExt(organizationGroupExt);
                }
                DicEmployeeCategory employeeCategory = null;
                if (positionJson.getEmployeeCategoryId() != null && !positionJson.getEmployeeCategoryId().isEmpty()) {
                    employeeCategory = dataManager.load(DicEmployeeCategory.class)
                            .query("select e from tsadv$DicEmployeeCategory e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getEmployeeCategoryId()))
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    positionExt.setEmployeeCategory(employeeCategory);
                }
                DicPositionStatus positionStatus = null;
                if (positionJson.getPositionStatusId() != null && !positionJson.getPositionStatusId().isEmpty()) {
                    positionStatus = dataManager.load(DicPositionStatus.class)
                            .query("select e from tsadv$DicPositionStatus e " +
                                    " where e.legacyId = :legacyId ")
                            .setParameters(ParamsMap.of("legacyId", positionJson.getPositionStatusId()))
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                    positionExt.setPositionStatus(positionStatus);
                }
                positionExt.setLegacyId(positionJson.getLegacyId());
                positionExt.setStartDate(positionJson.getStartDate() != null
                        ? formatter.parse(positionJson.getStartDate()) : null);
                positionExt.setEndDate(positionJson.getEndDate() != null
                        ? formatter.parse(positionJson.getEndDate()) : null);
                positionExt.setFte(positionJson.getFte() != null && !positionJson.getFte().isEmpty()
                        ? Double.parseDouble(positionJson.getFte()) : null);
                positionExt.setMaxPersons(positionJson.getMaxPerson() != null && !positionJson.getMaxPerson().isEmpty()
                        ? Integer.parseInt(positionJson.getMaxPerson()) : null);
                positionExt.setGroup(positionGroupExt);
                positionGroupExt.setGradeGroup(gradeGroup);
                positionGroupExt.setJobGroup(jobGroup);
                positionGroupExt.setOrganizationGroup(organizationGroupExt);
                positionGroupExt.getList().add(positionExt);
            }
            for (PositionGroupExt positionGroupExt : positionGroupExts) {
                commitContext.addInstanceToCommit(positionGroupExt);
                for (PositionExt positionExt : positionGroupExt.getList()) {
                    commitContext.addInstanceToCommit(positionExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, positionData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, positionData);

    }

    @Override
    public BaseResult deletePosition(PositionDataJson positionData) {
        String methodName = "deletePosition";
        BaseResult result = new BaseResult();
        ArrayList<PositionJson> positionJsons = new ArrayList<>();
        if (positionData.getPositions() != null) {
            positionJsons = positionData.getPositions();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PositionGroupExt> positionGroupExts = new ArrayList<>();
            for (PositionJson positionJson : positionJsons) {
                if (positionJson.getLegacyId() == null || positionJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no legacyId ");
                }
                if (positionJson.getCompanyCode() == null || positionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, positionData,
                            "no companyCode");
                }
                PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                        .query("select e.group from base$PositionExt e " +
                                " where e.legacyId = :legacyId and e.organizationGroupExt.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", positionJson.getLegacyId(),
                                "company", positionJson.getCompanyCode()))
                        .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                if (positionGroupExt == null) {
                    return prepareError(result, methodName, positionData,
                            "no position with legacyId and companyCode : "
                                    + positionJson.getLegacyId() + " , " + positionJson.getCompanyCode());
                }
                if (!positionGroupExts.stream().filter(positionGroupExt1 ->
                        positionGroupExt1.getId().equals(positionGroupExt.getId())).findAny().isPresent()) {
                    positionGroupExts.add(positionGroupExt);
                }

            }
            for (PositionGroupExt positionGroupExt : positionGroupExts) {
                for (PositionExt positionExt : positionGroupExt.getList()) {
                    entityManager.remove(positionExt);
                }
                entityManager.remove(positionGroupExt);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, positionData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, positionData);
    }

    @Override
    public BaseResult createOrUpdatePerson(PersonDataJson personData) {
        String methodName = "createOrUpdatePerson";
        ArrayList<PersonJson> personJsons = new ArrayList<>();
        if (personData.getPersons() != null) {
            personJsons = personData.getPersons();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            List<PersonGroupExt> personGroupExts = new ArrayList<>();
            for (PersonJson personJson : personJsons) {
                if (personJson.getLegacyId() == null || personJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personData,
                            "no legacyId ");
                }
                if (personJson.getCompanyCode() == null || personJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personData,
                            "no companyCode");
                }
                if (personJson.getPersonTypeId() == null || personJson.getPersonTypeId().isEmpty()) {
                    return prepareError(result, methodName, personData,
                            "no personTypeId");
                }
                PersonGroupExt personGroupExt = personGroupExts.stream().filter(personGroupExt1 ->
                        personGroupExt1.getCompany().getLegacyId().equals(personJson.getCompanyCode())
                                && personGroupExt1.getLegacyId().equals(personJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (personGroupExt == null) {
                    personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personJson.getLegacyId(),
                                    "company", personJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        for (PersonExt personExt : personGroupExt.getList()) {
                            if (formatter.parse(personJson.getStartDate()).before(personExt.getEndDate())
                                    && formatter.parse(personJson.getEndDate()).after(personExt.getStartDate())) {
                                commitContext.addInstanceToRemove(personExt);
                            }
                        }
//                        personGroupExt.getList().clear();
                        personGroupExts.add(personGroupExt);
                    }
                }
                if (personGroupExt == null) {
                    personGroupExt = metadata.create(PersonGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", personJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, personData,
                                "no base$DicCompany with legacyId " + personJson.getCompanyCode());
                    }
                    personGroupExt.setCompany(company);
                    personGroupExt.setLegacyId(personJson.getLegacyId());
                    personGroupExt.setId(UUID.randomUUID());
                    personGroupExt.setList(new ArrayList<>());
                    personGroupExts.add(personGroupExt);
                }
                PersonExt personExt = metadata.create(PersonExt.class);
                personExt.setLastName(personJson.getLastName());
                personExt.setFirstName(personJson.getFirstName());
                personExt.setMiddleName(personJson.getMiddleName());
                personExt.setLastNameLatin(personJson.getLastNameLatin());
                personExt.setFirstNameLatin(personJson.getFirstNameLatin());
                personExt.setStartDate(personJson.getStartDate() != null && !personJson.getStartDate().isEmpty()
                        ? formatter.parse(personJson.getStartDate()) : null);
                personExt.setEndDate(personJson.getEndDate() != null && !personJson.getEndDate().isEmpty()
                        ? formatter.parse(personJson.getEndDate()) : null);
                personExt.setDateOfDeath(personJson.getDateOfDeath() != null && !personJson.getDateOfDeath().isEmpty()
                        ? formatter.parse(personJson.getDateOfDeath()) : null);
                personExt.setDateOfBirth(personJson.getDateOfBirth() != null && !personJson.getDateOfBirth().isEmpty()
                        ? formatter.parse(personJson.getDateOfBirth()) : null);
                personExt.setHireDate(personJson.getHireDate() != null && !personJson.getHireDate().isEmpty()
                        ? formatter.parse(personJson.getHireDate()) : null);
                personExt.setEmployeeNumber(personJson.getEmployeeNumber());
                personExt.setBirthPlace(personJson.getPlaceOfBirth());
                personExt.setNationalIdentifier(personJson.getNationalIdentifier());
                personExt.setCommitmentsFromPrevJob(personJson.getHasWealthFromPrevEmployer() != null
                        ? Boolean.parseBoolean(personJson.getHasWealthFromPrevEmployer()) ? YesNoEnum.YES
                        : YesNoEnum.NO : null);
                personExt.setHaveNDA(personJson.getHasNdaFromPrevEmployer() != null
                        ? Boolean.parseBoolean(personJson.getHasNdaFromPrevEmployer()) ? YesNoEnum.YES
                        : YesNoEnum.NO : null);
                personExt.setCommitmentsLoan(personJson.getHasLoanFromPrevEmployer() != null
                        ? Boolean.parseBoolean(personJson.getHasLoanFromPrevEmployer()) : null);
                personExt.setCriminalAdministrativeLiability(personJson.getHasCriminalRecord() != null
                        ? Boolean.parseBoolean(personJson.getHasCriminalRecord()) ? YesNoEnum.YES : YesNoEnum.NO : null);
                personExt.setCommitmentsCredit(personJson.getHasCreditFromPrevEmployer() != null
                        ? Boolean.parseBoolean(personJson.getHasCreditFromPrevEmployer()) : null);
                personExt.setLegacyId(personJson.getLegacyId());
                personExt.setWriteHistory(false);
                DicPersonType personType = dataManager.load(DicPersonType.class)
                        .query("select e from tsadv$DicPersonType e " +
                                " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personJson.getPersonTypeId(),
                                "companyCode", personJson.getCompanyCode()))
                        .view(View.BASE).list().stream().findFirst().orElse(null);
                if (personType != null) {
                    personExt.setType(personType);
                } else {
                    return prepareError(result, methodName, personData,
                            "no tsadv$DicPersonType with legacyId " + personJson.getPersonTypeId());
                }
                DicCitizenship citizenship = null;
                if (personJson.getCitizenshipId() != null && !personJson.getCitizenshipId().isEmpty()) {
                    citizenship = dataManager.load(DicCitizenship.class)
                            .query("select e from tsadv$DicCitizenship e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personJson.getCitizenshipId(),
                                    "companyCode", personJson.getCompanyCode()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setCitizenship(citizenship);
                DicNationality nationality = null;
                if (personJson.getNationalityId() != null && !personJson.getNationalityId().isEmpty()) {
                    nationality = dataManager.load(DicNationality.class)
                            .query("select e from tsadv$DicNationality e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personJson.getNationalityId(),
                                    "companyCode", personJson.getCompanyCode()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setNationality(nationality);
                DicSex sex = null;
                if (personJson.getSexId() != null && !personJson.getSexId().isEmpty()) {
                    sex = dataManager.load(DicSex.class)
                            .query("select e from base$DicSex e " +
                                    " where e.legacyId = :legacyId")
                            .setParameters(ParamsMap.of("legacyId", personJson.getSexId()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setSex(sex);
                DicMaritalStatus maritalStatus = null;
                if (personJson.getMaritalStatus() != null && !personJson.getMaritalStatus().isEmpty()) {
                    maritalStatus = dataManager.load(DicMaritalStatus.class)
                            .query("select e from tsadv$DicMaritalStatus e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personJson.getMaritalStatus(),
                                    "companyCode", personJson.getCompanyCode()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setMaritalStatus(maritalStatus);

                personExt.setGroup(personGroupExt);
                personGroupExt.getList().add(personExt);
            }
            for (PersonGroupExt personGroupExt : personGroupExts) {
                commitContext.addInstanceToCommit(personGroupExt);
                for (PersonExt personExt : personGroupExt.getList()) {
                    commitContext.addInstanceToCommit(personExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personData);
    }

    @Override
    public BaseResult deletePerson(PersonDataJson personData) {
        String methodName = "deletePerson";
        BaseResult result = new BaseResult();
        ArrayList<PersonJson> personJsons = new ArrayList<>();
        if (personData.getPersons() != null) {
            personJsons = personData.getPersons();
        }
        try (Transaction transaction = transactionalDataManager.transactions().create()) {
            for (PersonJson personJson : personJsons) {
                List<Entity> removeList = new ArrayList<>();
                List<Entity> removeGroupList = new ArrayList<>();

                if (personJson.getLegacyId() == null || personJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personData,
                            "no legacyId ");
                }
                if (personJson.getCompanyCode() == null || personJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personData,
                            "no companyCode");
                }
                PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                        .query("select e from base$PersonGroupExt e " +
                                " where e.legacyId = :legacyId and e.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", personJson.getLegacyId(),
                                "company", personJson.getCompanyCode()))
                        .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                if (personGroupExt == null) {
                    return prepareError(result, methodName, personData,
                            "not found person");
                }

                for (PersonExt personExt : personGroupExt.getList()) {
                    if (removeList.stream().noneMatch(entity ->
                            entity.getId().equals(personExt.getId()))) {
                        removeList.add(personExt);
                    }
                }
                List<AssignmentExt> assignmentExtList = dataManager.load(AssignmentExt.class)
                        .query("select e from base$AssignmentExt e where e.personGroup.id = :personGroupId")
                        .setParameters(ParamsMap.of("personGroupId", personGroupExt.getId()))
                        .view("assignmentExt-for-integration").list();
                for (AssignmentExt assignment : assignmentExtList) {
                    if (removeList.stream().noneMatch(entity ->
                            entity.getId().equals(assignment.getId()))) {
                        removeList.add(assignment);
                        if (removeList.stream().noneMatch(entity ->
                                entity.getId().equals(assignment.getGroup().getId()))) {
                            removeGroupList.add(assignment.getGroup());
                            for (AssignmentExt assignmentExt : assignment.getGroup().getList()) {
                                if (removeList.stream().noneMatch(entity ->
                                        entity.getId().equals(assignmentExt.getId()))) {
                                    removeList.add(assignmentExt);
                                }
                            }
                        }
                    }
                }
                for (Entity removeInstance : removeList) {
                    transactionalDataManager.remove(removeInstance);
                }
                for (Entity removeInstance : removeGroupList) {
                    transactionalDataManager.remove(removeInstance);
                }
                transactionalDataManager.remove(personGroupExt);
            }
            transaction.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, personData);
    }

    @Override
    public BaseResult createOrUpdateOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData) {
        String methodName = "createOrUpdateOrganizationHierarchyElement";
        ArrayList<HierarchyElementJson> organizationHierarchyElementJsons = new ArrayList<>();
        if (hierarchyElementData.getOrganizationHierarchyElements() != null) {
            organizationHierarchyElementJsons = hierarchyElementData.getOrganizationHierarchyElements();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (HierarchyElementJson organizationHierarchyElementJson : organizationHierarchyElementJsons) {
                if (organizationHierarchyElementJson.getLegacyId() == null
                        || organizationHierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no legacyId ");
                }
                if (organizationHierarchyElementJson.getCompanyCode() == null
                        || organizationHierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no companyCode");
                }
                if (organizationHierarchyElementJson.getSubordinateOrganizationId() == null
                        || organizationHierarchyElementJson.getSubordinateOrganizationId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no subordinateOrganization");
                }
                if (organizationHierarchyElementJson.getParentOrganizationId() == null
                        || organizationHierarchyElementJson.getParentOrganizationId().isEmpty()
                        || organizationHierarchyElementJson.getParentOrganizationId().equals("0")) {
                    return prepareSuccessWithMessage(result, methodName, hierarchyElementData,
                            "not processed");
                }
                HierarchyElementGroup organizationHierarchyElementGroup = null;
                HierarchyElementExt hierarchyElementExt = null;
                if (organizationHierarchyElementGroup == null) {
                    List<HierarchyElementExt> hierarchyElementList = dataManager.load(HierarchyElementExt.class)
                            .query("select e from base$HierarchyElementExt e " +
                                    " where e.legacyId = :legacyId and e.organizationGroup.company.legacyId = :company ")
                            .setParameters(ParamsMap.of("legacyId", organizationHierarchyElementJson.getLegacyId(),
                                    "company", organizationHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list();
                    if (hierarchyElementList.size() > 1) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "more than one hierarchyElement found to update");
                    }
                    hierarchyElementExt = hierarchyElementList.stream().findFirst().orElse(null);
                    if (hierarchyElementExt != null) {
                        organizationHierarchyElementGroup = hierarchyElementExt.getGroup();
                    }
                }
                Hierarchy hierarchy = null;
                hierarchy = dataManager.load(Hierarchy.class).query(
                        "select e from base$Hierarchy e " +
                                " where e.primaryFlag = TRUE")
                        .view("hierarchy.view").list().stream().findFirst().orElse(null);
                hierarchyElementExt.setHierarchy(hierarchy);
                if (hierarchy == null) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no organization hierarchy");
                }
                if (organizationHierarchyElementGroup == null) {
                    organizationHierarchyElementGroup = metadata.create(HierarchyElementGroup.class);
                    organizationHierarchyElementGroup.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                    organizationHierarchyElementGroup.setId(UUID.randomUUID());
                    organizationHierarchyElementGroup.setList(new ArrayList<>());
                }
                if (hierarchyElementExt == null) {
                    hierarchyElementExt = metadata.create(HierarchyElementExt.class);
                    hierarchyElementExt.setGroup(organizationHierarchyElementGroup);
                    hierarchyElementExt.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                    organizationHierarchyElementGroup.getList().add(hierarchyElementExt);
                    commitContext.addInstanceToCommit(organizationHierarchyElementGroup);
                }
                OrganizationGroupExt subordinateOrganizationGroupExt = null;
                if (organizationHierarchyElementJson.getSubordinateOrganizationId() != null &&
                        !organizationHierarchyElementJson.getSubordinateOrganizationId().isEmpty()) {
                    subordinateOrganizationGroupExt = dataManager.load(OrganizationGroupExt.class).query(
                            "select e from base$OrganizationGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode ")
                            .setParameters(ParamsMap.of("companyCode"
                                    , organizationHierarchyElementJson.getCompanyCode()
                                    , "legacyId", organizationHierarchyElementJson.getSubordinateOrganizationId()))
                            .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (subordinateOrganizationGroupExt == null) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "not found subordinateOrganization with legacyId = " +
                                        organizationHierarchyElementJson.getSubordinatePositionId());
                    }
                    hierarchyElementExt.setOrganizationGroup(subordinateOrganizationGroupExt);
                }
                HierarchyElementExt parent = null;
                if (organizationHierarchyElementJson.getParentOrganizationId() != null
                        && !organizationHierarchyElementJson.getParentOrganizationId().isEmpty()) {
                    List<HierarchyElementExt> parentList = dataManager.load(HierarchyElementExt.class).query(
                            "select e from base$HierarchyElementExt e " +
                                    " where e.organizationGroup.legacyId = :legacyId " +
                                    " and e.organizationGroup.company.legacyId  = :companyCode ")
                            .setParameters(ParamsMap.of("legacyId"
                                    , organizationHierarchyElementJson.getParentOrganizationId()
                                    , "companyCode", organizationHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list();
                    if (parentList.size() > 1) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "more than one parent hierarchyElement found " +
                                        organizationHierarchyElementJson.getParentOrganizationId());
                    }
                    parent = parentList.stream().findFirst().orElse(null);
                    if (parent != null) {
                        hierarchyElementExt.setParent(parent);
                        hierarchyElementExt.setParentGroup(parent != null ? parent.getGroup() : null);
                    }
                } else {
                    hierarchyElementExt.setParent(null);
                    hierarchyElementExt.setParentGroup(null);
                }
                try {
                    hierarchyElementExt.setStartDate(organizationHierarchyElementJson.getStartDate() != null
                            ? formatter.parse(organizationHierarchyElementJson.getStartDate()) : null);
                    hierarchyElementExt.setEndDate(organizationHierarchyElementJson.getEndDate() != null
                            ? formatter.parse(organizationHierarchyElementJson.getEndDate()) : null);
                } catch (Exception e) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "not valid date format " +
                                    organizationHierarchyElementJson.getStartDate() + " - "
                                    + organizationHierarchyElementJson.getEndDate());
                }
                hierarchyElementExt.setHierarchy(hierarchy);
                hierarchyElementExt.setWriteHistory(false);
                hierarchyElementExt.setElementType(ElementType.ORGANIZATION);
                if (organizationHierarchyElementGroup.getLegacyId() == null) {
                    organizationHierarchyElementGroup.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                    commitContext.addInstanceToCommit(organizationHierarchyElementGroup);
                }
                commitContext.addInstanceToCommit(hierarchyElementExt);
            }
            for (HierarchyElementJson organizationHierarchyElementJson : organizationHierarchyElementJsons) {
                if (organizationHierarchyElementJson.getParentOrganizationId() != null
                        && !organizationHierarchyElementJson.getParentOrganizationId().isEmpty()) {
                    HierarchyElementExt foundedHierarchyElementExt = commitContext.getCommitInstances().stream().filter(entity ->
                            entity instanceof HierarchyElementExt && ((HierarchyElementExt) entity).getLegacyId()
                                    .equals(organizationHierarchyElementJson.getLegacyId())
                                    && ((HierarchyElementExt) entity).getOrganizationGroup().getCompany().getLegacyId()
                                    .equals(organizationHierarchyElementJson.getCompanyCode())).map(entity ->
                            (HierarchyElementExt) entity).findFirst().orElse(null);
                    HierarchyElementExt parentHierarchyElementExt = commitContext.getCommitInstances().stream().filter(entity ->
                            entity instanceof HierarchyElementExt && ((HierarchyElementExt) entity).getOrganizationGroup().getLegacyId()
                                    .equals(organizationHierarchyElementJson.getParentOrganizationId())
                                    && ((HierarchyElementExt) entity).getOrganizationGroup().getCompany().getLegacyId()
                                    .equals(organizationHierarchyElementJson.getCompanyCode())).map(entity ->
                            (HierarchyElementExt) entity).findFirst().orElse(null);
                    if (foundedHierarchyElementExt != null) {
                        if (foundedHierarchyElementExt.getParent() == null) {
                            if (parentHierarchyElementExt != null) {
                                foundedHierarchyElementExt.setParent(parentHierarchyElementExt);
                                foundedHierarchyElementExt.setParentGroup(parentHierarchyElementExt.getGroup());
                            } else {
                                return prepareError(result, methodName, hierarchyElementData,
                                        "no exist hierarchy element for parentOrganization");
                            }
                        }
                    }
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, hierarchyElementData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, hierarchyElementData);
    }

    protected BaseResult prepareSuccessWithMessage(BaseResult baseResult, String methodName, Serializable params, String message) {
        baseResult.setSuccess(true);
        baseResult.setSuccessMessage(message);
        createLog(methodName, baseResult, params);
        return baseResult;
    }

    @Override
    public BaseResult deleteOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData) {
        String methodName = "deleteOrganizationHierarchyElement";
        BaseResult result = new BaseResult();
        ArrayList<HierarchyElementJson> hierarchyElementJsons = new ArrayList<>();
        if (hierarchyElementData.getOrganizationHierarchyElements() != null) {
            hierarchyElementJsons = hierarchyElementData.getOrganizationHierarchyElements();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<HierarchyElementGroup> hierarchyElementGroups = new ArrayList<>();
            for (HierarchyElementJson hierarchyElementJson : hierarchyElementJsons) {
                if (hierarchyElementJson.getLegacyId() == null || hierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no legacyId ");
                }
                if (hierarchyElementJson.getCompanyCode() == null || hierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no companyCode");
                }
                HierarchyElementGroup hierarchyElementGroup = dataManager.load(HierarchyElementGroup.class)
                        .query("select e.group from base$HierarchyElementExt e " +
                                " where e.legacyId = :legacyId and e.organizationGroup.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", hierarchyElementJson.getLegacyId(),
                                "company", hierarchyElementJson.getCompanyCode()))
                        .view("hierarchyElementGroup-for-integration-rest").list().stream().findFirst().orElse(null);

                if (hierarchyElementGroup == null) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no hierarchy with legacyId and companyCode : "
                                    + hierarchyElementJson.getLegacyId() + " , " + hierarchyElementJson.getCompanyCode());
                }
                if (!hierarchyElementGroups.stream().filter(hierarchyElementGroup1 ->
                        hierarchyElementGroup1.getId().equals(hierarchyElementGroup.getId())).findAny().isPresent()) {
                    hierarchyElementGroups.add(hierarchyElementGroup);
                }

            }
            for (HierarchyElementGroup hierarchyElementGroup : hierarchyElementGroups) {
                for (HierarchyElementExt hierarchyElementExt : hierarchyElementGroup.getList()) {
                    entityManager.remove(hierarchyElementExt);
                }
                entityManager.remove(hierarchyElementGroup);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, hierarchyElementData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, hierarchyElementData);
    }

    @Override
    public BaseResult createOrUpdatePositionHierarchyElement(HierarchyElementDataJson hierarchyElementData) {
        String methodName = "createOrUpdatePositionHierarchyElement";
        ArrayList<HierarchyElementJson> positionHierarchyElementJsons = new ArrayList<>();
        if (hierarchyElementData.getPositionHierarchyElements() != null) {
            positionHierarchyElementJsons = hierarchyElementData.getPositionHierarchyElements();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (HierarchyElementJson positionHierarchyElementJson : positionHierarchyElementJsons) {
                if (positionHierarchyElementJson.getLegacyId() == null
                        || positionHierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no legacyId ");
                }
                if (positionHierarchyElementJson.getCompanyCode() == null
                        || positionHierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no companyCode");
                }
                if (positionHierarchyElementJson.getSubordinatePositionId() == null
                        || positionHierarchyElementJson.getSubordinatePositionId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no subordinatePositionId");
                }
                if (positionHierarchyElementJson.getParentPositionId() == null
                        || positionHierarchyElementJson.getParentPositionId().isEmpty()
                        || positionHierarchyElementJson.getParentPositionId().equals("0")) {
                    return prepareSuccessWithMessage(result, methodName, hierarchyElementData,
                            "not processed");
                }
                HierarchyElementGroup positionHierarchyElementGroup = null;
                HierarchyElementExt hierarchyElementExt = null;
                if (positionHierarchyElementGroup == null) {
                    List<HierarchyElementExt> hierarchyElementList = dataManager.load(HierarchyElementExt.class)
                            .query("select e from base$HierarchyElementExt e " +
                                    " where e.legacyId = :legacyId and e.positionGroup.company.legacyId = :company ")
                            .setParameters(ParamsMap.of("legacyId", positionHierarchyElementJson.getLegacyId(),
                                    "company", positionHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list();
                    if (hierarchyElementList.size() > 1) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "more than one hierarchyElement found to update");
                    }
                    hierarchyElementExt = hierarchyElementList.stream().findFirst().orElse(null);
                    if (hierarchyElementExt != null) {
                        positionHierarchyElementGroup = hierarchyElementExt.getGroup();
                    }
                }
                UUID positionStructureId = positionStructureConfig.getPositionStructureId();
                if (positionStructureId == null) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "set property tal.selfService.positionStructureId for posiiton structure");
                }
                Hierarchy hierarchy = null;
                hierarchy = dataManager.load(Hierarchy.class).query(
                        "select e from base$Hierarchy e " +
                                " where e.id = :positionStructureId")
                        .parameter("positionStructureId", positionStructureId)
                        .view("hierarchy.view").list().stream().findFirst().orElse(null);
                if (positionStructureId == null) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "property tal.selfService.positionStructureId not valid");
                }
                if (positionHierarchyElementGroup == null) {
                    positionHierarchyElementGroup = metadata.create(HierarchyElementGroup.class);
                    positionHierarchyElementGroup.setLegacyId(positionHierarchyElementJson.getLegacyId());
                    positionHierarchyElementGroup.setId(UUID.randomUUID());
                    positionHierarchyElementGroup.setList(new ArrayList<>());
                }
                if (hierarchyElementExt == null) {
                    hierarchyElementExt = metadata.create(HierarchyElementExt.class);
                    hierarchyElementExt.setGroup(positionHierarchyElementGroup);
                    hierarchyElementExt.setLegacyId(positionHierarchyElementJson.getLegacyId());
                    positionHierarchyElementGroup.getList().add(hierarchyElementExt);
                    commitContext.addInstanceToCommit(positionHierarchyElementGroup);
                }
                PositionGroupExt subordinatePositionGroupExt = null;
                if (positionHierarchyElementJson.getSubordinatePositionId() != null &&
                        !positionHierarchyElementJson.getSubordinatePositionId().isEmpty()) {
                    subordinatePositionGroupExt = dataManager.load(PositionGroupExt.class).query(
                            "select e from base$PositionGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode ")
                            .setParameters(ParamsMap.of("companyCode"
                                    , positionHierarchyElementJson.getCompanyCode()
                                    , "legacyId", positionHierarchyElementJson.getSubordinatePositionId()))
                            .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (subordinatePositionGroupExt == null) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "not found subordinatePosition with legacyId = " +
                                        positionHierarchyElementJson.getSubordinatePositionId());
                    }
                    hierarchyElementExt.setPositionGroup(subordinatePositionGroupExt);
                }
                HierarchyElementExt parent = null;
                if (positionHierarchyElementJson.getParentPositionId() != null
                        && !positionHierarchyElementJson.getParentPositionId().isEmpty()) {
                    List<HierarchyElementExt> parentList = dataManager.load(HierarchyElementExt.class).query(
                            "select e from base$HierarchyElementExt e " +
                                    " where e.positionGroup.legacyId = :legacyId " +
                                    " and e.positionGroup.company.legacyId  = :companyCode ")
                            .setParameters(ParamsMap.of("legacyId"
                                    , positionHierarchyElementJson.getParentPositionId()
                                    , "companyCode", positionHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list();
                    if (parentList.size() > 1) {
                        return prepareError(result, methodName, hierarchyElementData,
                                "more than one parent hierarchyElement found " +
                                        positionHierarchyElementJson.getParentPositionId());
                    }
                    parent = parentList.stream().findFirst().orElse(null);
                    if (parent != null) {
                        hierarchyElementExt.setParent(parent);
                        hierarchyElementExt.setParentGroup(parent != null ? parent.getGroup() : null);
                    }
                } else {
                    hierarchyElementExt.setParent(null);
                    hierarchyElementExt.setParentGroup(null);
                }
                try {
                    hierarchyElementExt.setStartDate(positionHierarchyElementJson.getStartDate() != null
                            ? formatter.parse(positionHierarchyElementJson.getStartDate()) : null);
                    hierarchyElementExt.setEndDate(positionHierarchyElementJson.getEndDate() != null
                            ? formatter.parse(positionHierarchyElementJson.getEndDate()) : null);
                } catch (Exception e) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "not valid date format " +
                                    positionHierarchyElementJson.getStartDate() + " - "
                                    + positionHierarchyElementJson.getEndDate());
                }
                hierarchyElementExt.setHierarchy(hierarchy);
                hierarchyElementExt.setWriteHistory(false);
                hierarchyElementExt.setElementType(ElementType.POSITION);
                if (positionHierarchyElementGroup.getLegacyId() == null) {
                    positionHierarchyElementGroup.setLegacyId(positionHierarchyElementJson.getLegacyId());
                    commitContext.addInstanceToCommit(positionHierarchyElementGroup);
                }
                commitContext.addInstanceToCommit(hierarchyElementExt);
            }
            for (HierarchyElementJson positionHierarchyElementJson : positionHierarchyElementJsons) {
                if (positionHierarchyElementJson.getParentPositionId() != null
                        && !positionHierarchyElementJson.getParentPositionId().isEmpty()) {
                    HierarchyElementExt foundedHierarchyElementExt = commitContext.getCommitInstances().stream().filter(entity ->
                            entity instanceof HierarchyElementExt && ((HierarchyElementExt) entity).getLegacyId()
                                    .equals(positionHierarchyElementJson.getLegacyId())
                                    && ((HierarchyElementExt) entity).getPositionGroup().getCompany().getLegacyId()
                                    .equals(positionHierarchyElementJson.getCompanyCode())).map(entity ->
                            (HierarchyElementExt) entity).findFirst().orElse(null);
                    HierarchyElementExt parentHierarchyElementExt = commitContext.getCommitInstances().stream().filter(entity ->
                            entity instanceof HierarchyElementExt && ((HierarchyElementExt) entity).getPositionGroup().getLegacyId()
                                    .equals(positionHierarchyElementJson.getParentPositionId())
                                    && ((HierarchyElementExt) entity).getPositionGroup().getCompany().getLegacyId()
                                    .equals(positionHierarchyElementJson.getCompanyCode())).map(entity ->
                            (HierarchyElementExt) entity).findFirst().orElse(null);
                    if (foundedHierarchyElementExt != null) {
                        if (foundedHierarchyElementExt.getParent() == null) {
                            if (parentHierarchyElementExt != null) {
                                foundedHierarchyElementExt.setParent(parentHierarchyElementExt);
                                foundedHierarchyElementExt.setParentGroup(parentHierarchyElementExt.getGroup());
                            } else {
                                return prepareError(result, methodName, hierarchyElementData,
                                        "no exist hierarchy element for parentPosition");
                            }
                        }
                    }
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, hierarchyElementData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r\n")));
        }
        return prepareSuccess(result, methodName, hierarchyElementData);
    }

    @Override
    public BaseResult deletePositionHierarchyElement(HierarchyElementDataJson hierarchyElementData) {
        String methodName = "deletePositionHierarchyElement";
        BaseResult result = new BaseResult();
        ArrayList<HierarchyElementJson> hierarchyElementJsons = new ArrayList<>();
        if (hierarchyElementData.getPositionHierarchyElements() != null) {
            hierarchyElementJsons = hierarchyElementData.getPositionHierarchyElements();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<HierarchyElementGroup> hierarchyElementGroups = new ArrayList<>();
            for (HierarchyElementJson hierarchyElementJson : hierarchyElementJsons) {
                if (hierarchyElementJson.getLegacyId() == null || hierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no legacyId ");
                }
                if (hierarchyElementJson.getCompanyCode() == null || hierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no companyCode");
                }
                HierarchyElementGroup hierarchyElementGroup = dataManager.load(HierarchyElementGroup.class)
                        .query("select e.group from base$HierarchyElementExt e " +
                                " where e.legacyId = :legacyId and e.positionGroup.id in " +
                                " (select p.group.id from base$PositionExt p " +
                                " where p.organizationGroupExt.company.legacyId = :company)")
                        .setParameters(ParamsMap.of("legacyId", hierarchyElementJson.getLegacyId(),
                                "company", hierarchyElementJson.getCompanyCode()))
                        .view("hierarchyElementGroup-for-integration-rest").list().stream().findFirst().orElse(null);

                if (hierarchyElementGroup == null) {
                    return prepareError(result, methodName, hierarchyElementData,
                            "no hierarchy with legacyId and companyCode : "
                                    + hierarchyElementJson.getLegacyId() + " , " + hierarchyElementJson.getCompanyCode());
                }
                if (!hierarchyElementGroups.stream().filter(hierarchyElementGroup1 ->
                        hierarchyElementGroup1.getId().equals(hierarchyElementGroup.getId())).findAny().isPresent()) {
                    hierarchyElementGroups.add(hierarchyElementGroup);
                }

            }
            for (HierarchyElementGroup hierarchyElementGroup : hierarchyElementGroups) {
                for (HierarchyElementExt hierarchyElementExt : hierarchyElementGroup.getList()) {
                    entityManager.remove(hierarchyElementExt);
                }
                entityManager.remove(hierarchyElementGroup);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, hierarchyElementData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, hierarchyElementData);
    }

    @Override
    public BaseResult prepareSuccess(BaseResult baseResult, String methodName, Serializable params) {
        baseResult.setSuccess(true);
        baseResult.setSuccessMessage("success");
        createLog(methodName, baseResult, params);
        return baseResult;
    }

    @Override
    public BaseResult prepareError(BaseResult baseResult, String methodName, Serializable params, String errorMessage) {
        baseResult.setSuccess(false);
        baseResult.setErrorMessage(errorMessage);
        createLog(methodName, baseResult, params);
        return baseResult;
    }

    protected void createLog(String methodName, BaseResult baseResult, Serializable param) {
//        if (baseResult.getErrorMessage() != null && !baseResult.getErrorMessage().equals("")) {
        UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);
        String login = userSessionSource.getUserSession().getUser().getLogin();
        restIntegrationLogService.log(login, methodName, toJson(param), baseResult.isSuccess()
                ? baseResult.getSuccessMessage() : baseResult.getErrorMessage(), baseResult.isSuccess(), new Date());
//        }
    }

    protected String toJson(Serializable params) {
        return gson.toJson(params);
    }

    @Override
    public BaseResult createOrUpdateAssignment(AssignmentDataJson assignmentDataJson) {
        String methodName = "createOrUpdateAssignment";
        ArrayList<AssignmentJson> assignmentJsons = new ArrayList<>();
        if (assignmentDataJson.getAssignmentJsons() != null) {
            assignmentJsons = assignmentDataJson.getAssignmentJsons();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            List<AssignmentGroupExt> assignmentGroupExtList = new ArrayList<>();
            for (AssignmentJson assignmentJson : assignmentJsons) {
                if (assignmentJson.getLegacyId() == null || assignmentJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no legacyId");
                }
                if (assignmentJson.getCompanyCode() == null || assignmentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no companyCode");
                }
                if (assignmentJson.getPersonId() == null || assignmentJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no personId");
                }
                if (assignmentJson.getOrganizationId() == null || assignmentJson.getOrganizationId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no organizationId");
                }
                if (assignmentJson.getJobId() == null || assignmentJson.getJobId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no jobId");
                }
                if (assignmentJson.getPositionId() == null || assignmentJson.getPositionId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no positionId");
                }
                if (assignmentJson.getStartDate() == null || assignmentJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no startDate");
                }
                if (assignmentJson.getEndDate() == null || assignmentJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no endDate");
                }

                AssignmentGroupExt assignmentGroupExt = assignmentGroupExtList.stream().filter(assignmentGroupExt1 ->
                        assignmentGroupExt1.getList().stream().anyMatch(assignmentExt ->
                                assignmentExt.getPersonGroup() != null
                                        && assignmentExt.getPersonGroup().getCompany() != null
                                        && assignmentExt.getPersonGroup().getCompany().getLegacyId() != null
                                        && assignmentExt.getPersonGroup().getCompany().getLegacyId()
                                        .equals(assignmentJson.getCompanyCode())
                                        && assignmentExt.getPersonGroup().getLegacyId() != null
                                        && assignmentExt.getPersonGroup().getLegacyId()
                                        .equals(assignmentJson.getPersonId())
                        ) && assignmentGroupExt1.getLegacyId().equals(assignmentJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (assignmentGroupExt == null) {
                    assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                            .query("select e.group from base$AssignmentExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.personGroup.company.legacyId = :company " +
                                    " and e.personGroup.legacyId = :personLegacyId")
                            .setParameters(ParamsMap.of("legacyId", assignmentJson.getLegacyId(),
                                    "company", assignmentJson.getCompanyCode(),
                                    "personLegacyId", assignmentJson.getPersonId()))
                            .view("assignmentGroup.view")
                            .list().stream().findFirst().orElse(null);
                    if (assignmentGroupExt != null) {
                        for (AssignmentExt assignmentExt : assignmentGroupExt.getList()) {
                            if (formatter.parse(assignmentJson.getStartDate()).before(assignmentExt.getEndDate())
                                    && formatter.parse(assignmentJson.getEndDate()).after(assignmentExt.getStartDate())) {
                                commitContext.addInstanceToRemove(assignmentExt);
                            }
                        }
//                        assignmentGroupExt.getList().clear();
                        assignmentGroupExtList.add(assignmentGroupExt);
                    }
                }
                if (assignmentGroupExt == null) {
                    assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class)
                            .query("select e from base_DicCompany e " +
                                    " where e.legacyId = :legacyId")
                            .parameter("legacyId", assignmentJson.getCompanyCode())
                            .view(View.BASE)
                            .list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, assignmentDataJson,
                                "no base$DicCompany with legacyId " + assignmentJson.getCompanyCode());
                    } else {
                        assignmentGroupExt.setCompany(company);
                    }
                    assignmentGroupExt.setLegacyId(assignmentJson.getLegacyId());
                    assignmentGroupExt.setId(UUID.randomUUID());
                    assignmentGroupExt.setAssignmentNumber(assignmentJson.getLegacyId());
                    assignmentGroupExt.setList(new ArrayList<>());
                    assignmentGroupExtList.add(assignmentGroupExt);
                }

                AssignmentExt assignmentExt = metadata.create(AssignmentExt.class);
                assignmentExt.setAssignmentStatus(dataManager.load(DicAssignmentStatus.class)
                        .query("select e from tsadv$DicAssignmentStatus e " +
                                " where e.code = 'ACTIVE'")
                        .view(View.BASE)
                        .list().stream().findFirst().orElse(null));
                DicAssignmentStatus dicAssignmentStatus = dataManager.load(DicAssignmentStatus.class)
                        .query("select e from tsadv$DicAssignmentStatus e " +
                                " where e.legacyId = :legacyId")
                        .parameter("legacyId", assignmentJson.getAssignmentStatus())
                        .view(View.BASE)
                        .list().stream().findFirst().orElse(null);
                if (dicAssignmentStatus != null) {
                    assignmentExt.setAssignmentStatus(dicAssignmentStatus);
                }
                PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                        .query("select e from base$PersonGroupExt e " +
                                " where e.legacyId = :pgLegacyId " +
                                " and e.company.legacyId = :companyCode ")
                        .setParameters(ParamsMap.of("pgLegacyId", assignmentJson.getPersonId(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("personGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (personGroupExt != null) {
                    assignmentExt.setPersonGroup(personGroupExt);
                    if (assignmentGroupExt.getPersonGroup() == null
                            || !assignmentGroupExt.getPersonGroup().equals(personGroupExt)) {
                        assignmentGroupExt.setPersonGroup(personGroupExt);
                    }
                } else {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no base$PersonGroupExt with legacyId " + assignmentJson.getPersonId()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                OrganizationGroupExt organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                        .query("select e from base$OrganizationGroupExt e " +
                                " where e.legacyId = :ogLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("ogLegacyId", assignmentJson.getOrganizationId(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("organizationGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (organizationGroupExt != null) {
                    assignmentExt.setOrganizationGroup(organizationGroupExt);
                    if (assignmentGroupExt.getOrganizationGroup() == null
                            || !assignmentGroupExt.getOrganizationGroup().equals(organizationGroupExt)) {
                        assignmentGroupExt.setOrganizationGroup(organizationGroupExt);
                    }
                } else {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no base$OrganizationGroupExt with legacyId " + assignmentJson.getOrganizationId()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                JobGroup jobGroup = dataManager.load(JobGroup.class)
                        .query("select e from tsadv$JobGroup e " +
                                " where e.legacyId = :jgLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("jgLegacyId", assignmentJson.getJobId(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("jobGroup-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (jobGroup != null) {
                    assignmentExt.setJobGroup(jobGroup);
                    if (assignmentGroupExt.getJobGroup() == null || !assignmentGroupExt.getJobGroup().equals(jobGroup)) {
                        assignmentGroupExt.setJobGroup(jobGroup);
                    }
                } else {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no tsadv$JobGroup with legacyId " + assignmentJson.getJobId()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                        .query("select e.group from base$PositionExt e " +
                                " where e.organizationGroupExt.company.legacyId = :companyCode " +
                                " and e.group.legacyId = :legacyId ")
                        .setParameters(ParamsMap.of("companyCode", assignmentJson.getCompanyCode(),
                                "legacyId", assignmentJson.getPositionId()))
                        .view("positionGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (positionGroupExt != null) {
                    assignmentExt.setPositionGroup(positionGroupExt);
                    if (assignmentGroupExt.getPositionGroup() == null
                            || !assignmentGroupExt.getPositionGroup().equals(positionGroupExt)) {
                        assignmentGroupExt.setPositionGroup(positionGroupExt);
                    }
                } else {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no base$PositionGroupExt with legacyId " + assignmentJson.getPositionId()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                GradeGroup gradeGroup = dataManager.load(GradeGroup.class)
                        .query("select e from tsadv$GradeGroup e" +
                                " where e.legacyId = :ggLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("ggLegacyId", assignmentJson.getGradeId(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("gradeGroup.browse")
                        .list().stream().findFirst().orElse(null);
                if (gradeGroup != null) {
                    assignmentExt.setGradeGroup(gradeGroup);
                    if (assignmentGroupExt.getGradeGroup() == null
                            || !assignmentGroupExt.getGradeGroup().equals(gradeGroup)) {
                        assignmentGroupExt.setGradeGroup(gradeGroup);
                    }
                } else {
                    assignmentExt.setGradeGroup(null);
                }
                assignmentExt.setWriteHistory(false);
                assignmentExt.setLegacyId(assignmentJson.getLegacyId());
                assignmentExt.setStartDate(formatter.parse(assignmentJson.getStartDate()));
                assignmentExt.setEndDate(formatter.parse(assignmentJson.getEndDate()));
                assignmentExt.setAssignDate(formatter.parse(assignmentJson.getStartDate()));
                assignmentExt.setFte(assignmentJson.getFte() != null && !assignmentJson.getFte().isEmpty()
                        ? Double.parseDouble(assignmentJson.getFte()) : null);
                assignmentExt.setProbationEndDate(assignmentJson.getProbationPeriodEndDate() != null
                        && !assignmentJson.getProbationPeriodEndDate().isEmpty()
                        ? formatter.parse(assignmentJson.getProbationPeriodEndDate())
                        : null);
                assignmentExt.setPrimaryFlag(Boolean.valueOf(assignmentJson.getPrimaryFlag()));
                assignmentExt.setGroup(assignmentGroupExt);
//                if (assignmentGroupExt.getGradeGroup() == null || !assignmentGroupExt.getGradeGroup().equals(gradeGroup)) {
//                    assignmentGroupExt.setGradeGroup(gradeGroup);
//                }
//                if (assignmentGroupExt.getJobGroup() == null || !assignmentGroupExt.getJobGroup().equals(jobGroup)) {
//                    assignmentGroupExt.setJobGroup(jobGroup);
//                }
//                if (assignmentGroupExt.getOrganizationGroup() == null
//                        || !assignmentGroupExt.getOrganizationGroup().equals(organizationGroupExt)) {
//                    assignmentGroupExt.setOrganizationGroup(organizationGroupExt);
//                }
//                if (assignmentGroupExt.getPositionGroup() == null
//                        || !assignmentGroupExt.getPositionGroup().equals(positionGroupExt)) {
//                    assignmentGroupExt.setPositionGroup(positionGroupExt);
//                }
//                if (assignmentGroupExt.getPersonGroup() == null || !assignmentGroupExt.getPersonGroup().equals(personGroupExt)) {
//                    assignmentGroupExt.setPersonGroup(personGroupExt);
//                }
                assignmentGroupExt.getList().add(assignmentExt);
            }
            for (AssignmentGroupExt assignmentGroupExt : assignmentGroupExtList) {
                commitContext.addInstanceToCommit(assignmentGroupExt);
                for (AssignmentExt assignmentExt : assignmentGroupExt.getList()) {
                    commitContext.addInstanceToCommit(assignmentExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, assignmentDataJson, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, assignmentDataJson);
    }

    @Override
    public BaseResult deleteAssignment(AssignmentDataJson assignmentDataJson) {
        String methodName = "deleteAssignment";
        BaseResult result = new BaseResult();
        ArrayList<AssignmentJson> assignmentJsons = new ArrayList<>();
        if (assignmentDataJson.getAssignmentJsons() != null) {
            assignmentJsons = assignmentDataJson.getAssignmentJsons();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<AssignmentGroupExt> assignmentGroupExts = new ArrayList<>();
            for (AssignmentJson assignmentJson : assignmentJsons) {
                if (assignmentJson.getLegacyId() == null || assignmentJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no legacyId ");
                }
                if (assignmentJson.getCompanyCode() == null || assignmentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no companyCode");
                }
                AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                        .query("select e.group from base$AssignmentExt e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.company.legacyId = :company ")
                        .setParameters(ParamsMap.of("legacyId", assignmentJson.getLegacyId(),
                                "company", assignmentJson.getCompanyCode()))
                        .view("assignmentGroup.view")
                        .list().stream().findFirst().orElse(null);

                if (assignmentGroupExt == null) {
                    return prepareError(result, methodName, assignmentDataJson,
                            "no assignment with legacyId and companyCode : "
                                    + assignmentJson.getLegacyId() + " , " + assignmentJson.getCompanyCode());
                }
                if (!assignmentGroupExts.stream().filter(assignmentGroupExt1 ->
                        assignmentGroupExt1.getId().equals(assignmentGroupExt.getId())).findAny().isPresent()) {
                    assignmentGroupExts.add(assignmentGroupExt);
                }

            }
            for (AssignmentGroupExt assignmentGroupExt : assignmentGroupExts) {
                for (AssignmentExt assignmentExt : assignmentGroupExt.getList()) {
                    entityManager.remove(assignmentExt);
                }
                entityManager.remove(assignmentGroupExt);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, assignmentDataJson, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, assignmentDataJson);
    }

    @Override
    public BaseResult createOrUpdateSalary(SalaryDataJson salaryData) {
        String methodName = "createOrUpdateSalary";
        ArrayList<SalaryJson> salarys = new ArrayList<>();
        if (salaryData.getSalary() != null) {
            salarys = salaryData.getSalary();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (SalaryJson salaryJson : salarys) {
                if (salaryJson.getLegacyId() == null || salaryJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, salaryData,
                            "no legacyId ");
                }
                if (salaryJson.getCompanyCode() == null || salaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, salaryData,
                            "no companyCode");
                }
                Salary salary = dataManager.load(Salary.class)
                        .query("select e from tsadv$Salary e " +
                                " where e.legacyId = :legacyId " +
                                " and e.assignmentGroup.legacyId = :agLegacyId")
                        .setParameters(ParamsMap.of("legacyId", salaryJson.getLegacyId(),
                                "agLegacyId", salaryJson.getAssignmentLegacyId()))
                        .view("salary.view").list().stream().findFirst().orElse(null);
                if (salary != null) {
                    salary.setLegacyId(salaryJson.getLegacyId());
                    AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                            .query("select e.group from base$AssignmentExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.personGroup.company.legacyId = :company ")
                            .setParameters(ParamsMap.of("legacyId", salaryJson.getAssignmentLegacyId(),
                                    "company", salaryJson.getCompanyCode()))
                            .view("assignmentGroup.view")
                            .list().stream().findFirst().orElse(null);
                    if (assignmentGroupExt != null) {
                        salary.setAssignmentGroup(assignmentGroupExt);
                    } else {
                        return prepareError(result, methodName, salaryData,
                                "no base$AssignmentGroupExt with legacyId " + salaryJson.getAssignmentLegacyId()
                                        + " and company legacyId " + salaryJson.getCompanyCode());
                    }
                    salary.setAmount(salaryJson.getAmount() != null && !salaryJson.getAmount().isEmpty()
                            ? Double.valueOf(salaryJson.getAmount().replace(",", "."))
                            : null);
                    DicCurrency dicCurrency = dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.legacyId = :cLegacyId")
                            .setParameters(ParamsMap.of("cLegacyId", salaryJson.getCurrency()))
                            .list().stream().findFirst().orElse(null);
                    if (dicCurrency != null) {
                        salary.setCurrency(dicCurrency);
                    } else {
                        return prepareError(result, methodName, salaryData,
                                "no base$DicCurrency with legacyId " + salaryJson.getCurrency());
                    }
                    salary.setNetGross(GrossNet.fromId(salaryJson.getNetGross() != null
                            && !salaryJson.getNetGross().isEmpty()
                            ? salaryJson.getNetGross()
                            : "GROSS"));
                    salary.setType(SalaryType.fromId(salaryJson.getSalaryType() != null
                            && !salaryJson.getSalaryType().isEmpty()
                            ? salaryJson.getSalaryType()
                            : "MONTHLYRATE"));
                    salary.setStartDate(salaryJson.getStartDate() != null && !salaryJson.getStartDate().isEmpty()
                            ? formatter.parse(salaryJson.getStartDate())
                            : null);
                    salary.setEndDate(salaryJson.getEndDate() != null && !salaryJson.getEndDate().isEmpty()
                            ? formatter.parse(salaryJson.getEndDate())
                            : null);
                    salary.setWriteHistory(false);
                    commitContext.addInstanceToCommit(salary);
                }
                if (salary == null) {
                    salary = metadata.create(Salary.class);
                    salary.setId(UUID.randomUUID());
                    salary.setLegacyId(salaryJson.getLegacyId());
                    AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                            .query("select e.group from base$AssignmentExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.personGroup.company.legacyId = :company ")
                            .setParameters(ParamsMap.of("legacyId", salaryJson.getAssignmentLegacyId(),
                                    "company", salaryJson.getCompanyCode()))
                            .view("assignmentGroup.view")
                            .list().stream().findFirst().orElse(null);
                    if (assignmentGroupExt != null) {
                        salary.setAssignmentGroup(assignmentGroupExt);
                    } else {
                        return prepareError(result, methodName, salaryData,
                                "no base$AssignmentGroupExt with legacyId " + salaryJson.getAssignmentLegacyId()
                                        + " and company legacyId " + salaryJson.getCompanyCode());
                    }
                    salary.setAmount(salaryJson.getAmount() != null && !salaryJson.getAmount().isEmpty()
                            ? Double.valueOf(salaryJson.getAmount().replace(",", "."))
                            : null);
                    DicCurrency dicCurrency = dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.legacyId = :cLegacyId")
                            .setParameters(ParamsMap.of("cLegacyId", salaryJson.getCurrency()))
                            .list().stream().findFirst().orElse(null);
                    if (dicCurrency != null) {
                        salary.setCurrency(dicCurrency);
                    } else {
                        return prepareError(result, methodName, salaryData,
                                "no base$DicCurrency with legacyId " + salaryJson.getCurrency());
                    }
                    salary.setNetGross(GrossNet.fromId(salaryJson.getNetGross() != null
                            && !salaryJson.getNetGross().isEmpty()
                            ? salaryJson.getNetGross()
                            : "GROSS"));
                    salary.setType(SalaryType.fromId(salaryJson.getSalaryType() != null
                            && !salaryJson.getSalaryType().isEmpty()
                            ? salaryJson.getSalaryType()
                            : "MONTHLYRATE"));
                    salary.setStartDate(salaryJson.getStartDate() != null && !salaryJson.getStartDate().isEmpty()
                            ? formatter.parse(salaryJson.getStartDate())
                            : null);
                    salary.setEndDate(salaryJson.getEndDate() != null && !salaryJson.getEndDate().isEmpty()
                            ? formatter.parse(salaryJson.getEndDate())
                            : null);
                    salary.setWriteHistory(false);
                    commitContext.addInstanceToCommit(salary);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, salaryData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, salaryData);
    }

    @Override
    public BaseResult deleteSalary(SalaryDataJson salaryData) {
        String methodName = "deleteSalary";
        BaseResult result = new BaseResult();
        ArrayList<SalaryJson> salarys = new ArrayList<>();
        if (salaryData.getSalary() != null) {
            salarys = salaryData.getSalary();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<Salary> salaries = new ArrayList<>();
            for (SalaryJson salaryJson : salarys) {
                if (salaryJson.getLegacyId() == null || salaryJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, salaryData,
                            "no legacyId ");
                }
                if (salaryJson.getCompanyCode() == null || salaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, salaryData,
                            "no companyCode");
                }
                Salary salary = dataManager.load(Salary.class)
                        .query("select e from tsadv$Salary e " +
                                " where e.legacyId = :legacyId " +
                                " and e.assignmentGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", salaryJson.getLegacyId(),
                                "companyCode", salaryJson.getCompanyCode()))
                        .view("salary.view").list().stream().findFirst().orElse(null);

                if (salary == null) {
                    return prepareError(result, methodName, salaryData,
                            "no salary with legacyId and assignment with companyCode : "
                                    + salaryJson.getLegacyId() + " , " + salaryJson.getCompanyCode());
                }
                if (!salaries.stream().filter(salary1 ->
                        salary1.getId().equals(salary.getId())).findAny().isPresent()) {
                    salaries.add(salary);
                }

            }
            for (Salary salary : salaries) {
                entityManager.remove(salary);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, salaryData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, salaryData);
    }

    @Override
    public BaseResult createOrUpdatePersonDocument(PersonDocumentDataJson personDocumentData) {
        String methodName = "createOrUpdatePersonDocument";
        ArrayList<PersonDocumentJson> personDocuments = new ArrayList<>();
        if (personDocumentData.getPersonDocuments() != null) {
            personDocuments = personDocumentData.getPersonDocuments();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (PersonDocumentJson personDocumentJson : personDocuments) {
                if (personDocumentJson.getLegacyId() == null || personDocumentJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no legacyId");
                }
                if (personDocumentJson.getCompanyCode() == null || personDocumentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no companyCode");
                }
                if (personDocumentJson.getPersonId() == null || personDocumentJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no personId");
                }
                if (personDocumentJson.getDocumentTypeId() == null || personDocumentJson.getDocumentTypeId().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no documentType");
                }
                if (personDocumentJson.getDocumentNumber() == null || personDocumentJson.getDocumentNumber().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no documentNumber");
                }
                if (personDocumentJson.getIssueDate() == null || personDocumentJson.getIssueDate().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no issueDate");
                }
                if (personDocumentJson.getExpiredDate() == null || personDocumentJson.getExpiredDate().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no expiredDate");
                }
                if (personDocumentJson.getIssueAuthorityId() == null || personDocumentJson.getIssueAuthorityId().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no issueAuthority");
                }
//                if (personDocumentJson.getStatus() == null || personDocumentJson.getStatus().isEmpty()) {
//                    return prepareError(result, methodName, personDocumentData,
//                            "no status");
//                }
                PersonDocument personDocument = dataManager.load(PersonDocument.class)
                        .query("select e from tsadv$PersonDocument e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pgLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personDocumentJson.getLegacyId(),
                                "pgLegacyId", personDocumentJson.getPersonId(),
                                "companyCode", personDocumentJson.getCompanyCode()))
                        .view("personDocument.edit").list().stream().findFirst().orElse(null);
                if (personDocument != null) {
                    personDocument.setLegacyId(personDocumentJson.getLegacyId());
                    personDocument.setStartDate(personDocumentJson.getStartDate() != null
                            && !personDocumentJson.getStartDate().isEmpty()
                            ? formatter.parse(personDocumentJson.getStartDate())
                            : null);
                    personDocument.setEndDate(personDocumentJson.getEndDate() != null
                            && !personDocumentJson.getEndDate().isEmpty()
                            ? formatter.parse(personDocumentJson.getEndDate())
                            : null);
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getPersonId(),
                                    "company", personDocumentJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personDocument.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no base$PersonGroupExt with legacyId " + personDocumentJson.getPersonId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    DicDocumentType dicDocumentType = dataManager.load(DicDocumentType.class)
                            .query("select e from tsadv$DicDocumentType e" +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getDocumentTypeId(),
                                    "companyCode", personDocumentJson.getCompanyCode()))
                            .view("dicDocumentType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (dicDocumentType != null) {
                        personDocument.setDocumentType(dicDocumentType);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no tsadv$DicDocumentType with legacyId " + personDocumentJson.getDocumentTypeId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    personDocument.setDocumentNumber(personDocumentJson.getDocumentNumber());
                    personDocument.setIssueDate(formatter.parse(personDocumentJson.getIssueDate()));
                    personDocument.setExpiredDate(formatter.parse(personDocumentJson.getExpiredDate()));
                    personDocument.setIssuedBy(personDocumentJson.getIssueByForExpat());

                    DicIssuingAuthority dicIssuingAuthority = dataManager.load(DicIssuingAuthority.class)
                            .query("select e from tsadv_DicIssuingAuthority e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getIssueAuthorityId(),
                                    "companyCode", personDocumentJson.getCompanyCode()))
                            .view("dicIssuingAuthority.for.integration")
                            .list().stream().findFirst().orElse(null);
                    if (dicIssuingAuthority != null) {
                        personDocument.setIssuingAuthority(dicIssuingAuthority);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no tsadv_DicIssuingAuthority with legacyId " + personDocumentJson.getIssueAuthorityId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    if (personDocumentJson.getStatus() != null && !personDocumentJson.getStatus().isEmpty()) {
                        DicApprovalStatus status = dataManager.load(DicApprovalStatus.class)
                                .query("select e from tsadv$DicApprovalStatus e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.company.legacyId = :companyCode ")
                                .setParameters(ParamsMap.of("legacyId", personDocumentJson.getStatus(),
                                        "companyCode", personDocumentJson.getCompanyCode()))
                                .view("dicApprovalStatus.for.integration")
                                .list().stream().findFirst().orElse(null);
                        if (status != null) {
                            personDocument.setStatus(status);
                        }
                    }
                    commitContext.addInstanceToCommit(personDocument);
                }
                if (personDocument == null) {
                    personDocument = metadata.create(PersonDocument.class);
                    personDocument.setId(UUID.randomUUID());
                    personDocument.setLegacyId(personDocumentJson.getLegacyId());
                    personDocument.setStartDate(personDocumentJson.getStartDate() != null
                            && !personDocumentJson.getStartDate().isEmpty()
                            ? formatter.parse(personDocumentJson.getStartDate())
                            : null);
                    personDocument.setEndDate(personDocumentJson.getEndDate() != null
                            && !personDocumentJson.getEndDate().isEmpty()
                            ? formatter.parse(personDocumentJson.getEndDate())
                            : null);
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getPersonId(),
                                    "company", personDocumentJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personDocument.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no base$PersonGroupExt with legacyId " + personDocumentJson.getPersonId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    DicDocumentType dicDocumentType = dataManager.load(DicDocumentType.class)
                            .query("select e from tsadv$DicDocumentType e" +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getDocumentTypeId(),
                                    "companyCode", personDocumentJson.getCompanyCode()))
                            .view("dicDocumentType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (dicDocumentType != null) {
                        personDocument.setDocumentType(dicDocumentType);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no tsadv$DicDocumentType with legacyId " + personDocumentJson.getDocumentTypeId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    personDocument.setDocumentNumber(personDocumentJson.getDocumentNumber());
                    personDocument.setIssueDate(formatter.parse(personDocumentJson.getIssueDate()));
                    personDocument.setExpiredDate(formatter.parse(personDocumentJson.getExpiredDate()));
                    personDocument.setIssuedBy(personDocumentJson.getIssueByForExpat());

                    DicIssuingAuthority dicIssuingAuthority = dataManager.load(DicIssuingAuthority.class)
                            .query("select e from tsadv_DicIssuingAuthority e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personDocumentJson.getIssueAuthorityId(),
                                    "companyCode", personDocumentJson.getCompanyCode()))
                            .view("dicIssuingAuthority.for.integration")
                            .list().stream().findFirst().orElse(null);
                    if (dicIssuingAuthority != null) {
                        personDocument.setIssuingAuthority(dicIssuingAuthority);
                    } else {
                        return prepareError(result, methodName, personDocumentData,
                                "no tsadv_DicIssuingAuthority with legacyId " + personDocumentJson.getIssueAuthorityId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    if (personDocumentJson.getStatus() != null && !personDocumentJson.getStatus().isEmpty()) {
                        DicApprovalStatus status = dataManager.load(DicApprovalStatus.class)
                                .query("select e from tsadv$DicApprovalStatus e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.company.legacyId = :companyCode ")
                                .setParameters(ParamsMap.of("legacyId", personDocumentJson.getStatus(),
                                        "companyCode", personDocumentJson.getCompanyCode()))
                                .view("dicApprovalStatus.for.integration")
                                .list().stream().findFirst().orElse(null);
                        if (status != null) {
                            personDocument.setStatus(status);
                        }
                    }
                    commitContext.addInstanceToCommit(personDocument);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personDocumentData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personDocumentData);
    }

    @Override
    public BaseResult deletePersonDocument(PersonDocumentDataJson personDocumentData) {
        String methodName = "deletePersonDocument";
        BaseResult result = new BaseResult();
        ArrayList<PersonDocumentJson> personDocuments = new ArrayList<>();
        if (personDocumentData.getPersonDocuments() != null) {
            personDocuments = personDocumentData.getPersonDocuments();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonDocument> personDocumentArrayList = new ArrayList<>();
            for (PersonDocumentJson personDocumentJson : personDocuments) {
                if (personDocumentJson.getLegacyId() == null || personDocumentJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no legacyId");
                }
                if (personDocumentJson.getCompanyCode() == null || personDocumentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDocumentData,
                            "no companyCode");
                }
//                if (personDocumentJson.getPersonId() == null || personDocumentJson.getPersonId().isEmpty()) {
//                    return prepareError(result, methodName, personDocumentData,
//                            "no personId");
//                }

                PersonDocument personDocument = dataManager.load(PersonDocument.class)
                        .query("select e from tsadv$PersonDocument e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personDocumentJson.getLegacyId(),
                                "companyCode", personDocumentJson.getCompanyCode()))
                        .view("personDocument.edit").list().stream().findFirst().orElse(null);

                if (personDocument == null) {
                    return prepareError(result, methodName, personDocumentData,
                            "no personDocument with legacyId and companyCode: "
                                    + personDocumentJson.getLegacyId() +
                                    ", " + personDocumentJson.getCompanyCode());
                }
                if (!personDocumentArrayList.stream().filter(personDocument1 ->
                        personDocument1.getId().equals(personDocument.getId())).findAny().isPresent()) {
                    personDocumentArrayList.add(personDocument);
                }
            }
            for (PersonDocument personDocument : personDocumentArrayList) {
                entityManager.remove(personDocument);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personDocumentData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personDocumentData);
    }

    @Override
    public BaseResult createOrUpdatePersonQualification(PersonQualificationDataJson personQualificationData) {
        String methodName = "createOrUpdatePersonQualification";
        ArrayList<PersonQualificationJson> personQualifications = new ArrayList<>();
        if (personQualificationData.getPersonQualifications() != null) {
            personQualifications = personQualificationData.getPersonQualifications();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (PersonQualificationJson personQualificationJson : personQualifications) {
                if (personQualificationJson.getLegacyId() == null || personQualificationJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no legacyId ");
                }
                if (personQualificationJson.getCompanyCode() == null || personQualificationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no companyCode");
                }
                if (personQualificationJson.getSchool() == null || personQualificationJson.getSchool().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no school");
                }
                if (personQualificationJson.getStartDate() == null || personQualificationJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no startDate");
                }
                if (personQualificationJson.getEndDate() == null || personQualificationJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no endDate");
                }
                if (personQualificationJson.getQualificationTypeId() == null || personQualificationJson.getQualificationTypeId().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no qualificationTypeId");
                }
                if (personQualificationJson.getDocumentNumber() == null || personQualificationJson.getDocumentNumber().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no documentNumber");
                }
                if (personQualificationJson.getDocumentDate() == null || personQualificationJson.getDocumentDate().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no documentDate");
                }
                PersonQualification personQualification = dataManager.load(PersonQualification.class)
                        .query("select e from tsadv$PersonQualification e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personQualificationJson.getLegacyId(),
                                "pLegacyId", personQualificationJson.getPersonId(),
                                "companyCode", personQualificationJson.getCompanyCode()))
                        .view("personQualification-view")
                        .list().stream().findFirst().orElse(null);
                if (personQualification != null) {
                    personQualification.setLegacyId(personQualificationJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personQualificationJson.getPersonId(),
                                    "company", personQualificationJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personQualification.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personQualificationData,
                                "no base$PersonGroupExt with legacyId " + personQualificationJson.getPersonId()
                                        + " and company legacyId " + personQualificationJson.getCompanyCode());
                    }
                    personQualification.setEducationalInstitutionName(personQualificationJson.getSchool());
                    personQualification.setStartDate(formatter.parse(personQualificationJson.getStartDate()));
                    personQualification.setEndDate(formatter.parse(personQualificationJson.getEndDate()));
                    personQualification.setDiploma(personQualificationJson.getDocumentNumber());
                    personQualification.setIssuedDate(formatter.parse(personQualificationJson.getDocumentDate()));
                    DicPersonQualificationType qualificationType = dataManager.load(DicPersonQualificationType.class)
                            .query("select e from tsadv$DicPersonQualificationType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personQualificationJson.getQualificationTypeId(),
                                    "companyCode", personQualificationJson.getCompanyCode()))
                            .view("dicPersonQualificationType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (qualificationType != null) {
                        personQualification.setType(qualificationType);
                    } else {
                        return prepareError(result, methodName, personQualificationData,
                                "no tsadv$DicPersonQualificationType with legacyId " + personQualificationJson.getQualificationTypeId()
                                        + " and company legacyId " + personQualificationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personQualification);
                }
                if (personQualification == null) {
                    personQualification = metadata.create(PersonQualification.class);
                    personQualification.setUuid(UUID.randomUUID());
                    personQualification.setLegacyId(personQualificationJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personQualificationJson.getPersonId(),
                                    "company", personQualificationJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personQualification.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personQualificationData,
                                "no base$PersonGroupExt with legacyId " + personQualificationJson.getPersonId()
                                        + " and company legacyId " + personQualificationJson.getCompanyCode());
                    }
                    personQualification.setEducationalInstitutionName(personQualificationJson.getSchool());
                    personQualification.setStartDate(formatter.parse(personQualificationJson.getStartDate()));
                    personQualification.setEndDate(formatter.parse(personQualificationJson.getEndDate()));
                    personQualification.setDiploma(personQualificationJson.getDocumentNumber());
                    personQualification.setIssuedDate(formatter.parse(personQualificationJson.getDocumentDate()));
                    DicPersonQualificationType qualificationType = dataManager.load(DicPersonQualificationType.class)
                            .query("select e from tsadv$DicPersonQualificationType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personQualificationJson.getQualificationTypeId(),
                                    "companyCode", personQualificationJson.getCompanyCode()))
                            .view("dicPersonQualificationType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (qualificationType != null) {
                        personQualification.setType(qualificationType);
                    } else {
                        return prepareError(result, methodName, personQualificationData,
                                "no tsadv$DicPersonQualificationType with legacyId " + personQualificationJson.getQualificationTypeId()
                                        + " and company legacyId " + personQualificationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personQualification);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personQualificationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personQualificationData);
    }

    @Override
    public BaseResult deletePersonQualification(PersonQualificationDataJson personQualificationData) {
        String methodName = "deletePersonQualification";
        BaseResult result = new BaseResult();
        ArrayList<PersonQualificationJson> personQualifications = new ArrayList<>();
        if (personQualificationData.getPersonQualifications() != null) {
            personQualifications = personQualificationData.getPersonQualifications();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonQualification> personQualificationArrayList = new ArrayList<>();
            for (PersonQualificationJson personQualificationJson : personQualifications) {
                if (personQualificationJson.getLegacyId() == null || personQualificationJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no legacyId");
                }
                if (personQualificationJson.getCompanyCode() == null || personQualificationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no companyCode");
                }
                if (personQualificationJson.getPersonId() == null || personQualificationJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personQualificationData,
                            "no personId");
                }

                PersonQualification personQualification = dataManager.load(PersonQualification.class)
                        .query("select e from tsadv$PersonQualification e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personQualificationJson.getLegacyId(),
                                "pLegacyId", personQualificationJson.getPersonId(),
                                "companyCode", personQualificationJson.getCompanyCode()))
                        .view("personQualification-view")
                        .list().stream().findFirst().orElse(null);

                if (personQualification == null) {
                    return prepareError(result, methodName, personQualificationData,
                            "no personQualification with legacyId and personId : "
                                    + personQualificationJson.getLegacyId() + " , " + personQualificationJson.getPersonId() +
                                    ", " + personQualificationJson.getCompanyCode());
                }
                if (!personQualificationArrayList.stream().filter(personQualification1 ->
                        personQualification1.getId().equals(personQualification.getId())).findAny().isPresent()) {
                    personQualificationArrayList.add(personQualification);
                }
            }
            for (PersonQualification personQualification : personQualificationArrayList) {
                entityManager.remove(personQualification);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personQualificationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personQualificationData);
    }

    @Override
    public BaseResult createOrUpdatePersonContact(PersonContactDataJson personContactData) {
        String methodName = "createOrUpdatePersonContact";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<PersonContactJson> personContacts = new ArrayList<>();
        if (personContactData.getPersonContacts() != null) {
            personContacts = personContactData.getPersonContacts();
        }
        try {
            ArrayList<PersonContact> personContactsCommitList = new ArrayList<>();
            for (PersonContactJson personContactJson : personContacts) {

                if (personContactJson.getPersonId() == null || personContactJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no personId");
                }

                if (personContactJson.getLegacyId() == null || personContactJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no legacyId");
                }

                if (personContactJson.getType() == null || personContactJson.getType().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no type");
                }

                if (personContactJson.getCompanyCode() == null || personContactJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no companyCode");
                }

                if (personContactJson.getValue() == null || personContactJson.getValue().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no value");
                }

                PersonContact personContact = personContactsCommitList.stream().filter(filterPersonContact ->
                        filterPersonContact.getLegacyId() != null
                                && filterPersonContact.getType() != null
                                && filterPersonContact.getType().getLegacyId() != null
                                && filterPersonContact.getContactValue() != null
                                && filterPersonContact.getContactValue().equals(personContactJson.getValue())
                                && filterPersonContact.getPersonGroup() != null
                                && filterPersonContact.getPersonGroup().getLegacyId() != null
                                && filterPersonContact.getPersonGroup().getCompany() != null
                                && filterPersonContact.getPersonGroup().getCompany().getLegacyId() != null
                                && filterPersonContact.getLegacyId().equals(personContactJson.getLegacyId())
                                && filterPersonContact.getType().getLegacyId().equals(personContactJson.getType())
                                && filterPersonContact.getPersonGroup().getLegacyId().equals(personContactJson.getPersonId())
                                && filterPersonContact.getPersonGroup().getCompany().getLegacyId().equals(personContactJson.getCompanyCode())
                ).findFirst().orElse(null);


                if (personContact == null) {
                    personContact = dataManager.load(PersonContact.class)
                            .query(
                                    " select e from tsadv$PersonContact e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.contactValue = :value" +
                                            " and e.personGroup.legacyId = :pgLegacyId " +
                                            " and e.personGroup.company.legacyId = :companyCode " +
                                            " and e.type.legacyId = :tpLegacyId")
                            .setParameters(ParamsMap.of(
                                    "value", personContactJson.getValue(),
                                    "legacyId", personContactJson.getLegacyId(),
                                    "pgLegacyId", personContactJson.getPersonId(),
                                    "companyCode", personContactJson.getCompanyCode(),
                                    "tpLegacyId", personContactJson.getType()))
                            .view("personContact.edit").list().stream().findFirst().orElse(null);

                    if (personContact != null) {
                        personContact.setLegacyId(personContactJson.getLegacyId());
                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query(
                                        " select e from base$PersonGroupExt e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :company ")
                                .setParameters(ParamsMap.of(
                                        "legacyId", personContactJson.getPersonId(),
                                        "company", personContactJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                        if (personGroupExt != null) {
                            personContact.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personContactData,
                                    "no base$PersonGroupExt with legacyId " + personContactJson.getPersonId()
                                            + " and company legacyId " + personContactJson.getCompanyCode());
                        }

                        DicPhoneType type = dataManager.load(DicPhoneType.class)
                                .query(
                                        "select e from tsadv$DicPhoneType e " +
                                                " where e.legacyId = :legacyId ")
                                .parameter("legacyId", personContactJson.getType())
                                .view(View.BASE).list().stream().findFirst().orElse(null);
                        if (type != null) {
                            personContact.setType(type);
                        } else {
                            return prepareError(result, methodName, personContactData, "" +
                                    "no tsadv$DicPhoneType with legacyId " + personContactJson.getType());
                        }
                        if (personContactJson.getStartDate() != null && !personContactJson.getStartDate().isEmpty()) {
                            personContact.setStartDate(formatter.parse(personContactJson.getStartDate()));
                        }
                        if (personContactJson.getEndDate() != null && !personContactJson.getEndDate().isEmpty()) {
                            personContact.setEndDate(formatter.parse(personContactJson.getEndDate()));
                        }
                        personContact.setContactValue(personContactJson.getValue());
                        personContactsCommitList.add(personContact);
                    } else {
                        personContact = metadata.create(PersonContact.class);
                        personContact.setId(UUID.randomUUID());
                        personContact.setLegacyId(personContactJson.getLegacyId());
                        if (personContactJson.getStartDate() != null && !personContactJson.getStartDate().isEmpty()) {
                            personContact.setStartDate(formatter.parse(personContactJson.getStartDate()));
                        }
                        if (personContactJson.getEndDate() != null && !personContactJson.getEndDate().isEmpty()) {
                            personContact.setEndDate(formatter.parse(personContactJson.getEndDate()));
                        }
                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query(
                                        " select e from base$PersonGroupExt e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :company ")
                                .setParameters(ParamsMap.of(
                                        "legacyId", personContactJson.getPersonId(),
                                        "company", personContactJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                        if (personGroupExt != null) {
                            personContact.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personContactData,
                                    "no base$PersonGroupExt with legacyId " + personContactJson.getPersonId()
                                            + " and company legacyId " + personContactJson.getCompanyCode());
                        }

                        DicPhoneType type = dataManager.load(DicPhoneType.class)
                                .query(
                                        "select e from tsadv$DicPhoneType e " +
                                                " where e.legacyId = :legacyId ")
                                .parameter("legacyId", personContactJson.getType())
                                .view(View.BASE).list().stream().findFirst().orElse(null);
                        if (type != null) {
                            personContact.setType(type);
                        } else {
                            return prepareError(result, methodName, personContactData, "" +
                                    "no tsadv$DicPhoneType with legacyId " + personContactJson.getType());
                        }
                        personContact.setContactValue(personContactJson.getValue());
                        personContactsCommitList.add(personContact);
                    }
                } else {
                    personContact.setLegacyId(personContactJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query(
                                    " select e from base$PersonGroupExt e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.company.legacyId = :company ")
                            .setParameters(ParamsMap.of(
                                    "legacyId", personContactJson.getPersonId(),
                                    "company", personContactJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personContact.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personContactData,
                                "no base$PersonGroupExt with legacyId " + personContactJson.getPersonId()
                                        + " and company legacyId " + personContactJson.getCompanyCode());
                    }

                    DicPhoneType type = dataManager.load(DicPhoneType.class)
                            .query(
                                    "select e from tsadv$DicPhoneType e " +
                                            " where e.legacyId = :legacyId ")
                            .parameter("legacyId", personContactJson.getType())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (type != null) {
                        personContact.setType(type);
                    } else {
                        return prepareError(result, methodName, personContactData, "" +
                                "no tsadv$DicPhoneType with legacyId " + personContactJson.getType());
                    }
                    personContact.setContactValue(personContactJson.getValue());
                }
            }

            for (PersonContact personContact : personContactsCommitList) {
                commitContext.addInstanceToCommit(personContact);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, personContactData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personContactData);
    }

    @Override
    public BaseResult deletePersonContact(PersonContactDataJson personContactData) {
        String methodName = "deletePersonContact";
        BaseResult result = new BaseResult();
        ArrayList<PersonContactJson> personContacts = new ArrayList<>();
        if (personContactData.getPersonContacts() != null) {
            personContacts = personContactData.getPersonContacts();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonContact> personContactArrayList = new ArrayList<>();
            for (PersonContactJson personContactJson : personContacts) {

                if (personContactJson.getLegacyId() == null || personContactJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no legacyId");
                }

                if (personContactJson.getCompanyCode() == null || personContactJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personContactData,
                            "no companyCode");
                }

                PersonContact personContact = dataManager.load(PersonContact.class)
                        .query(
                                " select e from tsadv$PersonContact e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.personGroup.company.legacyId = :companyCode ")
                        .setParameters(ParamsMap.of(
                                "legacyId", personContactJson.getLegacyId(),
                                "companyCode", personContactJson.getCompanyCode()))
                        .view("personContact.edit").list().stream().findFirst().orElse(null);

                if (personContact == null) {
                    return prepareError(result, methodName, personContactData,
                            "no tsadv$PersonContact with legacyId " + personContactJson.getLegacyId()
                                    + " and company legacyId " + personContactJson.getCompanyCode());
                }

                if (!personContactArrayList.stream().filter(personContact1 ->
                        personContact1.getId().equals(personContact.getId())).findAny().isPresent()) {
                    personContactArrayList.add(personContact);
                }
            }

            for (PersonContact personContact1 : personContactArrayList) {
                entityManager.remove(personContact1);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personContactData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, personContactData);
    }

    @Override
    public BaseResult createOrUpdatePersonEducation(PersonEducationDataJson personEducationData) {
        String methodName = "createOrUpdatePersonEducation";
        ArrayList<PersonEducationJson> personEducations = new ArrayList<>();
        if (personEducationData.getPersonEducations() != null) {
            personEducations = personEducationData.getPersonEducations();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (PersonEducationJson personEducationJson : personEducations) {
                if (personEducationJson.getLegacyId() == null || personEducationJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no legacyId");
                }
                if (personEducationJson.getCompanyCode() == null || personEducationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no companyCode");
                }
                if (personEducationJson.getSchool() == null || personEducationJson.getSchool().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no school");
                }
                if (personEducationJson.getEducationTypeId() == null || personEducationJson.getEducationTypeId().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no educationTypeId");
                }
                if (personEducationJson.getFaculty() == null || personEducationJson.getFaculty().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no faculty");
                }
                if (personEducationJson.getStartYear() == null || personEducationJson.getStartYear().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no startYear");
                }
                if (personEducationJson.getEndYear() == null || personEducationJson.getEndYear().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no endYear");
                }
                if (personEducationJson.getQualification() == null || personEducationJson.getQualification().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no qualification");
                }
                PersonEducation personEducation = dataManager.load(PersonEducation.class)
                        .query("select e from tsadv$PersonEducation e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personEducationJson.getLegacyId(),
                                "pLegacyId", personEducationJson.getPersonId(),
                                "companyCode", personEducationJson.getCompanyCode()))
                        .view("personEducation.full")
                        .list().stream().findFirst().orElse(null);
                if (personEducation != null) {
                    personEducation.setLegacyId(personEducationJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personEducationJson.getPersonId(),
                                    "company", personEducationJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personEducation.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personEducationData,
                                "no base$PersonGroupExt with legacyId " + personEducationJson.getPersonId()
                                        + " and company legacyId " + personEducationJson.getCompanyCode());
                    }
                    personEducation.setSchool(personEducationJson.getSchool());
                    personEducation.setStartYear(Integer.valueOf(personEducationJson.getStartYear()));
                    personEducation.setEndYear(Integer.valueOf(personEducationJson.getEndYear()));
                    personEducation.setFaculty(personEducationJson.getFaculty());
                    personEducation.setQualification(personEducationJson.getQualification());
                    DicEducationType educationType = dataManager.load(DicEducationType.class)
                            .query("select e from base$DicEducationType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personEducationJson.getEducationTypeId(),
                                    "companyCode", personEducationJson.getCompanyCode()))
                            .view("dicEducationType-browse")
                            .list().stream().findFirst().orElse(null);
                    if (educationType != null) {
                        personEducation.setEducationType(educationType);
                    } else {
                        return prepareError(result, methodName, personEducationData,
                                "no base$DicEducationType with legacyId " + personEducationJson.getEducationTypeId()
                                        + " and company legacyId " + personEducationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personEducation);
                }
                if (personEducation == null) {
                    personEducation = metadata.create(PersonEducation.class);
                    personEducation.setUuid(UUID.randomUUID());
                    personEducation.setLegacyId(personEducationJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personEducationJson.getPersonId(),
                                    "company", personEducationJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personEducation.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personEducationData,
                                "no base$PersonGroupExt with legacyId " + personEducationJson.getPersonId()
                                        + " and company legacyId " + personEducationJson.getCompanyCode());
                    }
                    personEducation.setSchool(personEducationJson.getSchool());
                    personEducation.setStartYear(Integer.valueOf(personEducationJson.getStartYear()));
                    personEducation.setEndYear(Integer.valueOf(personEducationJson.getEndYear()));
                    personEducation.setFaculty(personEducationJson.getFaculty());
                    personEducation.setQualification(personEducationJson.getQualification());
                    DicEducationType educationType = dataManager.load(DicEducationType.class)
                            .query("select e from base$DicEducationType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personEducationJson.getEducationTypeId(),
                                    "companyCode", personEducationJson.getCompanyCode()))
                            .view("dicEducationType-browse")
                            .list().stream().findFirst().orElse(null);
                    if (educationType != null) {
                        personEducation.setEducationType(educationType);
                    } else {
                        return prepareError(result, methodName, personEducationData,
                                "no base$DicEducationType with legacyId " + personEducationJson.getEducationTypeId()
                                        + " and company legacyId " + personEducationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personEducation);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personEducationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personEducationData);
    }

    @Override
    public BaseResult deletePersonEducation(PersonEducationDataJson personEducationData) {
        String methodName = "deletePersonEducation";
        BaseResult result = new BaseResult();
        ArrayList<PersonEducationJson> personEducations = new ArrayList<>();
        if (personEducationData.getPersonEducations() != null) {
            personEducations = personEducationData.getPersonEducations();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonEducation> personEducationArrayList = new ArrayList<>();
            for (PersonEducationJson personEducationJson : personEducations) {
                if (personEducationJson.getLegacyId() == null || personEducationJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no legacyId");
                }
                if (personEducationJson.getCompanyCode() == null || personEducationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no companyCode");
                }
                if (personEducationJson.getPersonId() == null || personEducationJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personEducationData,
                            "no personId");
                }

                PersonEducation personEducation = dataManager.load(PersonEducation.class)
                        .query("select e from tsadv$PersonEducation e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personEducationJson.getLegacyId(),
                                "pLegacyId", personEducationJson.getPersonId(),
                                "companyCode", personEducationJson.getCompanyCode()))
                        .view("personEducation.full")
                        .list().stream().findFirst().orElse(null);

                if (personEducation == null) {
                    return prepareError(result, methodName, personEducationData,
                            "no personEducation with legacyId and personId : "
                                    + personEducationJson.getLegacyId() + " , " + personEducationJson.getPersonId() +
                                    ", " + personEducationJson.getCompanyCode());
                }
                if (!personEducationArrayList.stream().filter(personEducation1 ->
                        personEducation1.getId().equals(personEducation.getId())).findAny().isPresent()) {
                    personEducationArrayList.add(personEducation);
                }
            }
            for (PersonEducation personEducation : personEducationArrayList) {
                entityManager.remove(personEducation);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personEducationData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personEducationData);
    }

    @Override
    public BaseResult createOrUpdateAbsence(AbsenceDataJson absenceData) {
        String methodName = "createOrUpdateAbsence";
        ArrayList<AbsenceJson> absences = new ArrayList<>();
        if (absenceData.getAbsences() != null) {
            absences = absenceData.getAbsences();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (AbsenceJson absenceJson : absences) {
                if (absenceJson.getLegacyId() == null || absenceJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no legacyId");
                }
                if (absenceJson.getCompanyCode() == null || absenceJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no companyCode");
                }
                if (absenceJson.getAbsenceTypeId() == null || absenceJson.getAbsenceTypeId().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no absenceTypeId");
                }
                if (absenceJson.getStartDate() == null || absenceJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no startDate");
                }
                if (absenceJson.getEndDate() == null || absenceJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no endDate");
                }
                if (absenceJson.getAbsenceDuration() == null || absenceJson.getAbsenceDuration().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no absenceDuration");
                }
//                if (absenceJson.getOrderNumber() == null || absenceJson.getOrderNumber().isEmpty()) {
//                    return prepareError(result, methodName, absenceData,
//                            "no orderNumber");
//                }
//                if (absenceJson.getOrderDate() == null || absenceJson.getOrderDate().isEmpty()) {
//                    return prepareError(result, methodName, absenceData,
//                            "no orderDate");
//                }
                Absence absence = dataManager.load(Absence.class)
                        .query("select e from tsadv$Absence e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", absenceJson.getLegacyId(),
                                "pLegacyId", absenceJson.getPersonId(),
                                "companyCode", absenceJson.getCompanyCode()))
                        .view("absence.for.integration")
                        .list().stream().findFirst().orElse(null);
                if (absence != null) {
                    absence.setLegacyId(absenceJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", absenceJson.getPersonId(),
                                    "company", absenceJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        absence.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, absenceData,
                                "no base$PersonGroupExt with legacyId " + absenceJson.getPersonId()
                                        + " and company legacyId " + absenceJson.getCompanyCode());
                    }
                    absence.setDateFrom(formatter.parse(absenceJson.getStartDate()));
                    absence.setDateTo(formatter.parse(absenceJson.getEndDate()));
                    absence.setAbsenceDays(Integer.valueOf(absenceJson.getAbsenceDuration()));
                    absence.setOrderNum(absenceJson.getOrderNumber());
                    absence.setOrderDate(absenceJson.getOrderDate() != null && !absenceJson.getOrderDate().isEmpty()
                            ? formatter.parse(absenceJson.getOrderDate())
                            : null);
                    DicAbsenceType absenceType = dataManager.load(DicAbsenceType.class)
                            .query("select e from tsadv$DicAbsenceType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", absenceJson.getAbsenceTypeId(),
                                    "companyCode", absenceJson.getCompanyCode()))
                            .view("dicAbsenceType.view")
                            .list().stream().findFirst().orElse(null);
                    if (absenceType != null) {
                        absence.setType(absenceType);
                    } else {
                        return prepareError(result, methodName, absenceData,
                                "no tsadv$DicAbsenceType with legacyId " + absenceJson.getAbsenceTypeId()
                                        + " and company legacyId " + absenceJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(absence);
                }
                if (absence == null) {
                    absence = metadata.create(Absence.class);
                    absence.setUuid(UUID.randomUUID());
                    absence.setLegacyId(absenceJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", absenceJson.getPersonId(),
                                    "company", absenceJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        absence.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, absenceData,
                                "no base$PersonGroupExt with legacyId " + absenceJson.getPersonId()
                                        + " and company legacyId " + absenceJson.getCompanyCode());
                    }
                    absence.setDateFrom(formatter.parse(absenceJson.getStartDate()));
                    absence.setDateTo(formatter.parse(absenceJson.getEndDate()));
                    absence.setAbsenceDays(Integer.valueOf(absenceJson.getAbsenceDuration()));
                    absence.setOrderNum(absenceJson.getOrderNumber());
                    absence.setOrderDate(absenceJson.getOrderDate() != null && !absenceJson.getOrderDate().isEmpty()
                            ? formatter.parse(absenceJson.getOrderDate())
                            : null);
                    DicAbsenceType absenceType = dataManager.load(DicAbsenceType.class)
                            .query("select e from tsadv$DicAbsenceType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", absenceJson.getAbsenceTypeId(),
                                    "companyCode", absenceJson.getCompanyCode()))
                            .view("dicAbsenceType.view")
                            .list().stream().findFirst().orElse(null);
                    if (absenceType != null) {
                        absence.setType(absenceType);
                    } else {
                        return prepareError(result, methodName, absenceData,
                                "no tsadv$DicAbsenceType with legacyId " + absenceJson.getAbsenceTypeId()
                                        + " and company legacyId " + absenceJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(absence);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, absenceData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, absenceData);
    }

    @Override
    public BaseResult deleteAbsence(AbsenceDataJson absenceData) {
        String methodName = "deleteAbsence";
        BaseResult result = new BaseResult();
        ArrayList<AbsenceJson> absences = new ArrayList<>();
        if (absenceData.getAbsences() != null) {
            absences = absenceData.getAbsences();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<Absence> absenceArrayList = new ArrayList<>();
            for (AbsenceJson absenceJson : absences) {
                if (absenceJson.getLegacyId() == null || absenceJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no legacyId");
                }
                if (absenceJson.getCompanyCode() == null || absenceJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no companyCode");
                }
                if (absenceJson.getPersonId() == null || absenceJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, absenceData,
                            "no personId");
                }

                Absence absence = dataManager.load(Absence.class)
                        .query("select e from tsadv$Absence e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", absenceJson.getLegacyId(),
                                "pLegacyId", absenceJson.getPersonId(),
                                "companyCode", absenceJson.getCompanyCode()))
                        .view("absence.for.integration")
                        .list().stream().findFirst().orElse(null);

                if (absence == null) {
                    return prepareError(result, methodName, absenceData,
                            "no absence with legacyId and personId : "
                                    + absenceJson.getLegacyId() + " , " + absenceJson.getPersonId() +
                                    ", " + absenceJson.getCompanyCode());
                }
                if (!absenceArrayList.stream().filter(absence1 ->
                        absence1.getId().equals(absence.getId())).findAny().isPresent()) {
                    absenceArrayList.add(absence);
                }
            }
            for (Absence absence : absenceArrayList) {
                entityManager.remove(absence);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, absenceData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, absenceData);
    }

    @Override
    public BaseResult createOrUpdatePersonExperience(PersonExperienceDataJson personExperienceData) {
        String methodName = "createOrUpdatePersonExperience";
        ArrayList<PersonExperienceJson> personExperiences = new ArrayList<>();
        if (personExperienceData.getPersonExperiences() != null) {
            personExperiences = personExperienceData.getPersonExperiences();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (PersonExperienceJson personExperienceJson : personExperiences) {
                if (personExperienceJson.getLegacyId() == null || personExperienceJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no legacyId");
                }
                if (personExperienceJson.getCompanyCode() == null || personExperienceJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no companyCode");
                }
                if (personExperienceJson.getPersonId() == null || personExperienceJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no personId");
                }
                if (personExperienceJson.getStartDate() == null || personExperienceJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no startDate");
                }
                if (personExperienceJson.getEndDate() == null || personExperienceJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no endDate");
                }
                if (personExperienceJson.getCompany() == null || personExperienceJson.getCompany().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no company");
                }
                if (personExperienceJson.getJob() == null || personExperienceJson.getJob().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no job");
                }
                if (personExperienceJson.getLocation() == null || personExperienceJson.getLocation().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no location");
                }
                PersonExperience personExperience = dataManager.load(PersonExperience.class)
                        .query("select e from tsadv$PersonExperience e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personExperienceJson.getLegacyId(),
                                "pLegacyId", personExperienceJson.getPersonId(),
                                "companyCode", personExperienceJson.getCompanyCode()))
                        .view("personExperience.full")
                        .list().stream().findFirst().orElse(null);
                if (personExperience != null) {
                    personExperience.setLegacyId(personExperienceJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personExperienceJson.getPersonId(),
                                    "company", personExperienceJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personExperience.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personExperienceData,
                                "no base$PersonGroupExt with legacyId " + personExperienceJson.getPersonId()
                                        + " and company legacyId " + personExperienceJson.getCompanyCode());
                    }
                    personExperience.setStartMonth(formatter.parse(personExperienceJson.getStartDate()));
                    personExperience.setEndMonth(formatter.parse(personExperienceJson.getEndDate()));
                    personExperience.setCompany(personExperienceJson.getCompany());
                    personExperience.setJob(personExperienceJson.getJob());
                    personExperience.setLocation(personExperienceJson.getLocation());

                    commitContext.addInstanceToCommit(personExperience);
                }
                if (personExperience == null) {
                    personExperience = metadata.create(PersonExperience.class);
                    personExperience.setUuid(UUID.randomUUID());
                    personExperience.setLegacyId(personExperienceJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personExperienceJson.getPersonId(),
                                    "company", personExperienceJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest")
                            .list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personExperience.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personExperienceData,
                                "no base$PersonGroupExt with legacyId " + personExperienceJson.getPersonId()
                                        + " and company legacyId " + personExperienceJson.getCompanyCode());
                    }
                    personExperience.setStartMonth(formatter.parse(personExperienceJson.getStartDate()));
                    personExperience.setEndMonth(formatter.parse(personExperienceJson.getEndDate()));
                    personExperience.setCompany(personExperienceJson.getCompany());
                    personExperience.setJob(personExperienceJson.getJob());
                    personExperience.setLocation(personExperienceJson.getLocation());

                    commitContext.addInstanceToCommit(personExperience);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personExperienceData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personExperienceData);
    }

    @Override
    public BaseResult deletePersonExperience(PersonExperienceDataJson personExperienceData) {
        String methodName = "deletePersonExperience";
        BaseResult result = new BaseResult();
        ArrayList<PersonExperienceJson> personExperiences = new ArrayList<>();
        if (personExperienceData.getPersonExperiences() != null) {
            personExperiences = personExperienceData.getPersonExperiences();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonExperience> personExperienceArrayList = new ArrayList<>();
            for (PersonExperienceJson personExperienceJson : personExperiences) {
                if (personExperienceJson.getLegacyId() == null || personExperienceJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no legacyId");
                }
                if (personExperienceJson.getCompanyCode() == null || personExperienceJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no companyCode");
                }
                if (personExperienceJson.getPersonId() == null || personExperienceJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personExperienceData,
                            "no personId");
                }

                PersonExperience personExperience = dataManager.load(PersonExperience.class)
                        .query("select e from tsadv$PersonExperience e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personExperienceJson.getLegacyId(),
                                "pLegacyId", personExperienceJson.getPersonId(),
                                "companyCode", personExperienceJson.getCompanyCode()))
                        .view("personExperience.full")
                        .list().stream().findFirst().orElse(null);

                if (personExperience == null) {
                    return prepareError(result, methodName, personExperienceData,
                            "no absence with legacyId and personId : "
                                    + personExperienceJson.getLegacyId() + " , " + personExperienceJson.getPersonId() +
                                    ", " + personExperienceJson.getCompanyCode());
                }
                if (!personExperienceArrayList.stream().filter(personExperience1 ->
                        personExperience1.getId().equals(personExperience.getId())).findAny().isPresent()) {
                    personExperienceArrayList.add(personExperience);
                }
            }
            for (PersonExperience personExperience : personExperienceArrayList) {
                entityManager.remove(personExperience);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personExperienceData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personExperienceData);
    }

    @Override
    public BaseResult createOrUpdateBeneficiary(BeneficiaryDataJson beneficiaryData) {
        String methodName = "createOrUpdateBeneficiary";
        ArrayList<BeneficiaryJson> beneficiaries = new ArrayList<>();
        if (beneficiaryData.getBeneficiaries() != null) {
            beneficiaries = beneficiaryData.getBeneficiaries();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        try {
            for (BeneficiaryJson beneficiaryJson : beneficiaries) {
                if (beneficiaryJson.getLegacyId() == null || beneficiaryJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no legacyId");
                }
                if (beneficiaryJson.getCompanyCode() == null || beneficiaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no companyCode");
                }
                if (beneficiaryJson.getPersonId() == null || beneficiaryJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no personId");
                }
                if (beneficiaryJson.getRelationshipTypeId() == null || beneficiaryJson.getRelationshipTypeId().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no relationshipTypeId");
                }
                if (beneficiaryJson.getLastName() == null || beneficiaryJson.getLastName().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no lastName");
                }
                if (beneficiaryJson.getFirstName() == null || beneficiaryJson.getFirstName().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no firstName");
                }
                if (beneficiaryJson.getMiddleName() == null || beneficiaryJson.getMiddleName().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no middleName");
                }
                if (beneficiaryJson.getLastNameLatin() == null || beneficiaryJson.getLastNameLatin().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no lastNameLatin");
                }
                if (beneficiaryJson.getFirstNameLatin() == null || beneficiaryJson.getFirstNameLatin().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no firstNameLatin");
                }
                if (beneficiaryJson.getDateOfBirth() == null || beneficiaryJson.getDateOfBirth().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no dateOfBirth");
                }
                if (beneficiaryJson.getWorkPlace() == null || beneficiaryJson.getWorkPlace().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no workPlace");
                }
                if (beneficiaryJson.getContactPhone() == null || beneficiaryJson.getContactPhone().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no contactPhone");
                }

                PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                        .query("select e from base$PersonGroupExt e " +
                                " where e.legacyId = :legacyId " +
                                " and e.company.legacyId = :company")
                        .setParameters(ParamsMap.of("legacyId", beneficiaryJson.getPersonId(),
                                "company", beneficiaryJson.getCompanyCode()))
                        .view("personGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);

                if (personGroupExt == null) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no personGroup with personId = " + beneficiaryJson.getPersonId()
                                    + " and companyCode = " + beneficiaryJson.getCompanyCode());
                }
                Beneficiary beneficiary = dataManager.load(Beneficiary.class)
                        .query("select e from tsadv$Beneficiary e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroupParent.legacyId = :pLegacyId " +
                                " and e.personGroupParent.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", beneficiaryJson.getLegacyId(),
                                "pLegacyId", beneficiaryJson.getPersonId(),
                                "companyCode", beneficiaryJson.getCompanyCode()))
                        .view("beneficiaryView")
                        .list().stream().findFirst().orElse(null);
                if (beneficiary != null && beneficiary.getPersonGroupChild() != null
                        && beneficiary.getPersonGroupChild().getPerson() != null) {
                    beneficiary.setLegacyId(beneficiaryJson.getLegacyId());
                    beneficiary.setPersonGroupParent(personGroupExt);

                    PersonExt personExt = beneficiary.getPersonGroupChild().getPerson();
                    personExt.setFirstName(beneficiaryJson.getFirstName());
                    personExt.setLastName(beneficiaryJson.getLastName());
                    personExt.setMiddleName(beneficiaryJson.getMiddleName());
                    personExt.setFirstNameLatin(beneficiaryJson.getFirstNameLatin());
                    personExt.setLastNameLatin(beneficiaryJson.getLastNameLatin());
                    personExt.setDateOfBirth(formatter.parse(beneficiaryJson.getDateOfBirth()));

                    DicRelationshipType relationshipType = dataManager.load(DicRelationshipType.class)
                            .query("select e from tsadv$DicRelationshipType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", beneficiaryJson.getRelationshipTypeId(),
                                    "companyCode", beneficiaryJson.getCompanyCode()))
                            .view("dicRelationshipType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (relationshipType != null) {
                        beneficiary.setRelationshipType(relationshipType);
                    } else {
                        return prepareError(result, methodName, beneficiaryData,
                                "no RelationshipType with relationshipTypeId = " + beneficiaryJson.getRelationshipTypeId()
                                        + " and companyCode = " + beneficiaryJson.getCompanyCode());
                    }
                    beneficiary.setAdditionalContact(beneficiaryJson.getContactPhone());
                    beneficiary.setWorkLocation(beneficiaryJson.getWorkPlace());

                    commitContext.addInstanceToCommit(personExt);
                    commitContext.addInstanceToCommit(beneficiary);
                } else {
                    beneficiary = metadata.create(Beneficiary.class);
                    beneficiary.setUuid(UUID.randomUUID());
                    beneficiary.setLegacyId(beneficiaryJson.getLegacyId());
                    beneficiary.setPersonGroupParent(personGroupExt);

                    PersonGroupExt personGroupChild = metadata.create(PersonGroupExt.class);

                    PersonExt newPersonExt = metadata.create(PersonExt.class);

                    DicPersonType personType = dataManager.load(DicPersonType.class)
                            .query("select e from tsadv$DicPersonType e" +
                                    " where e.code = 'BENEFICIARY'")
                            .list().stream().findFirst().orElse(null);

                    if (personType == null) {
                        return prepareError(result, methodName, beneficiaryData,
                                "no DicPersonType with code = 'BENEFICIARY'");
                    }

                    newPersonExt.setType(personType);
                    newPersonExt.setGroup(personGroupChild);
                    newPersonExt.setFirstName(beneficiaryJson.getFirstName());
                    newPersonExt.setLastName(beneficiaryJson.getLastName());
                    newPersonExt.setMiddleName(beneficiaryJson.getMiddleName());
                    newPersonExt.setFirstNameLatin(beneficiaryJson.getFirstNameLatin());
                    newPersonExt.setLastNameLatin(beneficiaryJson.getLastNameLatin());
                    newPersonExt.setDateOfBirth(formatter.parse(beneficiaryJson.getDateOfBirth()));

                    beneficiary.setPersonGroupChild(personGroupChild);
                    beneficiary.setWorkLocation(beneficiaryJson.getWorkPlace());
                    beneficiary.setAdditionalContact(beneficiaryJson.getContactPhone());

                    DicRelationshipType relationshipType = dataManager.load(DicRelationshipType.class)
                            .query("select e from tsadv$DicRelationshipType e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", beneficiaryJson.getRelationshipTypeId(),
                                    "companyCode", beneficiaryJson.getCompanyCode()))
                            .view("dicRelationshipType-edit")
                            .list().stream().findFirst().orElse(null);
                    if (relationshipType != null) {
                        beneficiary.setRelationshipType(relationshipType);
                    } else {
                        return prepareError(result, methodName, beneficiaryData,
                                "no RelationshipType with relationshipTypeId = " + beneficiaryJson.getRelationshipTypeId()
                                        + " and companyCode = " + beneficiaryJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personGroupChild);
                    commitContext.addInstanceToCommit(newPersonExt);
                    commitContext.addInstanceToCommit(beneficiary);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, beneficiaryData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, beneficiaryData);
    }

    @Override
    public BaseResult deleteBeneficiary(BeneficiaryDataJson beneficiaryData) {
        String methodName = "deleteBeneficiary";
        BaseResult result = new BaseResult();
        ArrayList<BeneficiaryJson> beneficiaries = new ArrayList<>();
        if (beneficiaryData.getBeneficiaries() != null) {
            beneficiaries = beneficiaryData.getBeneficiaries();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<Beneficiary> beneficiaryArrayList = new ArrayList<>();
            for (BeneficiaryJson beneficiaryJson : beneficiaries) {
                if (beneficiaryJson.getLegacyId() == null || beneficiaryJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no legacyId");
                }
                if (beneficiaryJson.getCompanyCode() == null || beneficiaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no companyCode");
                }
                if (beneficiaryJson.getPersonId() == null || beneficiaryJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no personId");
                }

                Beneficiary beneficiary = dataManager.load(Beneficiary.class)
                        .query("select e from tsadv$Beneficiary e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroupParent.legacyId = :pLegacyId " +
                                " and e.personGroupParent.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", beneficiaryJson.getLegacyId(),
                                "pLegacyId", beneficiaryJson.getPersonId(),
                                "companyCode", beneficiaryJson.getCompanyCode()))
                        .view("beneficiaryView")
                        .list().stream().findFirst().orElse(null);

                if (beneficiary == null) {
                    return prepareError(result, methodName, beneficiaryData,
                            "no beneficiary with legacyId and personId : "
                                    + beneficiaryJson.getLegacyId() + " , " + beneficiaryJson.getPersonId() +
                                    ", " + beneficiaryJson.getCompanyCode());
                }
                if (!beneficiaryArrayList.stream().filter(beneficiary1 ->
                        beneficiary1.getId().equals(beneficiary.getId())).findAny().isPresent()) {
                    beneficiaryArrayList.add(beneficiary);
                }
            }
            for (Beneficiary beneficiary : beneficiaryArrayList) {
                entityManager.remove(beneficiary);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, beneficiaryData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, beneficiaryData);
    }

    @Override
    public BaseResult createOrUpdatePersonDismissal(PersonDismissalDataJson personDismissalData) {
        String methodName = "createOrUpdatePersonDismissal";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<PersonDismissalJson> personDismissals = new ArrayList<>();
        if (personDismissalData.getPersonDismissals() != null) {
            personDismissals = personDismissalData.getPersonDismissals();
        }
        try {
            ArrayList<Dismissal> personDismissalsCommitList = new ArrayList<>();
            for (PersonDismissalJson personDismissalJson : personDismissals) {

                if (personDismissalJson.getPersonId() == null || personDismissalJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no personId");
                }

                if (personDismissalJson.getLegacyId() == null || personDismissalJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no legacyId");
                }

                if (personDismissalJson.getDismissalArticle() == null || personDismissalJson.getDismissalArticle().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no dismissalArticle");
                }

                if (personDismissalJson.getDismissalDate() == null || personDismissalJson.getDismissalDate().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no dismissalDate");
                }

                if (personDismissalJson.getCompanyCode() == null || personDismissalJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no companyCode");
                }

                Date personDismissalDate = formatter.parse(personDismissalJson.getDismissalDate());
                Dismissal personDismissal = personDismissalsCommitList.stream().filter(filterPersonDismissal ->
                        filterPersonDismissal.getLegacyId() != null
                                && filterPersonDismissal.getLcArticle() != null
                                && filterPersonDismissal.getLcArticle().getLegacyId() != null
                                && filterPersonDismissal.getLcArticle().getLegacyId().equals(personDismissalJson.getDismissalReasonCode())
                                && filterPersonDismissal.getDismissalDate() != null
                                && filterPersonDismissal.getDismissalDate().equals(personDismissalDate)
                                && filterPersonDismissal.getPersonGroup() != null
                                && filterPersonDismissal.getPersonGroup().getLegacyId() != null
                                && filterPersonDismissal.getPersonGroup().getLegacyId().equals(personDismissalJson.getPersonId())
                                && filterPersonDismissal.getPersonGroup().getCompany() != null
                                && filterPersonDismissal.getPersonGroup().getCompany().getLegacyId() != null
                                && filterPersonDismissal.getPersonGroup().getCompany().getLegacyId().equals(personDismissalJson.getCompanyCode())
                                && filterPersonDismissal.getLegacyId() != null
                                && filterPersonDismissal.getLegacyId().equals(personDismissalJson.getLegacyId())
                ).findFirst().orElse(null);
                if (personDismissal == null) {
                    personDismissal = dataManager.load(Dismissal.class)
                            .query(
                                    " select e from tsadv$Dismissal e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.personGroup.legacyId = :pgLegacyId " +
                                            " and e.personGroup.company.legacyId = :companyCode " +
                                            " and e.lcArticle.legacyId = :drLegacyId" +
                                            " and e.dismissalDate = :dsDate")
                            .setParameters(ParamsMap.of(
                                    "legacyId", personDismissalJson.getLegacyId(),
                                    "pgLegacyId", personDismissalJson.getPersonId(),
                                    "companyCode", personDismissalJson.getCompanyCode(),
                                    "drLegacyId", personDismissalJson.getDismissalArticle(),
                                    "dsDate", formatter.parse(personDismissalJson.getDismissalDate())))
                            .view("dismissal.edit").list().stream().findFirst().orElse(null);

                    if (personDismissal != null) {
                        personDismissal.setLegacyId(personDismissalJson.getLegacyId());
                        personDismissal.setDismissalDate(formatter.parse(personDismissalJson.getDismissalDate()));
                        personDismissal.setRequestDate(CommonUtils.getSystemDate());

                        DicDismissalStatus status = dataManager.load(DicDismissalStatus.class)
                                .query(
                                        "select e from tsadv$DicDismissalStatus e " +
                                                " where e.code = 'ACTIVE' ")
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (status != null) {
                            personDismissal.setStatus(status);
                        } else {
                            return prepareError(result, methodName, personDismissalData,
                                    "no tsadv$DicDismissalStatus with code = 'ACTIVE'");
                        }

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query(
                                        " select e from base$PersonGroupExt e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :company ")
                                .setParameters(ParamsMap.of(
                                        "legacyId", personDismissalJson.getPersonId(),
                                        "company", personDismissalJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                        if (personGroupExt != null) {
                            personDismissal.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personDismissalData,
                                    "no base$PersonGroupExt with legacyId " + personDismissalJson.getPersonId()
                                            + " and company legacyId " + personDismissalJson.getCompanyCode());
                        }

                        DicLCArticle article = dataManager.load(DicLCArticle.class)
                                .query(
                                        "select e from tsadv$DicLCArticle e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :companyCode")
                                .parameter("legacyId", personDismissalJson.getDismissalArticle())
                                .parameter("companyCode", personDismissalJson.getCompanyCode())
                                .view("dicLCArticle-edit")
                                .list().stream().findFirst().orElse(null);
                        if (article != null) {
                            personDismissal.setLcArticle(article);
                        } else {
                            return prepareError(result, methodName, personDismissalData, "" +
                                    "no tsadv$DicLCArticle with legacyId and companyCode " + personDismissalJson.getDismissalArticle()
                                    + " " + personDismissalJson.getCompanyCode());
                        }

                        DicDismissalReason reason = dataManager.load(DicDismissalReason.class)
                                .query("select e from tsadv$DicDismissalReason e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.company.legacyId = :companyCode")
                                .parameter("legacyId", personDismissalJson.getDismissalReasonCode())
                                .parameter("companyCode", personDismissalJson.getCompanyCode())
                                .view("dicDismissalReason-edit")
                                .list().stream().findFirst().orElse(null);
                        if (reason != null) {
                            personDismissal.setDismissalReason(reason);
                        }

                        personDismissalsCommitList.add(personDismissal);
                    } else {
                        personDismissal = metadata.create(Dismissal.class);
                        personDismissal.setId(UUID.randomUUID());
                        personDismissal.setLegacyId(personDismissalJson.getLegacyId());
                        personDismissal.setDismissalDate(formatter.parse(personDismissalJson.getDismissalDate()));
                        personDismissal.setRequestDate(CommonUtils.getSystemDate());

                        DicDismissalStatus status = dataManager.load(DicDismissalStatus.class)
                                .query(
                                        "select e from tsadv$DicDismissalStatus e " +
                                                " where e.code = 'ACTIVE' ")
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (status != null) {
                            personDismissal.setStatus(status);
                        } else {
                            return prepareError(result, methodName, personDismissalData,
                                    "no tsadv$DicDismissalStatus with code = 'ACTIVE'");
                        }

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query(
                                        " select e from base$PersonGroupExt e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :company ")
                                .setParameters(ParamsMap.of(
                                        "legacyId", personDismissalJson.getPersonId(),
                                        "company", personDismissalJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                        if (personGroupExt != null) {
                            personDismissal.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personDismissalData,
                                    "no base$PersonGroupExt with legacyId " + personDismissalJson.getPersonId()
                                            + " and company legacyId " + personDismissalJson.getCompanyCode());
                        }

                        DicLCArticle article = dataManager.load(DicLCArticle.class)
                                .query(
                                        "select e from tsadv$DicLCArticle e " +
                                                " where e.legacyId = :legacyId " +
                                                " and e.company.legacyId = :companyCode")
                                .parameter("legacyId", personDismissalJson.getDismissalArticle())
                                .parameter("companyCode", personDismissalJson.getCompanyCode())
                                .view("dicLCArticle-edit")
                                .list().stream().findFirst().orElse(null);
                        if (article != null) {
                            personDismissal.setLcArticle(article);
                        } else {
                            return prepareError(result, methodName, personDismissalData, "" +
                                    "no tsadv$DicLCArticle with legacyId and companyCode " + personDismissalJson.getDismissalArticle()
                                    + " " + personDismissalJson.getCompanyCode());
                        }

                        DicDismissalReason reason = dataManager.load(DicDismissalReason.class)
                                .query("select e from tsadv$DicDismissalReason e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.company.legacyId = :companyCode")
                                .parameter("legacyId", personDismissalJson.getDismissalReasonCode())
                                .parameter("companyCode", personDismissalJson.getCompanyCode())
                                .view("dicDismissalReason-edit")
                                .list().stream().findFirst().orElse(null);
                        if (reason != null) {
                            personDismissal.setDismissalReason(reason);
                        }

                        personDismissalsCommitList.add(personDismissal);
                    }
                } else {
                    personDismissal.setLegacyId(personDismissalJson.getLegacyId());
                    personDismissal.setRequestDate(CommonUtils.getSystemDate());

                    DicDismissalStatus status = dataManager.load(DicDismissalStatus.class)
                            .query(
                                    "select e from tsadv$DicDismissalStatus e " +
                                            " where e.code = 'ACTIVE' ")
                            .view(View.BASE).list().stream().findFirst().orElse(null);

                    if (status != null) {
                        personDismissal.setStatus(status);
                    } else {
                        return prepareError(result, methodName, personDismissalData,
                                "no tsadv$DicDismissalStatus with code = 'ACTIVE'");
                    }

                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query(
                                    " select e from base$PersonGroupExt e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.company.legacyId = :company ")
                            .setParameters(ParamsMap.of(
                                    "legacyId", personDismissalJson.getPersonId(),
                                    "company", personDismissalJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (personGroupExt != null) {
                        personDismissal.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personDismissalData,
                                "no base$PersonGroupExt with legacyId " + personDismissalJson.getPersonId()
                                        + " and company legacyId " + personDismissalJson.getCompanyCode());
                    }

                    DicLCArticle article = dataManager.load(DicLCArticle.class)
                            .query(
                                    "select e from tsadv$DicLCArticle e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.company.legacyId = :companyCode")
                            .parameter("legacyId", personDismissalJson.getDismissalArticle())
                            .parameter("companyCode", personDismissalJson.getCompanyCode())
                            .view("dicLCArticle-edit")
                            .list().stream().findFirst().orElse(null);
                    if (article != null) {
                        personDismissal.setLcArticle(article);
                    } else {
                        return prepareError(result, methodName, personDismissalData, "" +
                                "no tsadv$DicLCArticle with legacyId and companyCode " + personDismissalJson.getDismissalArticle()
                                + " " + personDismissalJson.getCompanyCode());
                    }

                    DicDismissalReason reason = dataManager.load(DicDismissalReason.class)
                            .query("select e from tsadv$DicDismissalReason e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .parameter("legacyId", personDismissalJson.getDismissalReasonCode())
                            .parameter("companyCode", personDismissalJson.getCompanyCode())
                            .view("dicDismissalReason-edit")
                            .list().stream().findFirst().orElse(null);
                    if (reason != null) {
                        personDismissal.setDismissalReason(reason);
                    }

                    personDismissalsCommitList.add(personDismissal);
                }
            }

            for (Dismissal personDismissal : personDismissalsCommitList) {
                commitContext.addInstanceToCommit(personDismissal);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, personDismissalData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personDismissalData);
    }

    @Override
    public BaseResult deletePersonDismissal(PersonDismissalDataJson personDismissalData) {
        String methodName = "deletePersonDismissal";
        BaseResult result = new BaseResult();
        ArrayList<PersonDismissalJson> personDismissals = new ArrayList<>();
        if (personDismissalData.getPersonDismissals() != null) {
            personDismissals = personDismissalData.getPersonDismissals();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<Dismissal> personDismissalsArrayList = new ArrayList<>();
            for (PersonDismissalJson personDismissalJson : personDismissals) {

                if (personDismissalJson.getLegacyId() == null || personDismissalJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no legacyId");
                }

                if (personDismissalJson.getCompanyCode() == null || personDismissalJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDismissalData,
                            "no companyCode");
                }

                Dismissal personDismissal = dataManager.load(Dismissal.class)
                        .query(
                                " select e from tsadv$Dismissal e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.personGroup.company.legacyId = :companyCode ")
                        .setParameters(ParamsMap.of(
                                "legacyId", personDismissalJson.getLegacyId(),
                                "companyCode", personDismissalJson.getCompanyCode()))
                        .view("dismissal.edit").list().stream().findFirst().orElse(null);

                if (personDismissal == null) {
                    return prepareError(result, methodName, personDismissalData,
                            "no tsadv$Dismissal with legacyId " + personDismissalJson.getLegacyId()
                                    + " and company legacyId " + personDismissalJson.getCompanyCode());
                }

                if (!personDismissalsArrayList.stream().filter(personDismissal1 ->
                        personDismissal1.getId().equals(personDismissal.getId())).findAny().isPresent()) {
                    personDismissalsArrayList.add(personDismissal);
                }
            }

            for (Dismissal personDismissal1 : personDismissalsArrayList) {
                entityManager.remove(personDismissal1);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personDismissalData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, personDismissalData);
    }

    @Override
    public BaseResult createOrUpdateHarmfulCondition(HarmfulConditionDataJson harmfulConditionData) {
        String methodName = "createOrUpdateHarmfulCondition";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<HarmfulConditionJson> harmfulConditions = new ArrayList<>();
        if (harmfulConditionData.getHarmfulConditions() != null) {
            harmfulConditions = harmfulConditionData.getHarmfulConditions();
        }
        try {
            ArrayList<PositionHarmfulCondition> harmfulConditionsCommitList = new ArrayList<>();
            for (HarmfulConditionJson harmfulConditionJson : harmfulConditions) {

                if (harmfulConditionJson.getLegacyId() == null || harmfulConditionJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no legacyId");
                }

                if (harmfulConditionJson.getPositionId() == null || harmfulConditionJson.getPositionId().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no positionId");
                }

                if (harmfulConditionJson.getStartDate() == null || harmfulConditionJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no startDate");
                }

                if (harmfulConditionJson.getEndDate() == null || harmfulConditionJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no endDate");
                }

                if (harmfulConditionJson.getDays() < 0) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "days is negative");
                }

                if (harmfulConditionJson.getCompanyCode() == null || harmfulConditionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no companyCode");
                }

                Date startDate = CommonUtils.truncDate(formatter.parse(harmfulConditionJson.getStartDate()));
                Date endDate = CommonUtils.truncDate(formatter.parse(harmfulConditionJson.getEndDate()));

                PositionHarmfulCondition harmfulCondition = harmfulConditionsCommitList.stream().filter(filterHarmfulCondition ->
                        filterHarmfulCondition.getLegacyId() != null
                                && filterHarmfulCondition.getLegacyId().equals(harmfulConditionJson.getLegacyId())
                                && filterHarmfulCondition.getPositionGroup() != null
                                && filterHarmfulCondition.getPositionGroup() != null
                                && filterHarmfulCondition.getPositionGroup().getLegacyId() != null
                                && filterHarmfulCondition.getPositionGroup().getLegacyId().equals(harmfulConditionJson.getPositionId())
                                && filterHarmfulCondition.getPositionGroup().getPosition().getOrganizationGroupExt().getCompany() != null
                                && filterHarmfulCondition.getPositionGroup().getPosition().getOrganizationGroupExt().getCompany().getLegacyId().equals(harmfulConditionJson.getCompanyCode())
                                && filterHarmfulCondition.getStartDate() != null
                                && filterHarmfulCondition.getStartDate().equals(startDate)
                                && filterHarmfulCondition.getEndDate() != null
                                && filterHarmfulCondition.getEndDate().equals(endDate)
                ).findFirst().orElse(null);
                if (harmfulCondition == null) {
                    harmfulCondition = dataManager.load(PositionHarmfulCondition.class)
                            .query(
                                    " select e from tsadv_PositionHarmfulCondition e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.positionGroup.legacyId = :pgLegacyId " +
                                            " and e.positionGroup.legacyId in" +
                                            " (select p.group.legacyId from base$PositionExt p " +
                                            "where p.organizationGroupExt.company.legacyId = :companyCode) ")
                            .setParameters(ParamsMap.of(
                                    "legacyId", harmfulConditionJson.getLegacyId(),
                                    "pgLegacyId", harmfulConditionJson.getPositionId(),
                                    "companyCode", harmfulConditionJson.getCompanyCode()))
                            .view("positionHarmfulCondition.edit").list().stream().findFirst().orElse(null);

                    if (harmfulCondition != null) {
                        harmfulCondition.setLegacyId(harmfulConditionJson.getLegacyId());
                        harmfulCondition.setStartDate(startDate);
                        harmfulCondition.setEndDate(endDate);
                        harmfulCondition.setDays(harmfulConditionJson.getDays());

                        PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                                .query(
                                        "select e from base$PositionGroupExt e " +
                                                " where e.legacyId = :id" +
                                                " and e.legacyId in " +
                                                "(select p.group.legacyId from base$PositionExt p" +
                                                " where p.organizationGroupExt.company.legacyId = :companyCode)")
                                .parameter("id", harmfulConditionJson.getPositionId())
                                .parameter("companyCode", harmfulConditionJson.getCompanyCode())
                                .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (positionGroupExt != null) {
                            harmfulCondition.setPositionGroup(positionGroupExt);
                        } else {
                            return prepareError(result, methodName, harmfulConditionData,
                                    "no base$PositionGroupExt with legacyId " + harmfulConditionJson.getPositionId()
                                            + " and company legacyId " + harmfulConditionJson.getCompanyCode());
                        }


                        harmfulConditionsCommitList.add(harmfulCondition);
                    } else {
                        harmfulCondition = metadata.create(PositionHarmfulCondition.class);
                        harmfulCondition.setId(UUID.randomUUID());
                        harmfulCondition.setLegacyId(harmfulConditionJson.getLegacyId());
                        harmfulCondition.setStartDate(startDate);
                        harmfulCondition.setEndDate(endDate);
                        harmfulCondition.setDays(harmfulConditionJson.getDays());

                        PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                                .query(
                                        "select e from base$PositionGroupExt e " +
                                                " where e.legacyId = :id" +
                                                " and e.legacyId in " +
                                                "(select p.group.legacyId from base$PositionExt p" +
                                                " where p.organizationGroupExt.company.legacyId = :companyCode)")
                                .parameter("id", harmfulConditionJson.getPositionId())
                                .parameter("companyCode", harmfulConditionJson.getCompanyCode())
                                .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (positionGroupExt != null) {
                            harmfulCondition.setPositionGroup(positionGroupExt);
                        } else {
                            return prepareError(result, methodName, harmfulConditionData,
                                    "no base$PositionGroupExt with legacyId " + harmfulConditionJson.getPositionId()
                                            + " and company legacyId " + harmfulConditionJson.getCompanyCode());
                        }

                        harmfulConditionsCommitList.add(harmfulCondition);
                    }
                } else {
                    harmfulCondition.setLegacyId(harmfulConditionJson.getLegacyId());
                    harmfulCondition.setStartDate(startDate);
                    harmfulCondition.setEndDate(endDate);
                    harmfulCondition.setDays(harmfulConditionJson.getDays());

                    PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                            .query(
                                    "select e from base$PositionGroupExt e " +
                                            " where e.legacyId = :id" +
                                            " and e.legacyId in " +
                                            "(select p.group.legacyId from base$PositionExt p" +
                                            " where p.organizationGroupExt.company.legacyId = :companyCode)")
                            .parameter("id", harmfulConditionJson.getPositionId())
                            .parameter("companyCode", harmfulConditionJson.getCompanyCode())
                            .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                    if (positionGroupExt != null) {
                        harmfulCondition.setPositionGroup(positionGroupExt);
                    } else {
                        return prepareError(result, methodName, harmfulConditionData,
                                "no base$PositionGroupExt with legacyId " + harmfulConditionJson.getPositionId()
                                        + " and company legacyId " + harmfulConditionJson.getCompanyCode());
                    }
                }
            }

            for (PositionHarmfulCondition positionHarmfulCondition : harmfulConditionsCommitList) {
                commitContext.addInstanceToCommit(positionHarmfulCondition);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, harmfulConditionData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, harmfulConditionData);
    }

    @Override
    public BaseResult deleteHarmfulCondition(HarmfulConditionDataJson harmfulConditionData) {
        String methodName = "deleteHarmfulCondition";
        BaseResult result = new BaseResult();
        ArrayList<HarmfulConditionJson> harmfulConditions = new ArrayList<>();
        if (harmfulConditionData.getHarmfulConditions() != null) {
            harmfulConditions = harmfulConditionData.getHarmfulConditions();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PositionHarmfulCondition> harmfulCondtionsArrayList = new ArrayList<>();
            for (HarmfulConditionJson harmfulConditionJson : harmfulConditions) {

                if (harmfulConditionJson.getLegacyId() == null || harmfulConditionJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no legacyId");
                }

                if (harmfulConditionJson.getCompanyCode() == null || harmfulConditionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no companyCode");
                }

                PositionHarmfulCondition harmfulCondition = dataManager.load(PositionHarmfulCondition.class)
                        .query(
                                " select e from tsadv_PositionHarmfulCondition e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.positionGroup.legacyId in" +
                                        " (select p.group.legacyId from base$PositionExt p " +
                                        "where p.organizationGroupExt.company.legacyId = :companyCode) ")
                        .setParameters(ParamsMap.of(
                                "legacyId", harmfulConditionJson.getLegacyId(),
                                "companyCode", harmfulConditionJson.getCompanyCode()))
                        .view("positionHarmfulCondition.edit").list().stream().findFirst().orElse(null);

                if (harmfulCondition == null) {
                    return prepareError(result, methodName, harmfulConditionData,
                            "no tsadv$PositionHarmfulCondition with legacyId " + harmfulConditionJson.getLegacyId()
                                    + " and company legacyId " + harmfulConditionJson.getCompanyCode());
                }

                if (!harmfulCondtionsArrayList.stream().filter(harmfulCondition1 ->
                        harmfulCondition1.getId().equals(harmfulCondition.getId())).findAny().isPresent()) {
                    harmfulCondtionsArrayList.add(harmfulCondition);
                }
            }

            for (PositionHarmfulCondition harmfulCondition : harmfulCondtionsArrayList) {
                entityManager.remove(harmfulCondition);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, harmfulConditionData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, harmfulConditionData);
    }

    @Override
    public BaseResult createOrUpdateAssignmentSchedule(AssignmentScheduleJsonData assignmentScheduleData) {
        String methodName = "createOrUpdateAssignmentSchedule";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<AssignmentScheduleJson> assignmentSchedules = new ArrayList<>();
        if (assignmentScheduleData.getAssignmentSchedules() != null) {
            assignmentSchedules = assignmentScheduleData.getAssignmentSchedules();
        }
        try {
            ArrayList<AssignmentSchedule> assignmentSchedulesCommitList = new ArrayList<>();
            for (AssignmentScheduleJson assignmentScheduleJson : assignmentSchedules) {

                if (assignmentScheduleJson.getAssignmentId() == null || assignmentScheduleJson.getAssignmentId().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no assignmentId");
                }

                if (assignmentScheduleJson.getScheduleId() == null || assignmentScheduleJson.getScheduleId().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no scheduleId");
                }

                if (assignmentScheduleJson.getStartDate() == null || assignmentScheduleJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no startDate");
                }

                if (assignmentScheduleJson.getEndDate() == null || assignmentScheduleJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no endDate");
                }

                if (assignmentScheduleJson.getEndPolicyCode() == null || assignmentScheduleJson.getEndPolicyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no endPolicyCode");
                }

                if (assignmentScheduleJson.getCompanyCode() == null || assignmentScheduleJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no companyCode");
                }


                //todo check this default values for non null constraint
                StandardOffset offset = dataManager.load(StandardOffset.class)
                        .query("select e from tsadv$StandardOffset e where e.offsetDisplay = :ofd")
                        .parameter("ofd", "STD5")
                        .view(View.BASE)
                        .list().stream().findFirst().orElse(null);

                MaterialDesignColorsEnum colorSet = MaterialDesignColorsEnum.AMBER;

                Date startDate = CommonUtils.truncDate(formatter.parse(assignmentScheduleJson.getStartDate()));
                Date endDate = CommonUtils.truncDate(formatter.parse(assignmentScheduleJson.getEndDate()));

                AssignmentSchedule assignmentSchedule = assignmentSchedulesCommitList.stream().filter(filterAssignmentSchedule ->
                        filterAssignmentSchedule.getAssignmentGroup() != null
                                && filterAssignmentSchedule.getAssignmentGroup().getLegacyId() != null
                                && filterAssignmentSchedule.getAssignmentGroup().getLegacyId().equals(assignmentScheduleJson.getAssignmentId())
                                && filterAssignmentSchedule.getSchedule() != null
                                && filterAssignmentSchedule.getSchedule().getLegacyId() != null
                                && filterAssignmentSchedule.getSchedule().getLegacyId().equals(assignmentScheduleJson.getScheduleId())
                                && filterAssignmentSchedule.getEndPolicyCode() != null
                                && filterAssignmentSchedule.getEndPolicyCode().equals(assignmentScheduleJson.getEndPolicyCode())
                                && filterAssignmentSchedule.getAssignmentGroup().getAssignment().getOrganizationGroup().getCompany() != null
                                && filterAssignmentSchedule.getAssignmentGroup().getAssignment().getOrganizationGroup().getCompany().getLegacyId().equals(assignmentScheduleJson.getCompanyCode())
                                && filterAssignmentSchedule.getStartDate() != null
                                && filterAssignmentSchedule.getStartDate().equals(startDate)
                                && filterAssignmentSchedule.getEndDate() != null
                                && filterAssignmentSchedule.getEndDate().equals(endDate)
                                && filterAssignmentSchedule.getEndPolicyCode() != null
                                && filterAssignmentSchedule.getEndPolicyCode().equals(assignmentScheduleJson.getEndPolicyCode())
                ).findFirst().orElse(null);
                if (assignmentSchedule == null) {
                    assignmentSchedule = dataManager.load(AssignmentSchedule.class)
                            .query(
                                    " select e from tsadv$AssignmentSchedule e " +
                                            " where e.startDate = :startDate" +
                                            " and e.assignmentGroup.legacyId = :agLegacyId " +
                                            " and e.assignmentGroup.legacyId in " +
                                            " (select p.group.legacyId from base$AssignmentExt p " +
                                            " where p.organizationGroup.company.legacyId = :companyCode) ")
                            .setParameters(
                                    ParamsMap.of(
                                            "startDate", CommonUtils.truncDate(formatter.parse(assignmentScheduleJson.getStartDate())),
                                            "agLegacyId", assignmentScheduleJson.getAssignmentId(),
                                            "companyCode", assignmentScheduleJson.getCompanyCode()
                                    )
                            )
                            .view("assignmentSchedule.edit").list().stream().findFirst().orElse(null);

                    if (assignmentSchedule != null) {
                        assignmentSchedule.setStartDate(startDate);
                        assignmentSchedule.setEndDate(endDate);
                        assignmentSchedule.setEndPolicyCode(assignmentScheduleJson.getEndPolicyCode());
                        assignmentSchedule.setOffset(offset);
                        assignmentSchedule.setColorsSet(colorSet);

                        AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                                .query("select e.group from base$AssignmentExt e " +
                                        " where e.group.legacyId = :legacyId " +
                                        " and e.group.legacyId in " +
                                        "(select p.group.legacyId from base$AssignmentExt p " +
                                        " where p.organizationGroup.company.legacyId = :companyCode)")
                                .setParameters(
                                        ParamsMap.of(
                                                "legacyId", assignmentScheduleJson.getAssignmentId(),
                                                "companyCode", assignmentScheduleJson.getCompanyCode())
                                )
                                .view("assignmentGroup.view")
                                .list().stream().findFirst().orElse(null);
                        if (assignmentGroupExt != null) {
                            assignmentSchedule.setAssignmentGroup(assignmentGroupExt);
                        } else {
                            return prepareError(result, methodName, assignmentScheduleData,
                                    "no base$AssignmentGroupExt with legacyId " + assignmentScheduleJson.getAssignmentId()
                                            + " and company legacyId " + assignmentScheduleJson.getCompanyCode());
                        }

                        StandardSchedule schedule = dataManager.load(StandardSchedule.class)
                                .query("select e from tsadv$StandardSchedule e " +
                                        " where e.legacyId = :shLegacyId " +
                                        " and e.company.legacyId = :companyCode")
                                .parameter("shLegacyId", assignmentScheduleJson.getScheduleId())
                                .parameter("companyCode", assignmentScheduleJson.getCompanyCode())
                                .view("schedule.view")
                                .list().stream().findFirst().orElse(null);

                        if (schedule != null) {
                            assignmentSchedule.setSchedule(schedule);
                        } else {
                            return prepareError(result, methodName, assignmentScheduleData,
                                    "no tsadv$StandardSchedule with legacyId " + assignmentScheduleJson.getScheduleId());
                        }

                        assignmentSchedulesCommitList.add(assignmentSchedule);
                    } else {
                        assignmentSchedule = metadata.create(AssignmentSchedule.class);
                        assignmentSchedule.setId(UUID.randomUUID());
                        assignmentSchedule.setStartDate(startDate);
                        assignmentSchedule.setEndDate(endDate);
                        assignmentSchedule.setEndPolicyCode(assignmentScheduleJson.getEndPolicyCode());
                        assignmentSchedule.setOffset(offset);
                        assignmentSchedule.setColorsSet(colorSet);


                        AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                                .query("select e.group from base$AssignmentExt e " +
                                        " where e.group.legacyId = :legacyId " +
                                        " and e.group.legacyId in " +
                                        "(select p.group.legacyId from base$AssignmentExt p " +
                                        " where p.organizationGroup.company.legacyId = :companyCode)")
                                .setParameters(
                                        ParamsMap.of(
                                                "legacyId", assignmentScheduleJson.getAssignmentId(),
                                                "companyCode", assignmentScheduleJson.getCompanyCode())
                                )
                                .view("assignmentGroup.view")
                                .list().stream().findFirst().orElse(null);
                        if (assignmentGroupExt != null) {
                            assignmentSchedule.setAssignmentGroup(assignmentGroupExt);
                        } else {
                            return prepareError(result, methodName, assignmentScheduleData,
                                    "no base$AssignmentGroupExt with legacyId " + assignmentScheduleJson.getAssignmentId()
                                            + " and company legacyId " + assignmentScheduleJson.getCompanyCode());
                        }

                        StandardSchedule schedule = dataManager.load(StandardSchedule.class)
                                .query("select e from tsadv$StandardSchedule e " +
                                        " where e.legacyId = :shLegacyId " +
                                        " and e.company.legacyId = :companyCode")
                                .parameter("shLegacyId", assignmentScheduleJson.getScheduleId())
                                .parameter("companyCode", assignmentScheduleJson.getCompanyCode())
                                .view("schedule.view")
                                .list().stream().findFirst().orElse(null);

                        if (schedule != null) {
                            assignmentSchedule.setSchedule(schedule);
                        } else {
                            return prepareError(result, methodName, assignmentScheduleData,
                                    "no tsadv$StandardSchedule with legacyId " + assignmentScheduleJson.getScheduleId()
                                            + " and companyCode = " + assignmentScheduleJson.getCompanyCode());
                        }

                        assignmentSchedulesCommitList.add(assignmentSchedule);
                    }
                } else {
                    assignmentSchedule.setStartDate(startDate);
                    assignmentSchedule.setEndDate(endDate);
                    assignmentSchedule.setEndPolicyCode(assignmentScheduleJson.getEndPolicyCode());
                    assignmentSchedule.setOffset(offset);
                    assignmentSchedule.setColorsSet(colorSet);

                    AssignmentGroupExt assignmentGroupExt = dataManager.load(AssignmentGroupExt.class)
                            .query("select e.group from base$AssignmentExt e " +
                                    " where e.group.legacyId = :legacyId " +
                                    " and e.group.legacyId in " +
                                    "(select p.group.legacyId from base$AssignmentExt p " +
                                    " where p.organizationGroup.company.legacyId = :companyCode)")
                            .setParameters(
                                    ParamsMap.of(
                                            "legacyId", assignmentScheduleJson.getAssignmentId(),
                                            "companyCode", assignmentScheduleJson.getCompanyCode())
                            )
                            .view("assignmentGroup.view")
                            .list().stream().findFirst().orElse(null);
                    if (assignmentGroupExt != null) {
                        assignmentSchedule.setAssignmentGroup(assignmentGroupExt);
                    } else {
                        return prepareError(result, methodName, assignmentScheduleData,
                                "no base$AssignmentGroupExt with legacyId " + assignmentScheduleJson.getAssignmentId()
                                        + " and company legacyId " + assignmentScheduleJson.getCompanyCode());
                    }

                    StandardSchedule schedule = dataManager.load(StandardSchedule.class)
                            .query("select e from tsadv$StandardSchedule e " +
                                    " where e.legacyId = :shLegacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .parameter("shLegacyId", assignmentScheduleJson.getScheduleId())
                            .parameter("companyCode", assignmentScheduleJson.getCompanyCode())
                            .view("schedule.view")
                            .list().stream().findFirst().orElse(null);

                    if (schedule != null) {
                        assignmentSchedule.setSchedule(schedule);
                    } else {
                        return prepareError(result, methodName, assignmentScheduleData,
                                "no tsadv$StandardSchedule with legacyId " + assignmentScheduleJson.getScheduleId()
                                        + " and companyCode = " + assignmentScheduleJson.getCompanyCode());
                    }
                }
            }

            for (AssignmentSchedule assignmentSchedule : assignmentSchedulesCommitList) {
                commitContext.addInstanceToCommit(assignmentSchedule);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, assignmentScheduleData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, assignmentScheduleData);
    }

    @Override
    public BaseResult deleteAssignmentSchedule(AssignmentScheduleJsonData assignmentScheduleData) {
        String methodName = "deleteAssignmentSchedule";
        BaseResult result = new BaseResult();
        ArrayList<AssignmentScheduleJson> assignmentSchedules = new ArrayList<>();
        if (assignmentScheduleData.getAssignmentSchedules() != null) {
            assignmentSchedules = assignmentScheduleData.getAssignmentSchedules();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<AssignmentSchedule> assignmentSchedulesArrayList = new ArrayList<>();
            for (AssignmentScheduleJson assignmentScheduleJson : assignmentSchedules) {

                if (assignmentScheduleJson.getAssignmentId() == null || assignmentScheduleJson.getAssignmentId().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no assignmentId");
                }

                if (assignmentScheduleJson.getStartDate() == null || assignmentScheduleJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no startDate");
                }
                if (assignmentScheduleJson.getCompanyCode() == null || assignmentScheduleJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no companyCode");
                }

                AssignmentSchedule assignmentSchedule = dataManager.load(AssignmentSchedule.class)
                        .query(
                                " select e from tsadv$AssignmentSchedule e " +
                                        " where e.startDate = :startDate" +
                                        " and e.assignmentGroup.legacyId = :agLegacyId " +
                                        " and e.assignmentGroup.legacyId in " +
                                        " (select p.group.legacyId from base$AssignmentExt p " +
                                        " where p.organizationGroup.company.legacyId = :companyCode) ")
                        .setParameters(
                                ParamsMap.of(
                                        "startDate", CommonUtils.truncDate(formatter.parse(assignmentScheduleJson.getStartDate())),
                                        "agLegacyId", assignmentScheduleJson.getAssignmentId(),
                                        "companyCode", assignmentScheduleJson.getCompanyCode()
                                )
                        )
                        .view("assignmentSchedule.edit").list().stream().findFirst().orElse(null);

                if (assignmentSchedule == null) {
                    return prepareError(result, methodName, assignmentScheduleData,
                            "no tsadv$AssignmentSchedule with assignmentId " + assignmentScheduleJson.getAssignmentId()
                                    + " and company legacyId " + assignmentScheduleJson.getCompanyCode());
                }

                if (!assignmentSchedulesArrayList.stream().filter(assignmentSchedule1 ->
                        assignmentSchedule1.getId().equals(assignmentSchedule.getId())).findAny().isPresent()) {
                    assignmentSchedulesArrayList.add(assignmentSchedule);
                }
            }

            for (AssignmentSchedule assignmentSchedule : assignmentSchedulesArrayList) {
                entityManager.remove(assignmentSchedule);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, assignmentScheduleData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, assignmentScheduleData);
    }

    @Override
    public BaseResult createOrUpdatePersonAddress(PersonAddressDataJson personAddressData) {
        String methodName = "createOrUpdatePersonAddress";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<PersonAddressJson> personAdresses = new ArrayList<>();
        if (personAddressData.getPersonAddresses() != null) {
            personAdresses = personAddressData.getPersonAddresses();
        }
        try {
            ArrayList<Address> personAddressesCommitList = new ArrayList<>();
            for (PersonAddressJson personAddressJson : personAdresses) {

                if (personAddressJson.getLegacyId() == null || personAddressJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no legacyId");
                }

                if (personAddressJson.getPersonId() == null || personAddressJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no personId");
                }

                if (personAddressJson.getFactAddress() == null || personAddressJson.getFactAddress().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no factAddress");
                }

                if (personAddressJson.getRegistrationAddress() == null || personAddressJson.getRegistrationAddress().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no registrationAddress");
                }

                if (personAddressJson.getFactAddressKATOCode() == null || personAddressJson.getFactAddressKATOCode().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no factAddressKATOCode");
                }

                if (personAddressJson.getRegistrationAddressKATOCode() == null || personAddressJson.getRegistrationAddressKATOCode().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no registrationAddressKATOCode");
                }

                if (personAddressJson.getCompanyCode() == null || personAddressJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no companyCode");
                }

                Date startDate = CommonUtils.getSystemDate();
                Date endDate = CommonUtils.getMaxDate();

                Address address = personAddressesCommitList.stream().filter(filterAddress ->
                        filterAddress.getLegacyId() != null
                                && filterAddress.getLegacyId().equals(personAddressJson.getLegacyId())
                                && filterAddress.getPersonGroup() != null
                                && filterAddress.getPersonGroup().getLegacyId() != null
                                && filterAddress.getPersonGroup().getLegacyId().equals(personAddressJson.getPersonId())
                                && filterAddress.getPersonGroup().getCompany() != null
                                && filterAddress.getPersonGroup().getCompany().getLegacyId().equals(personAddressJson.getCompanyCode())
                                && filterAddress.getFactAddress() != null
                                && filterAddress.getFactAddress().equals(personAddressJson.getFactAddress())
                                && filterAddress.getRegistrationAddress() != null
                                && filterAddress.getRegistrationAddress().equals(personAddressJson.getRegistrationAddress())
                                && filterAddress.getFactAddressKATOCode() != null
                                && filterAddress.getFactAddressKATOCode().equals(personAddressJson.getFactAddressKATOCode())
                                && filterAddress.getRegistrationAddressKATOCode() != null
                                && filterAddress.getRegistrationAddressKATOCode().equals(personAddressJson.getRegistrationAddressKATOCode())
                ).findFirst().orElse(null);
                if (address == null) {
                    address = dataManager.load(Address.class)
                            .query(
                                    " select e from tsadv$Address e " +
                                            " where e.legacyId = " + personAddressJson.getLegacyId() + " " +
                                            " and e.personGroup.legacyId = :pgLegacyId " +
                                            " and e.personGroup.company.legacyId = :companyCode " +
                                            " and e.factAddress = :fd " +
                                            " and e.registrationAddress = :rd" +
                                            " and e.factAddressKATOCode = :fdkc" +
                                            " and e.registrationAddressKATOCode = :rdkc ")
                            .setParameters(
                                    ParamsMap.of(
                                            "pgLegacyId", personAddressJson.getPersonId(),
                                            "companyCode", personAddressJson.getCompanyCode(),
                                            "fd", personAddressJson.getFactAddress(),
                                            "rd", personAddressJson.getRegistrationAddress(),
                                            "fdkc", personAddressJson.getFactAddressKATOCode(),
                                            "rdkc", personAddressJson.getRegistrationAddressKATOCode()
                                    )
                            )
                            .view("address.view").list().stream().findFirst().orElse(null);

                    if (address != null) {
                        address.setStartDate(startDate);
                        address.setEndDate(endDate);

                        address.setLegacyId(personAddressJson.getLegacyId());
                        address.setFactAddress(personAddressJson.getFactAddress());
                        address.setRegistrationAddress(personAddressJson.getRegistrationAddress());
                        address.setFactAddressKATOCode(personAddressJson.getFactAddressKATOCode());
                        address.setRegistrationAddressKATOCode(personAddressJson.getRegistrationAddressKATOCode());

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", personAddressJson.getPersonId(),
                                        "company", personAddressJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            address.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personAddressData,
                                    "no personGroup with legacyId and companyCode : "
                                            + personAddressJson.getPersonId() + " , " + personAddressJson.getCompanyCode());
                        }

                        personAddressesCommitList.add(address);
                    } else {
                        address = metadata.create(Address.class);
                        address.setId(UUID.randomUUID());
                        address.setLegacyId(personAddressJson.getLegacyId());
                        address.setFactAddress(personAddressJson.getFactAddress());
                        address.setRegistrationAddress(personAddressJson.getRegistrationAddress());
                        address.setFactAddressKATOCode(personAddressJson.getFactAddressKATOCode());
                        address.setRegistrationAddressKATOCode(personAddressJson.getRegistrationAddressKATOCode());

                        address.setStartDate(startDate);
                        address.setEndDate(endDate);

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", personAddressJson.getPersonId(),
                                        "company", personAddressJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            address.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personAddressData,
                                    "no personGroup with legacyId and companyCode : "
                                            + personAddressJson.getPersonId() + " , " + personAddressJson.getCompanyCode());
                        }

                        personAddressesCommitList.add(address);
                    }
                } else {

                    address.setStartDate(startDate);
                    address.setEndDate(endDate);

                    address.setLegacyId(personAddressJson.getLegacyId());
                    address.setFactAddress(personAddressJson.getFactAddress());
                    address.setRegistrationAddress(personAddressJson.getRegistrationAddress());
                    address.setFactAddressKATOCode(personAddressJson.getFactAddressKATOCode());
                    address.setRegistrationAddressKATOCode(personAddressJson.getRegistrationAddressKATOCode());

                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personAddressJson.getPersonId(),
                                    "company", personAddressJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                    if (personGroupExt != null) {
                        address.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personAddressData,
                                "no personGroup with legacyId and companyCode : "
                                        + personAddressJson.getPersonId() + " , " + personAddressJson.getCompanyCode());
                    }
                }
            }

            for (Address address : personAddressesCommitList) {
                commitContext.addInstanceToCommit(address);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, personAddressData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personAddressData);
    }

    @Override
    public BaseResult deletePersonAddress(PersonAddressDataJson personAddressData) {
        String methodName = "deletePersonAddress";
        BaseResult result = new BaseResult();
        ArrayList<PersonAddressJson> personAddresses = new ArrayList<>();
        if (personAddressData.getPersonAddresses() != null) {
            personAddresses = personAddressData.getPersonAddresses();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<Address> personAddressesArrayList = new ArrayList<>();
            for (PersonAddressJson personAddressJson : personAddresses) {

                if (personAddressJson.getLegacyId() == null || personAddressJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no legacyId");
                }

                if (personAddressJson.getCompanyCode() == null || personAddressJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personAddressData,
                            "no companyCode");
                }

                Address address = dataManager.load(Address.class)
                        .query(
                                " select e from tsadv$Address e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of(
                                "legacyId", personAddressJson.getLegacyId(),
                                "companyCode", personAddressJson.getCompanyCode()))
                        .view("address.view").list().stream().findFirst().orElse(null);

                if (address == null) {
                    return prepareError(result, methodName, personAddressData,
                            "no tsadv$Address with legacyId " + personAddressJson.getLegacyId()
                                    + " and company legacyId " + personAddressJson.getCompanyCode());
                }

                if (!personAddressesArrayList.stream().filter(personAddress1 ->
                        personAddress1.getId().equals(address.getId())).findAny().isPresent()) {
                    personAddressesArrayList.add(address);
                }
            }

            for (Address address : personAddressesArrayList) {
                entityManager.remove(address);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personAddressData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, personAddressData);
    }

    @Override
    public BaseResult createOrUpdatePersonLanguages(PersonLanguageDataJson personLanguageData) {
        String methodName = "createOrUpdatePersonLanguages";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<PersonLanguageJson> personLanguages = new ArrayList<>();
        if (personLanguageData.getPersonLanguages() != null) {
            personLanguages = personLanguageData.getPersonLanguages();
        }
        try {
            ArrayList<PersonLanguage> personLanguagesCommitList = new ArrayList<>();
            for (PersonLanguageJson personLanguageJson : personLanguages) {

                if (personLanguageJson.getLegacyId() == null || personLanguageJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no legacyId");
                }

                if (personLanguageJson.getPersonId() == null || personLanguageJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no personId");
                }

                if (personLanguageJson.getLanguageId() == null || personLanguageJson.getLanguageId().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no languageId");
                }

                if (personLanguageJson.getLevelId() == null || personLanguageJson.getLevelId().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no levelId");
                }

                if (personLanguageJson.getCompanyCode() == null || personLanguageJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no companyCode");
                }

                PersonLanguage personLanguage = personLanguagesCommitList.stream().filter(filterPersonLanguage ->
                        filterPersonLanguage.getLegacyId() != null
                                && filterPersonLanguage.getLegacyId().equals(personLanguageJson.getLegacyId())
                                && filterPersonLanguage.getPersonGroup() != null
                                && filterPersonLanguage.getPersonGroup().getLegacyId() != null
                                && filterPersonLanguage.getPersonGroup().getLegacyId().equals(personLanguageJson.getPersonId())
                                && filterPersonLanguage.getPersonGroup().getCompany() != null
                                && filterPersonLanguage.getPersonGroup().getCompany().getLegacyId().equals(personLanguageJson.getCompanyCode())
                                && filterPersonLanguage.getLanguage() != null
                                && filterPersonLanguage.getLanguage().getLegacyId().equals(personLanguageJson.getLanguageId())
                                && filterPersonLanguage.getLanguageLevel() != null
                                && filterPersonLanguage.getLanguageLevel().getLegacyId().equals(personLanguageJson.getLevelId())
                ).findFirst().orElse(null);
                if (personLanguage == null) {
                    personLanguage = dataManager.load(PersonLanguage.class)
                            .query(
                                    " select e from tsadv_PersonLanguage e " +
                                            " where e.legacyId = :legacyId" +
                                            " and e.personGroup.legacyId = :pgLegacyId " +
                                            " and e.personGroup.company.legacyId = :companyCode " +
                                            " and e.language.legacyId = :lgLegacyId " +
                                            " and e.languageLevel.legacyId = :lglvLegacyId")
                            .setParameters(
                                    ParamsMap.of(
                                            "legacyId", personLanguageJson.getLegacyId(),
                                            "pgLegacyId", personLanguageJson.getPersonId(),
                                            "companyCode", personLanguageJson.getCompanyCode(),
                                            "lgLegacyId", personLanguageJson.getLanguageId(),
                                            "lglvLegacyId", personLanguageJson.getLevelId()
                                    )
                            )
                            .view("personLanguage.edit").list().stream().findFirst().orElse(null);

                    if (personLanguage != null) {
                        personLanguage.setLegacyId(personLanguageJson.getLegacyId());

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", personLanguageJson.getPersonId(),
                                        "company", personLanguageJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            personLanguage.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no personGroup with legacyId and companyCode : "
                                            + personLanguageJson.getPersonId() + " , " + personLanguageJson.getCompanyCode());
                        }

                        DicLanguage language = dataManager.load(DicLanguage.class)
                                .query("select e from base$DicLanguage e " +
                                        " where e.legacyId = :lgLegacyId")
                                .parameter("lgLegacyId", personLanguageJson.getLanguageId())
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (language != null) {
                            personLanguage.setLanguage(language);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no base$DicLanguage with legacyId: " + personLanguageJson.getLanguageId());
                        }

                        DicLanguageLevel languageLevel = dataManager.load(DicLanguageLevel.class)
                                .query("select e from tsadv_DicLanguageLevel e " +
                                        " where e.legacyId = :lglvLegacyId")
                                .parameter("lglvLegacyId", personLanguageJson.getLevelId())
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (languageLevel != null) {
                            personLanguage.setLanguageLevel(languageLevel);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no tsadv_DicLanguageLevel with legacyId: " + personLanguageJson.getLevelId());
                        }

                        personLanguagesCommitList.add(personLanguage);
                    } else {
                        personLanguage = metadata.create(PersonLanguage.class);
                        personLanguage.setId(UUID.randomUUID());
                        personLanguage.setLegacyId(personLanguageJson.getLegacyId());

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", personLanguageJson.getPersonId(),
                                        "company", personLanguageJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            personLanguage.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no personGroup with legacyId and companyCode : "
                                            + personLanguageJson.getPersonId() + " , " + personLanguageJson.getCompanyCode());
                        }

                        DicLanguage language = dataManager.load(DicLanguage.class)
                                .query("select e from base$DicLanguage e " +
                                        " where e.legacyId = :lgLegacyId")
                                .parameter("lgLegacyId", personLanguageJson.getLanguageId())
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (language != null) {
                            personLanguage.setLanguage(language);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no base$DicLanguage with legacyId: " + personLanguageJson.getLanguageId());
                        }

                        DicLanguageLevel languageLevel = dataManager.load(DicLanguageLevel.class)
                                .query("select e from tsadv_DicLanguageLevel e " +
                                        " where e.legacyId = :lglvLegacyId")
                                .parameter("lglvLegacyId", personLanguageJson.getLevelId())
                                .view(View.BASE).list().stream().findFirst().orElse(null);

                        if (languageLevel != null) {
                            personLanguage.setLanguageLevel(languageLevel);
                        } else {
                            return prepareError(result, methodName, personLanguageData,
                                    "no tsadv_DicLanguageLevel with legacyId: " + personLanguageJson.getLevelId());
                        }

                        personLanguagesCommitList.add(personLanguage);
                    }
                } else {
                    personLanguage.setLegacyId(personLanguageJson.getLegacyId());

                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", personLanguageJson.getPersonId(),
                                    "company", personLanguageJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                    if (personGroupExt != null) {
                        personLanguage.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, personLanguageData,
                                "no personGroup with legacyId and companyCode : "
                                        + personLanguageJson.getPersonId() + " , " + personLanguageJson.getCompanyCode());
                    }

                    DicLanguage language = dataManager.load(DicLanguage.class)
                            .query("select e from base$DicLanguage e " +
                                    " where e.legacyId = :lgLegacyId")
                            .parameter("lgLegacyId", personLanguageJson.getLanguageId())
                            .view(View.BASE).list().stream().findFirst().orElse(null);

                    if (language != null) {
                        personLanguage.setLanguage(language);
                    } else {
                        return prepareError(result, methodName, personLanguageData,
                                "no base$DicLanguage with legacyId: " + personLanguageJson.getLanguageId());
                    }

                    DicLanguageLevel languageLevel = dataManager.load(DicLanguageLevel.class)
                            .query("select e from tsadv_DicLanguageLevel e " +
                                    " where e.legacyId = :lglvLegacyId")
                            .parameter("lglvLegacyId", personLanguageJson.getLevelId())
                            .view(View.BASE).list().stream().findFirst().orElse(null);

                    if (languageLevel != null) {
                        personLanguage.setLanguageLevel(languageLevel);
                    } else {
                        return prepareError(result, methodName, personLanguageData,
                                "no tsadv_DicLanguageLevel with legacyId: " + personLanguageJson.getLevelId());
                    }
                }
            }

            for (PersonLanguage personLanguage : personLanguagesCommitList) {
                commitContext.addInstanceToCommit(personLanguage);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, personLanguageData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personLanguageData);
    }

    @Override
    public BaseResult deletePersonLanguages(PersonLanguageDataJson personLanguageData) {
        String methodName = "deletePersonLanguages";
        BaseResult result = new BaseResult();
        ArrayList<PersonLanguageJson> personLanguages = new ArrayList<>();
        if (personLanguageData.getPersonLanguages() != null) {
            personLanguages = personLanguageData.getPersonLanguages();
        }

        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonLanguage> personLanguagesArrayList = new ArrayList<>();
            for (PersonLanguageJson personLanguageJson : personLanguages) {

                if (personLanguageJson.getLegacyId() == null || personLanguageJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no legacyId");
                }

                if (personLanguageJson.getCompanyCode() == null || personLanguageJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personLanguageData,
                            "no companyCode");
                }

                PersonLanguage personLanguage = dataManager.load(PersonLanguage.class)
                        .query(
                                " select e from tsadv_PersonLanguage e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of(
                                "legacyId", personLanguageJson.getLegacyId(),
                                "companyCode", personLanguageJson.getCompanyCode()))
                        .view("personLanguage.edit").list().stream().findFirst().orElse(null);

                if (personLanguage == null) {
                    return prepareError(result, methodName, personLanguageData,
                            "no tsadv_PersonLanguage with legacyId " + personLanguageJson.getLegacyId()
                                    + " and company legacyId " + personLanguageJson.getCompanyCode());
                }

                if (!personLanguagesArrayList.stream().filter(harmfulCondition1 ->
                        harmfulCondition1.getId().equals(personLanguage.getId())).findAny().isPresent()) {
                    personLanguagesArrayList.add(personLanguage);
                }
            }

            for (PersonLanguage personLanguage : personLanguagesArrayList) {
                entityManager.remove(personLanguage);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personLanguageData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }

        return prepareSuccess(result, methodName, personLanguageData);
    }

    @Override
    public BaseResult createOrUpdateUser(UserDataJson userData) {
        String methodName = "createOrUpdateUser";
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        ArrayList<UserJson> users = new ArrayList<>();
//        UserDataJson completedUserData = new UserDataJson();
//        ArrayList<UserJson> completedUsers = new ArrayList<>();

        if (userData.getUsers() != null) {
            users = userData.getUsers();
        }

        try {

            for (UserJson userJson : users) {

                if (userJson.getLogin() == null || userJson.getLogin().isEmpty()) {
                    return prepareError(result, methodName, userData,
                            "no login");
                }

                if (userJson.getEmployeeNumber() == null || userJson.getEmployeeNumber().isEmpty()) {
                    return prepareError(result, methodName, userData,
                            "no employeeNumber");
                }

//                if (userJson.getEmail() == null || userJson.getEmail().isEmpty()) {
//                    continue;
//                }

                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.login = :login")
                        .parameter("login", userJson.getLogin())
                        .view("userExt.edit")
                        .list().stream().findFirst().orElse(null);

                if (tsadvUser != null) {

                    if (integrationConfig.getIsIntegrationActiveDirectory()
                            && userJson.getEmail() != null
                            && !userJson.getEmail().isEmpty()
                            && !tsadvUser.getEmail().equals(userJson.getEmail())) {
                        tsadvUser.setEmail(userJson.getEmail());
                    }

                    String empNumber = "";

                    List<String> codes = new ArrayList<>();

                    if (userJson.getEmployeeNumber().length() > 5) {
                        if ("1".equals(userJson.getEmployeeNumber().substring(0, 1))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(1));
                            codes.add("VCM");
                        } else if ("2".equals(userJson.getEmployeeNumber().substring(0, 1))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(1));
                            codes.add("KBL");
                            codes.add("KAL");
                        } else if ("KMM-".equals(userJson.getEmployeeNumber().substring(0, 4))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(4));
                            codes.add("KMM");
                        }

                        List<PersonGroupExt> personGroupExtList = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " join e.list l " +
                                        " where current_date between l.startDate and l.endDate " +
                                        " and l.employeeNumber = :empNumber " +
                                        " and e.company.code in :codes")
                                .parameter("empNumber", empNumber)
                                .parameter("codes", codes)
                                .view("personGroupExt-for-integration-rest")
                                .list();

                        if (personGroupExtList.size() == 1) {
                            tsadvUser.setPersonGroup(personGroupExtList.get(0));
                            PersonExt personExt = personGroupExtList.get(0).getPerson();
                            if (personExt != null && userJson.getThumbnailPhoto() != null
                                    && !userJson.getThumbnailPhoto().isEmpty()
                                    && userJson.getPhotoExtension() != null
                                    && !userJson.getPhotoExtension().isEmpty()) {
                                byte[] decoder = Base64.getDecoder().decode(userJson.getThumbnailPhoto());
                                FileDescriptor file = metadata.create(FileDescriptor.class);
                                file.setCreateDate(BaseCommonUtils.getSystemDate());
                                file.setName("userPhoto" + "." + userJson.getPhotoExtension() != null
                                        && !userJson.getPhotoExtension().isEmpty()
                                        ? userJson.getPhotoExtension() : "png");
                                file.setExtension(userJson.getPhotoExtension() != null
                                        && !userJson.getPhotoExtension().isEmpty()
                                        ? userJson.getPhotoExtension() : "png");
                                file.setSize((long) decoder.length);
                                fileStorageAPI.saveFile(file, decoder);
                                personExt.setImage(file);
                                commitContext.addInstanceToCommit(personExt);
                            }
                        } else if (personGroupExtList.size() > 1) {
                            return prepareError(result, methodName, userData,
                                    "personsGroup more than 1");
                        } else {
                            return prepareError(result, methodName, userData,
                                    "no personsGroup with employeeNumber " + userJson.getEmployeeNumber());
                        }

                        commitContext.addInstanceToCommit(tsadvUser);
//                        completedUsers.add(userJson);
                    }
                } else {

                    tsadvUser = dataManager.create(TsadvUser.class);
                    tsadvUser.setLogin(userJson.getLogin());
                    if (integrationConfig.getIsIntegrationActiveDirectory()
                            && userJson.getEmail() != null
                            && !userJson.getEmail().isEmpty()
                            && !tsadvUser.getEmail().equals(userJson.getEmail())) {
                        tsadvUser.setEmail(userJson.getEmail());
                    }

                    String empNumber = "";

                    List<String> codes = new ArrayList<>();

                    if (userJson.getEmployeeNumber().length() > 5) {
                        if ("1".equals(userJson.getEmployeeNumber().substring(0, 1))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(1));
                            codes.add("VCM");
                        } else if ("2".equals(userJson.getEmployeeNumber().substring(0, 1))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(1));
                            codes.add("KBL");
                            codes.add("KAL");
                        } else if ("KMM-".equals(userJson.getEmployeeNumber().substring(0, 4))) {
                            empNumber = getEmpNumber(userJson.getEmployeeNumber().substring(4));
                            codes.add("KMM");
                        }

                        List<PersonGroupExt> personGroupExtList = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " join e.list l " +
                                        " where current_date between l.startDate and l.endDate " +
                                        " and l.employeeNumber = :empNumber " +
                                        " and e.company.code in :codes")
                                .parameter("empNumber", empNumber)
                                .parameter("codes", codes)
                                .view("personGroupExt-for-integration-rest")
                                .list();

                        if (personGroupExtList.size() == 1) {
                            tsadvUser.setPersonGroup(personGroupExtList.get(0));
                            Group group = dataManager.load(Group.class)
                                    .query("select e from sec$Group e " +
                                            " where e.name = :name ")
                                    .parameter("name", tsadvUser.getPersonGroup().getCompany() != null
                                            ? tsadvUser.getPersonGroup().getCompany().getCode()
                                            : null)
                                    .list().stream().findFirst().orElse(null);
                            if (group != null) {
                                tsadvUser.setGroup(group);
                            }
                            PersonExt personExt = personGroupExtList.get(0) != null
                                    ? personGroupExtList.get(0).getPerson()
                                    : null;
                            if (personExt != null && userJson.getThumbnailPhoto() != null
                                    && !userJson.getThumbnailPhoto().isEmpty()
                                    && userJson.getPhotoExtension() != null
                                    && !userJson.getPhotoExtension().isEmpty()) {
                                byte[] decoder = Base64.getDecoder().decode(userJson.getThumbnailPhoto());
                                FileDescriptor file = metadata.create(FileDescriptor.class);
                                file.setCreateDate(BaseCommonUtils.getSystemDate());
                                file.setName("userPhoto" + "." + userJson.getPhotoExtension() != null
                                        && !userJson.getPhotoExtension().isEmpty()
                                        ? userJson.getPhotoExtension() : "png");
                                file.setExtension(userJson.getPhotoExtension() != null
                                        && !userJson.getPhotoExtension().isEmpty()
                                        ? userJson.getPhotoExtension() : "png");
                                file.setSize((long) decoder.length);
                                fileStorageAPI.saveFile(file, decoder);
                                personExt.setImage(file);
                                commitContext.addInstanceToCommit(personExt);
                            }
                        } else if (personGroupExtList.size() > 1) {
                            return prepareError(result, methodName, userData,
                                    "personsGroup more than 1");
                        } else {
                            return prepareError(result, methodName, userData,
                                    "no personsGroup with employeeNumber " + userJson.getEmployeeNumber());
                        }

                        commitContext.addInstanceToCommit(tsadvUser);
//                        completedUsers.add(userJson);

                    }
                }
            }
//            completedUserData.setUsers(completedUsers);
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, userData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, userData);
    }

    @Override
    public BaseResult createOrUpdatePersonAbsenceBalance(AbsenceBalanceDataJson absenceBalanceData) {
        String methodName = "createOrUpdatePersonAbsenceBalance";
        ArrayList<AbsenceBalanceJson> absenceBalances = new ArrayList<>();
        if (absenceBalanceData.getAbsenceBalances() != null) {
            absenceBalances = absenceBalanceData.getAbsenceBalances();
        }
        BaseResult result = new BaseResult();
        CommitContext commitContext = new CommitContext();
        int scale = 2;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        try {
            ArrayList<AbsenceBalance> absenceBalancesCommitList = new ArrayList<>();
            for (AbsenceBalanceJson absenceBalanceJson : absenceBalances) {
                if (absenceBalanceJson.getLegacyId() == null || absenceBalanceJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no legacyId ");
                }
                if (absenceBalanceJson.getCompanyCode() == null || absenceBalanceJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no companyCode");
                }
                if (absenceBalanceJson.getPersonId() == null || absenceBalanceJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no personId");
                }
                if (absenceBalanceJson.getDate() == null || absenceBalanceJson.getDate().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no date");
                }
                if (absenceBalanceJson.getAnnualDueDays() == null || absenceBalanceJson.getAnnualDueDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no annualDueDays");
                }
                if (absenceBalanceJson.getAdditionalDueDays() == null || absenceBalanceJson.getAdditionalDueDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no additionalDueDays");
                }
                if (absenceBalanceJson.getEcologicalDueDays() == null || absenceBalanceJson.getEcologicalDueDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no ecologicalDueDays");
                }
                if (absenceBalanceJson.getDisabilityDueDays() == null || absenceBalanceJson.getDisabilityDueDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no disabilityDueDays");
                }
                if (absenceBalanceJson.getAnnualBalanceDays() == null || absenceBalanceJson.getAnnualBalanceDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no annualBalanceDays");
                }
                if (absenceBalanceJson.getAdditionalBalanceDays() == null || absenceBalanceJson.getAdditionalBalanceDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no additionalBalanceDays");
                }
                if (absenceBalanceJson.getEcologicalBalanceDays() == null || absenceBalanceJson.getEcologicalBalanceDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no ecologicalBalanceDays");
                }
                if (absenceBalanceJson.getDisabilityBalanceDays() == null || absenceBalanceJson.getDisabilityBalanceDays().isEmpty()) {
                    return prepareError(result, methodName, absenceBalanceData,
                            "no disabilityBalanceDays");
                }

                Date dateFromJson = formatter.parse(absenceBalanceJson.getDate());
                AbsenceBalance absenceBalance = absenceBalancesCommitList.stream().filter(filterAbsenceBalance ->
                        filterAbsenceBalance.getLegacyId() != null
                                && filterAbsenceBalance.getLegacyId().equals(absenceBalanceJson.getLegacyId())
                                && filterAbsenceBalance.getPersonGroup() != null
                                && filterAbsenceBalance.getPersonGroup().getLegacyId().equals(absenceBalanceJson.getPersonId())
                                && filterAbsenceBalance.getPersonGroup().getCompany() != null
                                && filterAbsenceBalance.getPersonGroup().getCompany().getLegacyId() != null
                                && filterAbsenceBalance.getPersonGroup().getCompany().getLegacyId().equals(absenceBalanceJson.getCompanyCode())
                                && filterAbsenceBalance.getDateFrom().equals(dateFromJson)
                ).findFirst().orElse(null);
                if (absenceBalance == null) {
                    absenceBalance = dataManager.load(AbsenceBalance.class)
                            .query("select e from tsadv$AbsenceBalance e " +
                                    " where e.legacyId = :legacyId " +
                                    " and e.personGroup.legacyId = :pgLegacyId" +
                                    " and e.dateFrom = :date")
                            .setParameters(ParamsMap.of("legacyId", absenceBalanceJson.getLegacyId(),
                                    "pgLegacyId", absenceBalanceJson.getPersonId(),
                                    "date", dateFromJson))
                            .view("absenceBalance.edit").list().stream().findFirst().orElse(null);

                    if (absenceBalance != null) {

                        absenceBalance.setLegacyId(absenceBalanceJson.getLegacyId());

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", absenceBalanceJson.getPersonId(),
                                        "company", absenceBalanceJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            absenceBalance.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, absenceBalanceData,
                                    "no personGroup with legacyId and companyCode : "
                                            + absenceBalanceJson.getPersonId() + " , " + absenceBalanceJson.getCompanyCode());
                        }

                        absenceBalance.setDateFrom(formatter.parse(absenceBalanceJson.getDate()));
                        absenceBalance.setDateTo(formatter.parse(absenceBalanceJson.getDate()));

                        Double annualDueDays = new BigDecimal(absenceBalanceJson.getAnnualDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setBalanceDays(annualDueDays);

                        Double additionalDueDays = new BigDecimal(absenceBalanceJson.getAdditionalDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setAdditionalBalanceDays(additionalDueDays);

                        Double ecologicalDueDays = new BigDecimal(absenceBalanceJson.getEcologicalDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setEcologicalDueDays(ecologicalDueDays);

                        Double disabilityDueDays = new BigDecimal(absenceBalanceJson.getDisabilityDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDisabilityDueDays(disabilityDueDays);

                        Double annualBalanceDays = new BigDecimal(absenceBalanceJson.getAnnualBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDaysLeft(annualBalanceDays);

                        Double additionalBalanceDays = new BigDecimal(absenceBalanceJson.getAdditionalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setExtraDaysLeft(additionalBalanceDays);

                        Double ecologicalBalanceDays = new BigDecimal(absenceBalanceJson.getEcologicalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setEcologicalDaysLeft(ecologicalBalanceDays);

                        Double disabilityBalanceDays = new BigDecimal(absenceBalanceJson.getDisabilityBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDisabilityDaysLeft(disabilityBalanceDays);

                        absenceBalancesCommitList.add(absenceBalance);
                    } else {
                        absenceBalance = metadata.create(AbsenceBalance.class);
                        absenceBalance.setId(UUID.randomUUID());
                        absenceBalance.setLegacyId(absenceBalanceJson.getLegacyId());

                        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                                .query("select e from base$PersonGroupExt e " +
                                        " where e.legacyId = :legacyId and e.company.legacyId = :company")
                                .setParameters(ParamsMap.of("legacyId", absenceBalanceJson.getPersonId(),
                                        "company", absenceBalanceJson.getCompanyCode()))
                                .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                        if (personGroupExt != null) {
                            absenceBalance.setPersonGroup(personGroupExt);
                        } else {
                            return prepareError(result, methodName, absenceBalanceData,
                                    "no personGroup with legacyId and companyCode : "
                                            + absenceBalanceJson.getPersonId() + " , " + absenceBalanceJson.getCompanyCode());
                        }


                        absenceBalance.setDateFrom(formatter.parse(absenceBalanceJson.getDate()));
                        absenceBalance.setDateTo(formatter.parse(absenceBalanceJson.getDate()));

                        Double annualDueDays = new BigDecimal(absenceBalanceJson.getAnnualDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setBalanceDays(annualDueDays);

                        Double additionalDueDays = new BigDecimal(absenceBalanceJson.getAdditionalDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setAdditionalBalanceDays(additionalDueDays);

                        Double ecologicalDueDays = new BigDecimal(absenceBalanceJson.getEcologicalDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setEcologicalDueDays(ecologicalDueDays);

                        Double disabilityDueDays = new BigDecimal(absenceBalanceJson.getDisabilityDueDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDisabilityDueDays(disabilityDueDays);

                        Double annualBalanceDays = new BigDecimal(absenceBalanceJson.getAnnualBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDaysLeft(annualBalanceDays);

                        Double additionalBalanceDays = new BigDecimal(absenceBalanceJson.getAdditionalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setExtraDaysLeft(additionalBalanceDays);

                        Double ecologicalBalanceDays = new BigDecimal(absenceBalanceJson.getEcologicalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setEcologicalDaysLeft(ecologicalBalanceDays);

                        Double disabilityBalanceDays = new BigDecimal(absenceBalanceJson.getDisabilityBalanceDays()).setScale(scale, roundingMode).doubleValue();
                        absenceBalance.setDisabilityDaysLeft(disabilityBalanceDays);


                        absenceBalancesCommitList.add(absenceBalance);
                    }
                } else {
                    absenceBalance.setLegacyId(absenceBalanceJson.getLegacyId());

                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query("select e from base$PersonGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", absenceBalanceJson.getPersonId(),
                                    "company", absenceBalanceJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);

                    if (personGroupExt != null) {
                        absenceBalance.setPersonGroup(personGroupExt);
                    } else {
                        return prepareError(result, methodName, absenceBalanceData,
                                "no personGroup with legacyId and companyCode : "
                                        + absenceBalanceJson.getPersonId() + " , " + absenceBalanceJson.getCompanyCode());
                    }

                    absenceBalance.setDateFrom(formatter.parse(absenceBalanceJson.getDate()));
                    absenceBalance.setDateTo(formatter.parse(absenceBalanceJson.getDate()));

                    Double annualDueDays = new BigDecimal(absenceBalanceJson.getAnnualDueDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setBalanceDays(annualDueDays);

                    Double additionalDueDays = new BigDecimal(absenceBalanceJson.getAdditionalDueDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setAdditionalBalanceDays(additionalDueDays);

                    Double ecologicalDueDays = new BigDecimal(absenceBalanceJson.getEcologicalDueDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setEcologicalDueDays(ecologicalDueDays);

                    Double disabilityDueDays = new BigDecimal(absenceBalanceJson.getDisabilityDueDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setDisabilityDueDays(disabilityDueDays);

                    Double annualBalanceDays = new BigDecimal(absenceBalanceJson.getAnnualBalanceDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setDaysLeft(annualBalanceDays);

                    Double additionalBalanceDays = new BigDecimal(absenceBalanceJson.getAdditionalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setExtraDaysLeft(additionalBalanceDays);

                    Double ecologicalBalanceDays = new BigDecimal(absenceBalanceJson.getEcologicalBalanceDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setEcologicalDaysLeft(ecologicalBalanceDays);

                    Double disabilityBalanceDays = new BigDecimal(absenceBalanceJson.getDisabilityBalanceDays()).setScale(scale, roundingMode).doubleValue();
                    absenceBalance.setDisabilityDaysLeft(disabilityBalanceDays);

                }
            }

            for (AbsenceBalance absenceBalance : absenceBalancesCommitList) {
                commitContext.addInstanceToCommit(absenceBalance);
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            e.printStackTrace();
            return prepareError(result, methodName, absenceBalanceData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, absenceBalanceData);
    }

    protected String getEmpNumber(String jsonEmpNumber) {
        for (int i = 0; i < jsonEmpNumber.length(); i++) {
            if (jsonEmpNumber.charAt(i) != '0') {
                return jsonEmpNumber.substring(i);
            }
        }
        return "";
    }
}
package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.*;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.entity.tb.dictionary.DicPersonQualificationType;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.integration.jsonobject.*;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.PersonEducation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
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
    protected RestIntegrationLogService restIntegrationLogService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

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
                    return prepareError(result, methodName, organizationJsons,
                            "no legacyId ");
                }
                if (organizationJson.getCompanyCode() == null || organizationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizationJsons,
                            "no companyCode");
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
                            commitContext.addInstanceToRemove(organizationExt);
//                            transactionalDataManager.remove(organizationExt);
                        }
                        organizationGroupExt.getList().clear();
                        organizationGroupExts.add(organizationGroupExt);
                    }
                }
                if (organizationGroupExt == null) {
                    organizationGroupExt = metadata.create(OrganizationGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organizationJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, organizationJsons,
                                "no base$DicCompany with legacyId " + organizationJson.getCompanyCode());
                    }
                    organizationGroupExt.setCompany(company);
                    organizationGroupExt.setLegacyId(organizationJson.getLegacyId());
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
            return prepareError(result, methodName, organizationJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, organizationJsons);
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
                    return prepareError(result, methodName, organizations,
                            "no legacyId ");
                }
                if (organization.getCompanyCode() == null || organization.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizations,
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
        return prepareSuccess(result, methodName, organizations);
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
                    return prepareError(result, methodName, jobs,
                            "no legacyId ");
                }
                if (jobJson.getCompanyCode() == null || jobJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, jobs,
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
                        return prepareError(result, methodName, jobs,
                                "no base$DicCompany with legacyId " + jobJson.getCompanyCode());
                    }
                    jobGroup.setCompany(company);
                    jobGroup.setLegacyId(jobJson.getLegacyId());
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
            return prepareError(result, methodName, jobs, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, jobs);
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
                    return prepareError(result, methodName, jobJsons,
                            "no legacyId ");
                }
                if (jobJson.getCompanyCode() == null || jobJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, jobJsons,
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
        return prepareSuccess(result, methodName, jobJsons);
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
                    return prepareError(result, methodName, positionJsons,
                            "no legacyId ");
                }
                if (positionJson.getCompanyCode() == null || positionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, positionJsons,
                            "no companyCode");
                }
                if (positionJson.getOrganizationLegacyId() == null || positionJson.getOrganizationLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionJsons,
                            "no organizationLegacyId");
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
                            commitContext.addInstanceToRemove(positionExt1);
                        }
                        positionGroupExt.getList().clear();
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
                        return prepareError(result, methodName, positionJsons,
                                "no base$DicCompany with legacyId " + positionJson.getCompanyCode());
                    }
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
                    positionExt.setGradeGroup(gradeGroup);
                }
                JobGroup jobGroup = null;
                if (positionJson.getJobLegacyId() != null && !positionJson.getJobLegacyId().isEmpty()) {
                    jobGroup = dataManager.load(JobGroup.class).query("select e from tsadv$JobGroup e " +
                            " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getJobLegacyId()))
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
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
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode", positionJson.getCompanyCode(),
                                    "legacyId", positionJson.getPositionStatusId()))
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
            return prepareError(result, methodName, positionJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, positionJsons);

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
                    return prepareError(result, methodName, positionJsons,
                            "no legacyId ");
                }
                if (positionJson.getCompanyCode() == null || positionJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, positionJsons,
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
        return prepareSuccess(result, methodName, positionJsons);
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
                    return prepareError(result, methodName, personJsons,
                            "no legacyId ");
                }
                if (personJson.getCompanyCode() == null || personJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personJsons,
                            "no companyCode");
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
                            commitContext.addInstanceToRemove(personExt);
                        }
                        personGroupExt.getList().clear();
                        personGroupExts.add(personGroupExt);
                    }
                }
                if (personGroupExt == null) {
                    personGroupExt = metadata.create(PersonGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", personJson.getCompanyCode())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, personJsons,
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
                personExt.setStartDate(personJson.getStartDate() != null
                        ? formatter.parse(personJson.getStartDate()) : null);
                personExt.setEndDate(personJson.getEndDate() != null
                        ? formatter.parse(personJson.getEndDate()) : null);
                personExt.setDateOfDeath(personJson.getDateOfDeath() != null
                        ? formatter.parse(personJson.getDateOfDeath()) : null);
                personExt.setDateOfBirth(personJson.getDateOfBirth() != null
                        ? formatter.parse(personJson.getDateOfBirth()) : null);
                personExt.setHireDate(personJson.getHireDate() != null
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
                DicPersonType personType = null;
                if (personJson.getPersonTypeId() != null && !personJson.getPersonTypeId().isEmpty()) {
                    personType = dataManager.load(DicPersonType.class)
                            .query("select e from tsadv$DicPersonType e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personJson.getPersonTypeId(),
                                    "companyCode", personJson.getCompanyCode()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setType(personType);
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
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("legacyId", personJson.getSexId(),
                                    "companyCode", personJson.getCompanyCode()))
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                }
                personExt.setSex(sex);

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
            return prepareError(result, methodName, personJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personJsons);
    }

    @Override
    public BaseResult deletePerson(PersonDataJson personData) {
        String methodName = "deletePerson";
        BaseResult result = new BaseResult();
        ArrayList<PersonJson> personJsons = new ArrayList<>();
        if (personData.getPersons() != null) {
            personJsons = personData.getPersons();
        }
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonGroupExt> personGroupExts = new ArrayList<>();
            for (PersonJson personJson : personJsons) {
                if (personJson.getLegacyId() == null || personJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, personJsons,
                            "no legacyId ");
                }
                if (personJson.getCompanyCode() == null || personJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personJsons,
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
                            "no position with legacyId and companyCode : "
                                    + personJson.getLegacyId() + " , " + personJson.getCompanyCode());
                }
                if (!personGroupExts.stream().filter(personGroupExt1 ->
                        personGroupExt1.getId().equals(personGroupExt.getId())).findAny().isPresent()) {
                    personGroupExts.add(personGroupExt);
                }

            }
            for (PersonGroupExt personGroupExt : personGroupExts) {
                for (PersonExt personExt : personGroupExt.getList()) {
                    entityManager.remove(personExt);
                }
                entityManager.remove(personGroupExt);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, personData, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personJsons);
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
            List<HierarchyElementGroup> organizationHierarchyElementGroups = new ArrayList<>();
            for (HierarchyElementJson organizationHierarchyElementJson : organizationHierarchyElementJsons) {
                if (organizationHierarchyElementJson.getLegacyId() == null
                        || organizationHierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, organizationHierarchyElementJsons,
                            "no legacyId ");
                }
                if (organizationHierarchyElementJson.getCompanyCode() == null
                        || organizationHierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizationHierarchyElementJsons,
                            "no companyCode");
                }
                if (organizationHierarchyElementJson.getSubordinateOrganizationId() == null
                        || organizationHierarchyElementJson.getSubordinateOrganizationId().isEmpty()) {
                    return prepareError(result, methodName, organizationHierarchyElementJsons,
                            "no subordinateOrganization");
                }
                HierarchyElementGroup organizationHierarchyElementGroup = organizationHierarchyElementGroups.stream().filter(
                        hierarchyElementGroup1 ->
                                hierarchyElementGroup1.getList() != null
                                        && hierarchyElementGroup1.getList().stream().anyMatch(hierarchyElementExt1 ->
                                        hierarchyElementExt1.getOrganizationGroup() != null
                                                && hierarchyElementExt1.getOrganizationGroup().getCompany() != null
                                                && hierarchyElementExt1.getOrganizationGroup().getCompany()
                                                .getLegacyId() != null
                                                && hierarchyElementExt1.getOrganizationGroup().getCompany()
                                                .getLegacyId().equals(organizationHierarchyElementJson.getCompanyCode()))
                                        && hierarchyElementGroup1.getLegacyId() != null
                                        && hierarchyElementGroup1.getLegacyId()
                                        .equals(organizationHierarchyElementJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (organizationHierarchyElementGroup == null) {
                    organizationHierarchyElementGroup = dataManager.load(HierarchyElementGroup.class)
                            .query("select e.group from base$HierarchyElementExt e " +
                                    " where e.legacyId = :legacyId and e.organizationGroup.company.legacyId = :company ")
                            .setParameters(ParamsMap.of("legacyId", organizationHierarchyElementJson.getLegacyId(),
                                    "company", organizationHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementGroup-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (organizationHierarchyElementGroup != null) {
                        for (HierarchyElementExt hierarchyElementExt : organizationHierarchyElementGroup.getList()) {
                            commitContext.addInstanceToRemove(hierarchyElementExt);
                        }
                        organizationHierarchyElementGroup.getList().clear();
                        organizationHierarchyElementGroups.add(organizationHierarchyElementGroup);
                    }
                }
                if (organizationHierarchyElementGroup == null) {
                    organizationHierarchyElementGroup = metadata.create(HierarchyElementGroup.class);
                    organizationHierarchyElementGroup.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                    organizationHierarchyElementGroup.setId(UUID.randomUUID());
                    organizationHierarchyElementGroup.setList(new ArrayList<>());
                    organizationHierarchyElementGroups.add(organizationHierarchyElementGroup);
                }
                HierarchyElementExt hierarchyElementExt = metadata.create(HierarchyElementExt.class);
                OrganizationGroupExt subordinateOrganizationGroupExt = null;
                if (organizationHierarchyElementJson.getSubordinateOrganizationId() != null &&
                        !organizationHierarchyElementJson.getSubordinateOrganizationId().isEmpty()) {
                    subordinateOrganizationGroupExt = dataManager.load(OrganizationGroupExt.class).query(
                            "select e from base$OrganizationGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("companyCode"
                                    , organizationHierarchyElementJson.getCompanyCode()
                                    , "legacyId", organizationHierarchyElementJson.getSubordinateOrganizationId()))
                            .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    hierarchyElementExt.setOrganizationGroup(subordinateOrganizationGroupExt);
                }
                if (subordinateOrganizationGroupExt == null) {
                    return prepareError(result, methodName, organizationHierarchyElementJsons,
                            "not found subordinateOrganization with legacyId = " +
                                    organizationHierarchyElementJson.getSubordinateOrganizationId());
                }
                Hierarchy hierarchy = null;
                hierarchy = dataManager.load(Hierarchy.class).query(
                        "select e from base$Hierarchy e " +
                                " where e.primaryFlag = TRUE")
                        .view("hierarchy.view").list().stream().findFirst().orElse(null);
                hierarchyElementExt.setHierarchy(hierarchy);
                HierarchyElementExt parent = null;
                if (organizationHierarchyElementJson.getParentOrganizationId() != null
                        && !organizationHierarchyElementJson.getParentOrganizationId().isEmpty()) {
                    parent = dataManager.load(HierarchyElementExt.class).query(
                            "select e from base$HierarchyElementExt e " +
                                    " where e.organizationGroup.legacyId = :legacyId " +
                                    " and e.organizationGroup.company.legacyId = :companyCode ")
                            .setParameters(ParamsMap.of("legacyId"
                                    , organizationHierarchyElementJson.getParentOrganizationId()
                                    , "companyCode", organizationHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    hierarchyElementExt.setParent(parent);
                    hierarchyElementExt.setParentGroup(parent != null ? parent.getGroup() : null);
                }
                hierarchyElementExt.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                hierarchyElementExt.setStartDate(organizationHierarchyElementJson.getStartDate() != null
                        ? formatter.parse(organizationHierarchyElementJson.getStartDate()) : null);
                hierarchyElementExt.setEndDate(organizationHierarchyElementJson.getEndDate() != null
                        ? formatter.parse(organizationHierarchyElementJson.getEndDate()) : null);
                hierarchyElementExt.setLegacyId(organizationHierarchyElementJson.getLegacyId());
                hierarchyElementExt.setGroup(organizationHierarchyElementGroup);
                hierarchyElementExt.setWriteHistory(false);
                hierarchyElementExt.setElementType(ElementType.ORGANIZATION);
                organizationHierarchyElementGroup.getList().add(hierarchyElementExt);
            }
            for (HierarchyElementGroup hierarchyElementGroup : organizationHierarchyElementGroups) {
                commitContext.addInstanceToCommit(hierarchyElementGroup);
                for (HierarchyElementExt hierarchyElementExt : hierarchyElementGroup.getList()) {
                    HierarchyElementJson foundHierarchyElementJson = organizationHierarchyElementJsons.stream()
                            .filter(hierarchyElementJson ->
                                    hierarchyElementJson.getLegacyId().equals(hierarchyElementExt.getLegacyId())
                                            && hierarchyElementJson.getCompanyCode()
                                            .equals(hierarchyElementExt.getOrganizationGroup().getCompany().getLegacyId()))
                            .findFirst().orElse(null);
                    organizationHierarchyElementGroups.forEach(hierarchyElementGroup1 ->
                            hierarchyElementGroup1.getList().forEach(hierarchyElementExt1 -> {
                                if (hierarchyElementExt1.getOrganizationGroup().getCompany().getLegacyId()
                                        .equals(foundHierarchyElementJson.getCompanyCode())
                                        && hierarchyElementExt1.getOrganizationGroup().getLegacyId()
                                        .equals(foundHierarchyElementJson.getParentOrganizationId())) {
                                    hierarchyElementExt.setParent(hierarchyElementExt1);
                                    hierarchyElementExt.setParentGroup(hierarchyElementGroup);
                                }
                            }));
                    commitContext.addInstanceToCommit(hierarchyElementExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, organizationHierarchyElementJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, organizationHierarchyElementJsons);
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
                    return prepareError(result, methodName, hierarchyElementJsons,
                            "no legacyId ");
                }
                if (hierarchyElementJson.getCompanyCode() == null || hierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementJsons,
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
        return prepareSuccess(result, methodName, hierarchyElementJsons);
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
            List<HierarchyElementGroup> positionHierarchyElementGroups = new ArrayList<>();
            for (HierarchyElementJson positionHierarchyElementJson : positionHierarchyElementJsons) {
                if (positionHierarchyElementJson.getLegacyId() == null
                        || positionHierarchyElementJson.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, positionHierarchyElementJsons,
                            "no legacyId ");
                }
                if (positionHierarchyElementJson.getCompanyCode() == null
                        || positionHierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, positionHierarchyElementJsons,
                            "no companyCode");
                }
                HierarchyElementGroup positionHierarchyElementGroup = positionHierarchyElementGroups.stream().filter(
                        hierarchyElementGroup1 ->
                                hierarchyElementGroup1.getList() != null
                                        && hierarchyElementGroup1.getList().stream().anyMatch(hierarchyElementExt1 ->
                                        hierarchyElementExt1.getPositionGroup() != null
                                                && hierarchyElementExt1.getPositionGroup().getList() != null
                                                && hierarchyElementExt1.getPositionGroup().getList().stream()
                                                .anyMatch(positionExt -> positionExt.getOrganizationGroupExt() != null
                                                        && positionExt.getOrganizationGroupExt().getCompany() != null
                                                        && positionExt.getOrganizationGroupExt().getCompany()
                                                        .getLegacyId() != null
                                                        && positionExt.getOrganizationGroupExt().getCompany()
                                                        .getLegacyId()
                                                        .equals(positionHierarchyElementJson.getCompanyCode())))
                                        && hierarchyElementGroup1.getLegacyId() != null
                                        && hierarchyElementGroup1.getLegacyId()
                                        .equals(positionHierarchyElementJson.getLegacyId()))
                        .findFirst().orElse(null);
                if (positionHierarchyElementGroup == null) {
                    positionHierarchyElementGroup = dataManager.load(HierarchyElementGroup.class)
                            .query("select e.group from base$HierarchyElementExt e " +
                                    " where e.legacyId = :legacyId and e.positionGroup.id in " +
                                    " (select p.group.id from base$PositionExt p " +
                                    " where p.organizationGroupExt.company.legacyId = :company) ")
                            .setParameters(ParamsMap.of("legacyId", positionHierarchyElementJson.getLegacyId(),
                                    "company", positionHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementGroup-for-integration-rest").list().stream().findFirst().orElse(null);
                    if (positionHierarchyElementGroup != null) {
                        for (HierarchyElementExt hierarchyElementExt : positionHierarchyElementGroup.getList()) {
                            commitContext.addInstanceToRemove(hierarchyElementExt);
                        }
                        positionHierarchyElementGroup.getList().clear();
                        positionHierarchyElementGroups.add(positionHierarchyElementGroup);
                    }
                }
                if (positionHierarchyElementGroup == null) {
                    positionHierarchyElementGroup = metadata.create(HierarchyElementGroup.class);
                    positionHierarchyElementGroup.setLegacyId(positionHierarchyElementJson.getLegacyId());
                    positionHierarchyElementGroup.setId(UUID.randomUUID());
                    positionHierarchyElementGroup.setList(new ArrayList<>());
                    positionHierarchyElementGroups.add(positionHierarchyElementGroup);
                }
                HierarchyElementExt hierarchyElementExt = metadata.create(HierarchyElementExt.class);
                PositionGroupExt subordinatePositionGroupExt = null;
                if (positionHierarchyElementJson.getSubordinatePositionId() != null &&
                        !positionHierarchyElementJson.getSubordinatePositionId().isEmpty()) {
                    subordinatePositionGroupExt = dataManager.load(PositionGroupExt.class).query(
                            "select e from base$PositionGroupExt e " +
                                    " where e.legacyId = :legacyId and e.id in " +
                                    " (select p.group.id from base$PositionExt p " +
                                    " where p.organizationGroupExt.company.legacyId = :companyCode)")
                            .setParameters(ParamsMap.of("companyCode"
                                    , positionHierarchyElementJson.getCompanyCode()
                                    , "legacyId", positionHierarchyElementJson.getSubordinatePositionId()))
                            .view("positionGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    hierarchyElementExt.setPositionGroup(subordinatePositionGroupExt);
                }
                if (subordinatePositionGroupExt == null) {
                    return prepareError(result, methodName, positionHierarchyElementJsons,
                            "not found subordinatePosition with legacyId = " +
                                    positionHierarchyElementJson.getSubordinatePositionId());
                }
                UUID positionStructureId = positionStructureConfig.getPositionStructureId();
                if (positionStructureId == null) {
                    return prepareError(result, methodName, positionHierarchyElementJsons,
                            "set property tal.selfService.positionStructureId for posiiton structure");
                }
                Hierarchy hierarchy = null;
                hierarchy = dataManager.load(Hierarchy.class).query(
                        "select e from base$Hierarchy e " +
                                " where e.id = :positionStructureId")
                        .parameter("positionStructureId", positionStructureId)
                        .view("hierarchy.view").list().stream().findFirst().orElse(null);
                hierarchyElementExt.setHierarchy(hierarchy);
                HierarchyElementExt parent = null;
                if (positionHierarchyElementJson.getParentPositionId() != null
                        && !positionHierarchyElementJson.getParentPositionId().isEmpty()) {
                    parent = dataManager.load(HierarchyElementExt.class).query(
                            "select e from base$HierarchyElementExt e " +
                                    " where e.positionGroup.legacyId = :legacyId " +
                                    " and e.positionGroup.id in " +
                                    " (select p.group.id from base$PositionExt p " +
                                    " where  p.organizationGroupExt.company.legacyId = :companyCode) ")
                            .setParameters(ParamsMap.of("legacyId"
                                    , positionHierarchyElementJson.getParentPositionId()
                                    , "companyCode", positionHierarchyElementJson.getCompanyCode()))
                            .view("hierarchyElementExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    hierarchyElementExt.setParent(parent);
                    hierarchyElementExt.setParentGroup(parent != null ? parent.getGroup() : null);
                }
                hierarchyElementExt.setLegacyId(positionHierarchyElementJson.getLegacyId());
                hierarchyElementExt.setStartDate(positionHierarchyElementJson.getStartDate() != null
                        ? formatter.parse(positionHierarchyElementJson.getStartDate()) : null);
                hierarchyElementExt.setEndDate(positionHierarchyElementJson.getEndDate() != null
                        ? formatter.parse(positionHierarchyElementJson.getEndDate()) : null);
                hierarchyElementExt.setLegacyId(positionHierarchyElementJson.getLegacyId());
                hierarchyElementExt.setGroup(positionHierarchyElementGroup);
                hierarchyElementExt.setWriteHistory(false);
                hierarchyElementExt.setElementType(ElementType.POSITION);
                positionHierarchyElementGroup.getList().add(hierarchyElementExt);
            }
            for (HierarchyElementGroup hierarchyElementGroup : positionHierarchyElementGroups) {
                commitContext.addInstanceToCommit(hierarchyElementGroup);
                for (HierarchyElementExt hierarchyElementExt : hierarchyElementGroup.getList()) {
                    HierarchyElementJson foundHierarchyElementJson = positionHierarchyElementJsons.stream()
                            .filter(hierarchyElementJson ->
                                    hierarchyElementJson.getLegacyId().equals(hierarchyElementExt.getLegacyId())
                                            && hierarchyElementExt.getPositionGroup().getList().stream()
                                            .anyMatch(positionExt -> positionExt.getOrganizationGroupExt().getCompany()
                                                    .getLegacyId().equals(hierarchyElementJson.getCompanyCode())))
                            .findFirst().orElse(null);
                    positionHierarchyElementGroups.forEach(hierarchyElementGroup1 ->
                            hierarchyElementGroup1.getList().forEach(hierarchyElementExt1 -> {
                                if (hierarchyElementExt1.getPositionGroup().getList().stream()
                                        .anyMatch(positionExt -> positionExt.getOrganizationGroupExt().getCompany()
                                                .getLegacyId().equals(foundHierarchyElementJson.getCompanyCode()))
                                        && hierarchyElementExt1.getPositionGroup().getLegacyId()
                                        .equals(foundHierarchyElementJson.getParentPositionId())) {
                                    hierarchyElementExt.setParent(hierarchyElementExt1);
                                    hierarchyElementExt.setParentGroup(hierarchyElementGroup);
                                }
                            }));
                    commitContext.addInstanceToCommit(hierarchyElementExt);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, positionHierarchyElementJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, positionHierarchyElementJsons);
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
                    return prepareError(result, methodName, hierarchyElementJsons,
                            "no legacyId ");
                }
                if (hierarchyElementJson.getCompanyCode() == null || hierarchyElementJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, hierarchyElementJsons,
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
        return prepareSuccess(result, methodName, hierarchyElementJsons);
    }

    protected <T extends Serializable> BaseResult prepareSuccess(BaseResult baseResult, String methodName, Serializable params) {
        baseResult.setSuccess(true);
        baseResult.setSuccessMessage("success");
        createLog(methodName, baseResult, params);
        return baseResult;
    }

    protected BaseResult prepareError(BaseResult baseResult, String methodName, Serializable params, String errorMessage) {
        baseResult.setSuccess(false);
        baseResult.setErrorMessage(errorMessage);
        createLog(methodName, baseResult, params);
        return baseResult;
    }

    protected void createLog(String methodName, BaseResult baseResult, Serializable param) {
        if (baseResult.getErrorMessage() != null && !baseResult.getErrorMessage().equals("")) {
            UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);
            String login = userSessionSource.getUserSession().getUser().getLogin();
            restIntegrationLogService.log(login, methodName, toJson(param), baseResult.getErrorMessage(), baseResult.isSuccess(), new Date());
        }
    }

    protected String toJson(Serializable params) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
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
                    return prepareError(result, methodName, assignmentJsons,
                            "no legacyId");
                }
                if (assignmentJson.getCompanyCode() == null || assignmentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no companyCode");
                }
                if (assignmentJson.getPersonGroup() == null || assignmentJson.getPersonGroup().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no personGroup");
                }
                if (assignmentJson.getOrganizationGroup() == null || assignmentJson.getOrganizationGroup().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no organizationGroup()");
                }
                if (assignmentJson.getJobGroup() == null || assignmentJson.getJobGroup().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no jobGroup");
                }
                if (assignmentJson.getPositionGroup() == null || assignmentJson.getPositionGroup().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no positionGroup");
                }
                if (assignmentJson.getGrade() == null || assignmentJson.getGrade().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no grade");
                }
                if (assignmentJson.getStartDate() == null || assignmentJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no startDate");
                }
                if (assignmentJson.getEndDate() == null || assignmentJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no endDate");
                }
                if (assignmentJson.getAssignmentNumber() == null || assignmentJson.getAssignmentNumber().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no assignmentNumber");
                }
                if (assignmentJson.getAssignDate() == null || assignmentJson.getAssignDate().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
                            "no assignDate");
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
                                        .equals(assignmentJson.getPersonGroup())
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
                                    "personLegacyId", assignmentJson.getPersonGroup()))
                            .view("assignmentGroup.view")
                            .list().stream().findFirst().orElse(null);
                    if (assignmentGroupExt != null) {
                        for (AssignmentExt assignmentExt : assignmentGroupExt.getList()) {
                            commitContext.addInstanceToRemove(assignmentExt);
                        }
                        assignmentGroupExt.getList().clear();
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
                        return prepareError(result, methodName, assignmentJsons,
                                "no base$DicCompany with legacyId " + assignmentJson.getCompanyCode());
                    }
                    assignmentGroupExt.setLegacyId(assignmentJson.getLegacyId());
                    assignmentGroupExt.setId(UUID.randomUUID());
                    assignmentGroupExt.setAssignmentNumber(assignmentJson.getAssignmentNumber());
                    assignmentGroupExt.setList(new ArrayList<>());
                    assignmentGroupExtList.add(assignmentGroupExt);
                }

                AssignmentExt assignmentExt = metadata.create(AssignmentExt.class);
                assignmentExt.setAssignmentStatus(assignmentJson.getStatus() != null
                        && !assignmentJson.getStatus().isEmpty()
                        ? dataManager.load(DicAssignmentStatus.class)
                        .query("select e from tsadv$DicAssignmentStatus e " +
                                " where e.legacyId = :legacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", assignmentJson.getStatus(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("dicAssignmentStatus-edit")
                        .list().stream().findFirst().orElse(null)
                        : dataManager.load(DicAssignmentStatus.class)
                        .query("select e from tsadv$DicAssignmentStatus e " +
                                " where e.code = 'ACTIVE'")
                        .list().stream().findFirst().orElse(null));
                PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                        .query("select e from base$PersonGroupExt e " +
                                " where e.legacyId = :pgLegacyId " +
                                " and e.company.legacyId = :companyCode ")
                        .setParameters(ParamsMap.of("pgLegacyId", assignmentJson.getPersonGroup(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("personGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (personGroupExt != null) {
                    assignmentExt.setPersonGroup(personGroupExt);
                } else {
                    return prepareError(result, methodName, assignmentJsons,
                            "no base$PersonGroupExt with legacyId " + assignmentJson.getPersonGroup()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                OrganizationGroupExt organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                        .query("select e from base$OrganizationGroupExt e " +
                                " where e.legacyId = :ogLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("ogLegacyId", assignmentJson.getOrganizationGroup(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("organizationGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (organizationGroupExt != null) {
                    assignmentExt.setOrganizationGroup(organizationGroupExt);
                } else {
                    return prepareError(result, methodName, assignmentJsons,
                            "no base$OrganizationGroupExt with legacyId " + assignmentJson.getOrganizationGroup()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                JobGroup jobGroup = dataManager.load(JobGroup.class)
                        .query("select e from tsadv$JobGroup e " +
                                " where e.legacyId = :jgLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("jgLegacyId", assignmentJson.getJobGroup(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("jobGroup-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (jobGroup != null) {
                    assignmentExt.setJobGroup(jobGroup);
                } else {
                    return prepareError(result, methodName, assignmentJsons,
                            "no tsadv$JobGroup with legacyId " + assignmentJson.getJobGroup()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                PositionGroupExt positionGroupExt = dataManager.load(PositionGroupExt.class)
                        .query("select e.group from base$PositionExt e " +
                                " where e.organizationGroupExt.company.legacyId = :companyCode " +
                                " and e.group.legacyId = :legacyId ")
                        .setParameters(ParamsMap.of("companyCode", assignmentJson.getCompanyCode(),
                                "legacyId", assignmentJson.getPositionGroup()))
                        .view("positionGroupExt-for-integration-rest")
                        .list().stream().findFirst().orElse(null);
                if (positionGroupExt != null) {
                    assignmentExt.setPositionGroup(positionGroupExt);
                } else {
                    return prepareError(result, methodName, assignmentJsons,
                            "no base$PositionGroupExt with legacyId " + assignmentJson.getPositionGroup()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                GradeGroup gradeGroup = dataManager.load(GradeGroup.class)
                        .query("select e from tsadv$GradeGroup e" +
                                " where e.legacyId = :ggLegacyId " +
                                " and e.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("ggLegacyId", assignmentJson.getGrade(),
                                "companyCode", assignmentJson.getCompanyCode()))
                        .view("gradeGroup.browse")
                        .list().stream().findFirst().orElse(null);
                if (gradeGroup != null) {
                    assignmentExt.setGradeGroup(gradeGroup);
                } else {
                    return prepareError(result, methodName, assignmentJsons,
                            "no tsadv$GradeGroup with legacyId " + assignmentJson.getGrade()
                                    + " and company legacyId " + assignmentJson.getCompanyCode());
                }
                assignmentExt.setWriteHistory(false);
                assignmentExt.setLegacyId(assignmentJson.getLegacyId());
                assignmentExt.setStartDate(formatter.parse(assignmentJson.getStartDate()));
                assignmentExt.setEndDate(formatter.parse(assignmentJson.getEndDate()));
                assignmentExt.setAssignDate(formatter.parse(assignmentJson.getAssignDate()));
                assignmentExt.setFte(assignmentJson.getFte() != null && !assignmentJson.getFte().isEmpty()
                        ? Double.parseDouble(assignmentJson.getFte()) : null);
                assignmentExt.setProbationEndDate(formatter.parse(assignmentJson.getProbationPeriodEndDate()));
                assignmentExt.setPrimaryFlag(Boolean.valueOf(assignmentJson.getPrimaryFlag()));
                assignmentExt.setGroup(assignmentGroupExt);
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
            return prepareError(result, methodName, assignmentJsons, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, assignmentJsons);
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
                    return prepareError(result, methodName, assignmentJsons,
                            "no legacyId ");
                }
                if (assignmentJson.getCompanyCode() == null || assignmentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, assignmentJsons,
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
                    return prepareError(result, methodName, salarys,
                            "no legacyId ");
                }
                if (salaryJson.getCompanyCode() == null || salaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, salarys,
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
                        return prepareError(result, methodName, salarys,
                                "no base$AssignmentGroupExt with legacyId " + salaryJson.getAssignmentLegacyId()
                                        + " and company legacyId " + salaryJson.getCompanyCode());
                    }
                    salary.setAmount(salaryJson.getAmount() != null && !salaryJson.getAmount().isEmpty()
                            ? Double.valueOf(salaryJson.getAmount())
                            : null);
                    salary.setCurrency(salaryJson.getCurrency() != null && !salaryJson.getCurrency().isEmpty()
                            ? dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.legacyId = :cLegacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("cLegacyId", salaryJson.getCurrency(),
                                    "companyCode", salaryJson.getCompanyCode()))
                            .list().stream().findFirst().orElse(null)
                            : dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.code = 'KZT'")
                            .list().stream().findFirst().orElse(null));
                    salary.setNetGross(GrossNet.fromId(salaryJson.getNetGross() != null
                            && !salaryJson.getNetGross().isEmpty()
                            ? salaryJson.getNetGross()
                            : "GROSS"));
                    salary.setType(SalaryType.fromId(salaryJson.getSalaryType() != null
                            && !salaryJson.getSalaryType().isEmpty()
                            ? salaryJson.getNetGross()
                            : "MONTHLYRATE"));
                    salary.setStartDate(formatter.parse(salaryJson.getStartDate()));
                    salary.setEndDate(formatter.parse(salaryJson.getEndDate()));
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
                        return prepareError(result, methodName, salarys,
                                "no base$AssignmentGroupExt with legacyId " + salaryJson.getAssignmentLegacyId()
                                        + " and company legacyId " + salaryJson.getCompanyCode());
                    }
                    salary.setAmount(salaryJson.getAmount() != null && !salaryJson.getAmount().isEmpty()
                            ? Double.valueOf(salaryJson.getAmount())
                            : null);
                    salary.setCurrency(salaryJson.getCurrency() != null && !salaryJson.getCurrency().isEmpty()
                            ? dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.legacyId = :cLegacyId " +
                                    " and e.company.legacyId = :companyCode")
                            .setParameters(ParamsMap.of("cLegacyId", salaryJson.getCurrency(),
                                    "companyCode", salaryJson.getCompanyCode()))
                            .list().stream().findFirst().orElse(null)
                            : dataManager.load(DicCurrency.class)
                            .query("select e from base$DicCurrency e " +
                                    " where e.code = 'KZT'")
                            .list().stream().findFirst().orElse(null));
                    salary.setNetGross(GrossNet.fromId(salaryJson.getNetGross() != null
                            && !salaryJson.getNetGross().isEmpty()
                            ? salaryJson.getNetGross()
                            : "GROSS"));
                    salary.setType(SalaryType.fromId(salaryJson.getSalaryType() != null
                            && !salaryJson.getSalaryType().isEmpty()
                            ? salaryJson.getSalaryType()
                            : "MONTHLYRATE"));
                    salary.setStartDate(formatter.parse(salaryJson.getStartDate()));
                    salary.setEndDate(formatter.parse(salaryJson.getEndDate()));
                    salary.setWriteHistory(false);
                    commitContext.addInstanceToCommit(salary);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, salarys, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, salarys);
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
                    return prepareError(result, methodName, salarys,
                            "no legacyId ");
                }
                if (salaryJson.getCompanyCode() == null || salaryJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, salarys,
                            "no companyCode");
                }
                Salary salary = dataManager.load(Salary.class)
                        .query("select e from tsadv$Salary e " +
                                " where e.legacyId = :legacyId " +
                                " and e.assignmentGroup.legacyId = :agLegacyId")
                        .setParameters(ParamsMap.of("legacyId", salaryJson.getLegacyId(),
                                "agLegacyId", salaryJson.getAssignmentLegacyId()))
                        .view("salary.view").list().stream().findFirst().orElse(null);

                if (salary == null) {
                    return prepareError(result, methodName, salaryJson,
                            "no salary with legacyId and assignmentLegacyId : "
                                    + salaryJson.getLegacyId() + " , " + salaryJson.getAssignmentLegacyId());
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
                    return prepareError(result, methodName, personDocuments,
                            "no legacyId");
                }
                if (personDocumentJson.getCompanyCode() == null || personDocumentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no companyCode");
                }
                if (personDocumentJson.getPersonId() == null || personDocumentJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no personId");
                }
                if (personDocumentJson.getDocumentTypeId() == null || personDocumentJson.getDocumentTypeId().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no documentType");
                }
                if (personDocumentJson.getDocumentNumber() == null || personDocumentJson.getDocumentNumber().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no documentNumber");
                }
                if (personDocumentJson.getIssueDate() == null || personDocumentJson.getIssueDate().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no issueDate");
                }
                if (personDocumentJson.getExpiredDate() == null || personDocumentJson.getExpiredDate().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no expiredDate");
                }
                if (personDocumentJson.getIssueAuthorityId() == null || personDocumentJson.getIssueAuthorityId().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no issueAuthority");
                }
                if (personDocumentJson.getStatus() == null || personDocumentJson.getStatus().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no status");
                }
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
                        return prepareError(result, methodName, personDocuments,
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
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv$DicDocumentType with legacyId " + personDocumentJson.getDocumentTypeId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    personDocument.setDocumentNumber(personDocumentJson.getDocumentNumber());
                    personDocument.setIssueDate(formatter.parse(personDocumentJson.getIssueDate()));
                    personDocument.setExpiredDate(formatter.parse(personDocumentJson.getExpiredDate()));

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
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv_DicIssuingAuthority with legacyId " + personDocumentJson.getIssueAuthorityId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
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
                    } else {
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv$DicApprovalStatus with legacyId " + personDocumentJson.getStatus()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personDocument);
                }
                if (personDocument == null) {
                    personDocument = metadata.create(PersonDocument.class);
                    personDocument.setId(UUID.randomUUID());
                    personDocument.setLegacyId(personDocumentJson.getLegacyId());
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
                        return prepareError(result, methodName, personDocuments,
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
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv$DicDocumentType with legacyId " + personDocumentJson.getDocumentTypeId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    personDocument.setDocumentNumber(personDocumentJson.getDocumentNumber());
                    personDocument.setIssueDate(formatter.parse(personDocumentJson.getIssueDate()));
                    personDocument.setExpiredDate(formatter.parse(personDocumentJson.getExpiredDate()));

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
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv_DicIssuingAuthority with legacyId " + personDocumentJson.getIssueAuthorityId()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
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
                    } else {
                        return prepareError(result, methodName, personDocuments,
                                "no tsadv$DicApprovalStatus with legacyId " + personDocumentJson.getStatus()
                                        + " and company legacyId " + personDocumentJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personDocument);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personDocuments, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personDocuments);
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
                    return prepareError(result, methodName, personDocuments,
                            "no legacyId");
                }
                if (personDocumentJson.getCompanyCode() == null || personDocumentJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no companyCode");
                }
                if (personDocumentJson.getPersonId() == null || personDocumentJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personDocuments,
                            "no personId");
                }

                PersonDocument personDocument = dataManager.load(PersonDocument.class)
                        .query("select e from tsadv$PersonDocument e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pgLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode")
                        .setParameters(ParamsMap.of("legacyId", personDocumentJson.getLegacyId(),
                                "pgLegacyId", personDocumentJson.getPersonId(),
                                "companyCode", personDocumentJson.getCompanyCode()))
                        .view("personDocument.edit").list().stream().findFirst().orElse(null);

                if (personDocument == null) {
                    return prepareError(result, methodName, personDocumentJson,
                            "no personDocument with legacyId and personId : "
                                    + personDocumentJson.getLegacyId() + " , " + personDocumentJson.getPersonId() +
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
                    return prepareError(result, methodName, personQualifications,
                            "no legacyId ");
                }
                if (personQualificationJson.getCompanyCode() == null || personQualificationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no companyCode");
                }
                if (personQualificationJson.getSchool() == null || personQualificationJson.getSchool().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no school");
                }
                if (personQualificationJson.getStartDate() == null || personQualificationJson.getStartDate().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no startDate");
                }
                if (personQualificationJson.getEndDate() == null || personQualificationJson.getEndDate().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no endDate");
                }
                if (personQualificationJson.getQualificationTypeId() == null || personQualificationJson.getQualificationTypeId().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no qualificationTypeId");
                }
                if (personQualificationJson.getDocumentNumber() == null || personQualificationJson.getDocumentNumber().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no documentNumber");
                }
                if (personQualificationJson.getDocumentDate() == null || personQualificationJson.getDocumentDate().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
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
                        return prepareError(result, methodName, personQualificationJson,
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
                        return prepareError(result, methodName, personQualificationJson,
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
                        return prepareError(result, methodName, personQualificationJson,
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
                        return prepareError(result, methodName, personQualificationJson,
                                "no tsadv$DicPersonQualificationType with legacyId " + personQualificationJson.getQualificationTypeId()
                                        + " and company legacyId " + personQualificationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personQualification);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personQualifications, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personQualifications);
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
                    return prepareError(result, methodName, personQualifications,
                            "no legacyId");
                }
                if (personQualificationJson.getCompanyCode() == null || personQualificationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
                            "no companyCode");
                }
                if (personQualificationJson.getPersonId() == null || personQualificationJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personQualifications,
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
                    return prepareError(result, methodName, personQualificationJson,
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
        if(personContactData.getPersonContacts() != null){
            personContacts = personContactData.getPersonContacts();
        }
        try{
            for(PersonContactJson personContactJson : personContacts){

                if(personContactJson.getLegacyId() == null || personContactJson.getLegacyId().isEmpty()){
                    return prepareError(result, methodName, personContacts,
                            "no legacyId");
                }

                if(personContactJson.getCompanyCode() == null || personContactJson.getCompanyCode().isEmpty()){
                    return prepareError(result, methodName, personContacts,
                            "no companyCode");
                }

                if (personContactJson.getPersonId() == null || personContactJson.getPersonId().isEmpty()){
                    return prepareError(result,methodName,personContacts,
                            "no personId");
                }

                PersonContact personContact = dataManager.load(PersonContact.class)
                        .query(
                                " select e from tsadv$PersonContact e " +
                                " where e.legacyId = :legacyId " +
                                " and e.personGroup.legacyId = :pgLegacyId " +
                                " and e.personGroup.company.legacyId = :companyCode " +
                                " and e.type.legacyId = :tpLegacyId")
                        .setParameters(ParamsMap.of(
                                "legacyId",personContactJson.getLegacyId(),
                                "pgLegacyId",personContactJson.getPersonId(),
                                "companyCode",personContactJson.getCompanyCode(),
                                "tpLegacyId",personContactJson.getType()))
                        .view("personContact.edit").list().stream().findFirst().orElse(null);

                if(personContact != null){
                    personContact.setLegacyId(personContactJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query(
                                    " select e from base$PersonGroupExt e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.company.legacyId = :company ")
                            .setParameters(ParamsMap.of(
                                    "legacyId",personContactJson.getPersonId(),
                                    "company",personContactJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if(personGroupExt != null){
                        personContact.setPersonGroup(personGroupExt);
                    }else{
                        return prepareError(result,methodName,personContacts,
                                "no base$PersonGroupExt with legacyId " + personContactJson.getPersonId()
                                 + " and company legacyId " + personContactJson.getCompanyCode());
                    }

                    DicPhoneType type = dataManager.load(DicPhoneType.class)
                            .query(
                                    "select e from tsadv$DicPhoneType e " +
                                    " where e.legacyId = :legacyId ")
                            .parameter("legacyId",personContactJson.getType())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if(type != null){
                        personContact.setType(type);
                    }else{
                        return prepareError(result,methodName,personContactJson.getType(),"" +
                                "no tsadv$DicPhoneType with legacyId " + personContactJson.getType());
                    }
                    personContact.setContactValue(personContactJson.getValue());
                    commitContext.addInstanceToCommit(personContact);
                }
                if(personContact == null)
                    personContact = metadata.create(PersonContact.class);
                    personContact.setId(UUID.randomUUID());
                    personContact.setLegacyId(personContactJson.getLegacyId());
                    PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class)
                            .query(
                                    " select e from base$PersonGroupExt e " +
                                            " where e.legacyId = :legacyId " +
                                            " and e.company.legacyId = :company ")
                            .setParameters(ParamsMap.of(
                                    "legacyId",personContactJson.getPersonId(),
                                    "company",personContactJson.getCompanyCode()))
                            .view("personGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                    if(personGroupExt != null){
                        personContact.setPersonGroup(personGroupExt);
                    }else{
                        return prepareError(result,methodName,personContacts,
                                "no base$PersonGroupExt with legacyId " + personContactJson.getPersonId()
                                        + " and company legacyId " + personContactJson.getCompanyCode());
                    }

                    DicPhoneType type = dataManager.load(DicPhoneType.class)
                            .query(
                                    "select e from tsadv$DicPhoneType e " +
                                            " where e.legacyId = :legacyId ")
                            .parameter("legacyId",personContactJson.getType())
                            .view(View.BASE).list().stream().findFirst().orElse(null);
                    if(type != null){
                        personContact.setType(type);
                    }else{
                        return prepareError(result,methodName,personContactJson.getType(),"" +
                                "no tsadv$DicPhoneType with legacyId " + personContactJson.getType());
                    }
                    personContact.setContactValue(personContactJson.getValue());
                    commitContext.addInstanceToCommit(personContact);
                }
                dataManager.commit(commitContext);
            } catch (Exception e) {
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
        if(personContactData.getPersonContacts() != null){
            personContacts = personContactData.getPersonContacts();
        }

        try(Transaction tx = persistence.getTransaction()){
            EntityManager entityManager = persistence.getEntityManager();
            ArrayList<PersonContact> personContactArrayList = new ArrayList<>();
            for(PersonContactJson personContactJson : personContacts){

                if(personContactJson.getLegacyId() == null || personContactJson.getLegacyId().isEmpty()){
                    return prepareError(result, methodName, personContacts,
                            "no legacyId");
                }

                if(personContactJson.getCompanyCode() == null || personContactJson.getCompanyCode().isEmpty()){
                    return prepareError(result, methodName, personContacts,
                            "no companyCode");
                }

                PersonContact personContact = dataManager.load(PersonContact.class)
                        .query(
                                " select e from tsadv$PersonContact e " +
                                        " where e.legacyId = :legacyId " +
                                        " and e.personGroup.company.legacyId = :companyCode ")
                        .setParameters(ParamsMap.of(
                                "legacyId",personContactJson.getLegacyId(),
                                "companyCode",personContactJson.getCompanyCode()))
                        .view("personContact.edit").list().stream().findFirst().orElse(null);

                if(personContact == null){
                    return prepareError(result,methodName,personContactJson,
                            "no PersonContact with legacyId " + personContactJson.getPersonId()
                                    + " and company legacyId " + personContactJson.getCompanyCode());
                }

                if(!personContactArrayList.stream().filter(personContact1 ->
                        personContact1.getId().equals(personContact.getId())).findAny().isPresent()){
                    personContactArrayList.add(personContact);
                }
            }

            for(PersonContact personContact1 : personContactArrayList){
                entityManager.remove(personContact1);
            }
            tx.commit();
        }catch (Exception e){
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
                    return prepareError(result, methodName, personEducations,
                            "no legacyId");
                }
                if (personEducationJson.getCompanyCode() == null || personEducationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no companyCode");
                }
                if (personEducationJson.getSchool() == null || personEducationJson.getSchool().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no school");
                }
                if (personEducationJson.getEducationTypeId() == null || personEducationJson.getEducationTypeId().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no educationTypeId");
                }
                if (personEducationJson.getFaculty() == null || personEducationJson.getFaculty().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no faculty");
                }
                if (personEducationJson.getStartYear() == null || personEducationJson.getStartYear().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no startYear");
                }
                if (personEducationJson.getEndYear() == null || personEducationJson.getEndYear().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no endYear");
                }
                if (personEducationJson.getQualification() == null || personEducationJson.getQualification().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
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
                        return prepareError(result, methodName, personEducationJson,
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
                        return prepareError(result, methodName, personEducationJson,
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
                        return prepareError(result, methodName, personEducationJson,
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
                        return prepareError(result, methodName, personEducationJson,
                                "no base$DicEducationType with legacyId " + personEducationJson.getEducationTypeId()
                                        + " and company legacyId " + personEducationJson.getCompanyCode());
                    }
                    commitContext.addInstanceToCommit(personEducation);
                }
            }
            dataManager.commit(commitContext);
        } catch (Exception e) {
            return prepareError(result, methodName, personEducations, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personEducations);
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
                    return prepareError(result, methodName, personEducations,
                            "no legacyId");
                }
                if (personEducationJson.getCompanyCode() == null || personEducationJson.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
                            "no companyCode");
                }
                if (personEducationJson.getPersonId() == null || personEducationJson.getPersonId().isEmpty()) {
                    return prepareError(result, methodName, personEducations,
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
                    return prepareError(result, methodName, personEducationJson,
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
            return prepareError(result, methodName, personEducations, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, personEducationData);
    }
}
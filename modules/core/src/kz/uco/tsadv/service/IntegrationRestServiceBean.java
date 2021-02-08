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
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.integration.jsonobject.*;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
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
                organizationExt.setOrganizationNameLang3(organizationJson.getOrganizationNameLang3());
                organizationExt.setLegacyId(organizationJson.getLegacyId());
                organizationExt.setStartDate(formatter.parse(organizationJson.getStartDate()));
                organizationExt.setEndDate(formatter.parse(organizationJson.getEndDate()));
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
                job.setStartDate(formatter.parse(jobJson.getStartDate()));
                job.setEndDate(formatter.parse(jobJson.getEndDate()));
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
                positionExt.setStartDate(formatter.parse(positionJson.getStartDate()));
                positionExt.setEndDate(formatter.parse(positionJson.getEndDate()));
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
}
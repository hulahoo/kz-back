package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicOrgType;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.*;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicPositionStatus;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
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
}
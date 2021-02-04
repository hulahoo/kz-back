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
import kz.uco.tsadv.modules.integration.jsonobject.JobDataJson;
import kz.uco.tsadv.modules.integration.jsonobject.JobJson;
import kz.uco.tsadv.modules.integration.jsonobject.OrganizationDataJson;
import kz.uco.tsadv.modules.integration.jsonobject.OrganizationJson;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import org.springframework.stereotype.Service;

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

    @Override
    public BaseResult createOrUpdateOrganization(OrganizationDataJson organizationData) {
        String methodName = "createOrUpdateOrganization";
        ArrayList<OrganizationJson> organizations = new ArrayList<>();
        if (organizationData.getOrganizations() != null) {
            organizations = organizationData.getOrganizations();
        }
        BaseResult result = new BaseResult();
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            List<OrganizationGroupExt> organizationGroupExts = new ArrayList<>();
            for (OrganizationJson organization : organizations) {
                if (organization.getLegacyId() == null || organization.getLegacyId().isEmpty()) {
                    return prepareError(result, methodName, organizations,
                            "no legacyId ");
                }
                if (organization.getCompanyCode() == null || organization.getCompanyCode().isEmpty()) {
                    return prepareError(result, methodName, organizations,
                            "no companyCode");
                }
                OrganizationGroupExt organizationGroupExt = organizationGroupExts.stream().filter(organizationGroupExt1 ->
                        organizationGroupExt1.getCompany().getLegacyId().equals(organization.getCompanyCode())
                                && organizationGroupExt1.getLegacyId().equals(organization.getLegacyId()))
                        .findFirst().orElse(null);
                if (organizationGroupExt == null) {
                    organizationGroupExt = dataManager.load(OrganizationGroupExt.class)
                            .query("select e from base$OrganizationGroupExt e " +
                                    " where e.legacyId = :legacyId and e.company.legacyId = :company")
                            .setParameters(ParamsMap.of("legacyId", organization.getLegacyId(),
                                    "company", organization.getCompanyCode()))
                            .view("organizationGroupExt-for-integration-rest").list().stream().findFirst().orElse(null);
                }
                if (organizationGroupExt == null) {
                    organizationGroupExt = metadata.create(OrganizationGroupExt.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organization.getCompanyCode())
                            .view(View.MINIMAL).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, organizations,
                                "no base$DicCompany with legacyId " + organization.getCompanyCode());
                    }
                    organizationGroupExt.setCompany(company);
                    organizationGroupExt.setLegacyId(organization.getLegacyId());
                    organizationGroupExt.setId(UUID.randomUUID());
                    organizationGroupExt.setList(new ArrayList<>());
                    organizationGroupExt = dataManager.commit(organizationGroupExt);
                    organizationGroupExt = dataManager.reload(organizationGroupExt,
                            "organizationGroupExt-for-integration-rest");
                    organizationGroupExts.add(organizationGroupExt);
                }

                for (OrganizationExt organizationExt : organizationGroupExt.getList()) {
                    entityManager.remove(organizationExt);
                }
                OrganizationExt organizationExt = metadata.create(OrganizationExt.class);
                organizationExt.setOrganizationNameLang1(organization.getOrganizationNameLang1());
                organizationExt.setOrganizationNameLang2(organization.getOrganizationNameLang2());
                organizationExt.setOrganizationNameLang3(organization.getOrganizationNameLang3());

                DicOrgType type = null;
                if (organization.getOrganizationType() != null && !organization.getOrganizationType().isEmpty()) {
                    type = dataManager.load(DicOrgType.class).query("select e from base$DicOrgType e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organization.getOrganizationType())
                            .view(View.MINIMAL).list().stream().findFirst().orElse(null);
                    organizationExt.setType(type);
                }
                DicLocation location = null;
                if (organization.getOrganizationType() != null && !organization.getOrganizationType().isEmpty()) {
                    location = dataManager.load(DicLocation.class).query("select e from base$DicLocation e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", organization.getLocation())
                            .view(View.MINIMAL).list().stream().findFirst().orElse(null);
                    organizationExt.setLocation(location);
                }
                organizationExt.setOrganizationNameLang3(organization.getOrganizationNameLang3());
                organizationExt.setLegacyId(organization.getLegacyId());
                organizationExt.setStartDate(formatter.parse(organization.getStartDate()));
                organizationExt.setEndDate(formatter.parse(organization.getEndDate()));
                organizationExt.setLegacyId(organization.getLegacyId());
                organizationExt.setGroup(organizationGroupExt);
                entityManager.persist(organizationExt);
            }
            tx.commit();
        } catch (Exception e) {
            return prepareError(result, methodName, organizations, e.getMessage() + "\r" +
                    Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.toString())
                            .collect(Collectors.joining("\r")));
        }
        return prepareSuccess(result, methodName, organizations);
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
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
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
                }
                if (jobGroup == null) {
                    jobGroup = metadata.create(JobGroup.class);
                    DicCompany company = dataManager.load(DicCompany.class).query("select e from base_DicCompany e " +
                            " where e.legacyId = :legacyId").parameter("legacyId", jobJson.getCompanyCode())
                            .view(View.MINIMAL).list().stream().findFirst().orElse(null);
                    if (company == null) {
                        return prepareError(result, methodName, jobs,
                                "no base$DicCompany with legacyId " + jobJson.getCompanyCode());
                    }
                    jobGroup.setCompany(company);
                    jobGroup.setLegacyId(jobJson.getLegacyId());
                    jobGroup.setId(UUID.randomUUID());
                    jobGroup.setList(new ArrayList<>());
                    jobGroup = dataManager.commit(jobGroup);
                    jobGroup = dataManager.reload(jobGroup,
                            "jobGroup-for-integration-rest");
                    jobGroups.add(jobGroup);
                }

                for (Job job : jobGroup.getList()) {
                    entityManager.remove(job);
                }
                Job job = metadata.create(Job.class);
                job.setJobNameLang1(jobJson.getJobNameLang1());
                job.setJobNameLang1(jobJson.getJobNameLang1());
                job.setJobNameLang1(jobJson.getJobNameLang1());
                job.setLegacyId(jobJson.getLegacyId());
                job.setStartDate(formatter.parse(jobJson.getStartDate()));
                job.setEndDate(formatter.parse(jobJson.getEndDate()));
                job.setLegacyId(jobJson.getLegacyId());
                job.setGroup(jobGroup);
                entityManager.persist(job);
            }
            tx.commit();
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
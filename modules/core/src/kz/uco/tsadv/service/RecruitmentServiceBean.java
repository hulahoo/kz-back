package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@Service(RecruitmentService.NAME)
public class RecruitmentServiceBean implements RecruitmentService {

    @Inject
    private EmployeeService employeeService;

    @Inject
    private NotificationService notificationService;
    @Inject
    private UniqueNumbersAPI uniqueNumbersAPI;

    @Inject
    private DataManager dataManager;

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private CommonService commonService;

    @Inject
    private Configuration configuration;

    @Inject
    private UserSessionSource userSessionSource;

    protected int languageIndex = 0;


    @Override
    public List<RequisitionSearchCandidateResult> getRequisitionSearchCandidate(UUID requisitionId, RequisitionSearchCandidate requisitionSearchCandidate, String language) {

        try (Transaction transaction = persistence.createTransaction()) {
            languageIndex = languageIndex(language);
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "with params as (select r.id as requisition_id, r.job_group_id, ?1::date as system_date from tsadv_requisition r where r.id = ?2)" +
                            "                       select tp.id              p_id," +
                            "                              tp.group_id        pg_id," +
                            "                              (select count(1) " +
                            "                                 from tsadv_job_request " +
                            "                                where candidate_person_group_id = tp.group_id " +
                            "                                  and delete_ts is null) as click_count," +
                            "                              tp.first_name," +
                            "                              tp.last_name," +
                            "                              tp.middle_name," +
                            "                              p_type.sort_order  pt_so," +
                            "                              p_type.code        pt_code," +
                            "                              p_type.lang_value1 pt_l1," +
                            "                              p_type.lang_value2 pt_l2," +
                            "                              p_type.lang_value3 pt_l3," +
                            "                              p_type.lang_value4 pt_l4," +
                            "                              p_type.lang_value5 pt_l5," +
                            "                              tt.p_match," +
                            "                              pos.position_name_lang" + languageIndex + "," +     //columnChanged TODO
                            "                              org.organization_name_lang" + languageIndex + "," +  //columnChanged TODO
                            "                              tt.is_reserved" +
                            "                            from (select t.pg_id," +
                            "                                         t.p_match," +
                            "                                         case when exists (select 1" +
                            "                                                            from tsadv_job_request jr " +
                            "                                                            join tsadv_requisition r on (r.id = jr.requisition_id) " +
                            "                                                           where jr.delete_ts is null" +
                            "                                                             and jr.candidate_person_group_id = t.pg_id" +
                            "                                                             and r.job_group_id = p.job_group_id" +
                            "                                                             and jr.requisition_id <> p.requisition_id " +
                            "                                                             and jr.is_reserved = true)" +
                            "                                          then true " +
                            "                                          else false " +
                            "                                          end as is_reserved" +
                            "                                    from (select  pg.id as pg_id," +
                            "                                                  avg(case when rcl.level_number <= coalesce(cel.level_number, -1)" +
                            "                                                  then 1" +
                            "                                                  else 0 " +
                            "                                                  end) as p_match" +
                            "                                             from BASE_PERSON_GROUP pg" +
                            "                                       cross join params p" +
                            "                                        left join tsadv_requisition_competence rc on (rc.requisition_id = p.requisition_id)" +
                            "                                        left join tsadv_scale_level rcl on (rcl.id = rc.scale_level_id)" +
                            "                                        left join tsadv_competence_element ce on (ce.person_group_id = pg.id" +
                            "                                                                            and ce.competence_group_id = rc.competence_group_id " +
                            "                                                                            and (ce.id is null or ce.delete_ts is null))" +
                            "                                        left join tsadv_scale_level cel on (cel.id = ce.scale_level_id)" +
                            "                                            where pg.delete_ts is null" +
                            "                                              and rc.delete_ts is null" +
                            "                                         group by pg.id) t" +
                            "                                       cross join params p) tt" +
                            "                           cross join params p" +
                            "                            left join BASE_PERSON tp on (tp.group_id = tt.pg_id) " +
                            "                            left join tsadv_dic_person_type p_type on (p_type.id = tp.type_id) " +
                            "                            left join BASE_ASSIGNMENT a on (a.person_group_id = tp.group_id) " +
                            "                            left join BASE_POSITION pos on (pos.group_id = a.position_group_id) " +
                            "                            left join BASE_ORGANIZATION org on (org.group_id = a.organization_group_id) " +
                            "                                where tp.delete_ts is null " +
                            "                                  and p.system_date between tp.start_date and tp.end_date " +
                            "                                  and p_type.delete_ts is null " +
                            "                                  and (case when p_type.code not in ('CANDIDATE', 'STUDENT')  " +
                            "                                            then (a.delete_ts is null and p.system_date between a.start_date and a.end_date " +
                            "                                              and pos.delete_ts is null and p.system_date between pos.start_date and pos.end_date " +
                            "                                              and org.delete_ts is null and p.system_date between org.start_date and org.end_date) " +
                            "                                            else true " +
                            "                                       end) " +
                            "                                  and tp.group_id not in (select jr.candidate_person_group_id " +
                            "                                                            from tsadv_job_request jr" +
                            "                                                           where jr.requisition_id = p.requisition_id" +
                            "                                                             and jr.delete_ts is null)" +
                            " %s " +
                            " order by p_type.sort_order asc, tt.is_reserved desc, tt.p_match desc",
                    addFilter(requisitionSearchCandidate)));

            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, requisitionId);

            List<Object[]> list = query.getResultList();

            List<RequisitionSearchCandidateResult> resultList = new LinkedList<>();

            if (!list.isEmpty()) {
                for (Object[] row : list) {
                    boolean add = true;
                    Double experience = requisitionSearchCandidate.getExperience();

                    if (experience != null && experience != 0) {
                        Double totalExperience = employeeService.getTotalExperienceDouble(UUID.fromString(String.valueOf(row[1])), CommonUtils.getSystemDate());
                        if (totalExperience < experience) {
                            add = false;
                        }
                    }

                    if (add) resultList.add(mappingSearchCandidateResult(row));
                }
            }
            return resultList;
        }
    }

    private int languageIndex(String language) {
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            int count = 1;
            for (String lang : langs) {
                if (language.equals(lang)) {
                    return count;
                }
                count++;
            }
        }
        return 1;
    }

    @Override
    public Long getCurrentSequenceValue(String sequenceName) {
        return uniqueNumbersAPI.getCurrentNumber(sequenceName);
    }

    protected List<OrganizationHrUser> getRmPersonGroupId(UUID organizationGroupId) {
        List<OrganizationHrUser> list = employeeService.getHrUsers(organizationGroupId, "RECRUITING_MANAGER");
        return list;
    }

    public void checkRequisitionStartDate() {
        try {
            LoadContext<Requisition> loadContext = LoadContext.create(Requisition.class);
            loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Requisition e where e.startDate = :sysDate and e.requisitionStatus = 1")
                    .setParameter("sysDate", new Date()));
            loadContext.setView("recruitmentServiceBeanJava.view");
            List<Requisition> list = dataManager.loadList(loadContext);


            for (Requisition requisition : list) {
                TsadvUser manager;
                TsadvUser recruiter;
                TsadvUser personGroupList;
                List<OrganizationHrUser> rmPersonGroupIdList = getRmPersonGroupId(requisition.getOrganizationGroup().getId());
                if (requisition.getManagerPersonGroup() != null) {
                    manager = getUserExt(requisition.getManagerPersonGroup().getId());
                    sendMessageStartDate(manager, requisition);
                }
                if (requisition.getRecruiterPersonGroup() != null) {
                    recruiter = getUserExt(requisition.getRecruiterPersonGroup().getId());
                    sendMessageStartDate(recruiter, requisition);
                }
                for (OrganizationHrUser ohu : rmPersonGroupIdList) {
                    if (ohu.getOrganizationGroup() != null) {
                        personGroupList = ohu.getUser();
                        sendMessageStartDate(personGroupList, requisition);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void checkFinalCollectDate() {
        try {
            List<Requisition> requisitionList = commonService.getEntities(Requisition.class,
                    "select e from tsadv$Requisition e " +
                            "left join e.recruiterPersonGroup rp " +
                            "left join rp.list p on :systemDate " +
                            "between p.startDate and p.endDate " +
                            "where e.requisitionStatus<>6 " +
                            "and e.finalCollectDate<:systemDate",
                    ParamsMap.of("systemDate", CommonUtils.getSystemDate()),
                    "requisition.view");
            CommitContext commitContext = new CommitContext();
            requisitionList.forEach(requisition -> {
                requisition.setRequisitionStatus(RequisitionStatus.FINISH_COLLECT);
                commitContext.addInstanceToCommit(requisition);
            });
            dataManager.commit(commitContext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean getIsChangeStatus(Requisition requisition, String userLogin) {
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        boolean editWithoutChangeStatusRm = recruitmentConfig.getEditWithoutChangeStatusRm();
        boolean editWithoutChangeStatusR = recruitmentConfig.getEditWithoutChangeStatusR();
        boolean editWithoutChangeStatusM = recruitmentConfig.getEditWithoutChangeStatusM();

        boolean isRm = employeeService.getHrUsers(requisition.getOrganizationGroup().getId(), "RECRUITING_MANAGER").stream()
                .filter(organizationHrUser -> organizationHrUser.getUser().getLogin().equals(userLogin))
                .count() > 0;

        boolean isR = employeeService.getHrUsers(requisition.getOrganizationGroup().getId(), "RECRUITING_SPECIALIST").stream()
                .filter(organizationHrUser -> organizationHrUser.getUser().getLogin().equals(userLogin))
                .count() > 0;

        TsadvUser manager = employeeService.getUserExtByPersonGroupId(requisition.getManagerPersonGroup().getId());
        boolean isM = manager != null ? manager.getLogin().equals(userLogin) : false;

        boolean changeStatus =
                !(editWithoutChangeStatusRm && isRm ||
                        editWithoutChangeStatusR && isR ||
                        editWithoutChangeStatusM && isM);

        return changeStatus;
    }

    @Override
    public void checkRequisitionTimeout() {
        try {
            LoadContext<Requisition> loadContext = LoadContext.create(Requisition.class);

            loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Requisition e where e.endDate <= :sysDate and e.requisitionStatus <> 3")
                    .setParameter("sysDate", new Date()));
            //LoadContext.Query query = LoadContext.createQuery("select e from tsadv$Requisition e where e.endDate = :sysDate");
            loadContext.setView("recruitmentServiceBeanJava.view");
            //query.setParameter("sysDate", new Date());
            List<Requisition> list = dataManager.loadList(loadContext);

            if (list != null && !list.isEmpty()) {
                CommitContext commitContext = new CommitContext();

                for (Requisition requisition : list) {
                    requisition.setRequisitionStatus(RequisitionStatus.CLOSED);
                    commitContext.addInstanceToCommit(requisition);

                    TsadvUser manager;
                    TsadvUser recruiter;
                    if (requisition.getRecruiterPersonGroup() != null) {
                        recruiter = getUserExt(requisition.getRecruiterPersonGroup().getId());
                        sendMessage(recruiter, requisition);
                    }
                    if (requisition.getManagerPersonGroup() != null) {
                        manager = getUserExt(requisition.getManagerPersonGroup().getId());
                        sendMessage(manager, requisition);
                    }
                    TsadvUser candidate;
                    for (JobRequest jobRequest : requisition.getJobRequests()) {
                        if (jobRequest.getRequestStatus() != JobRequestStatus.HIRED && jobRequest.getRequestStatus() != JobRequestStatus.REJECTED) {
                            sendNotificationForCandidate(jobRequest.getCandidatePersonGroup().getId(),
                                    null,
                                    "requisition.notify.candidate.close", requisition);
                        }
                    }
                }
                dataManager.commit(commitContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendNotificationForCandidate(UUID candidateId, String login, String templateCode, Requisition requisition) {
        boolean success = false;
        String errorMessage = "";

        try {
            TsadvUser userExt = candidateId != null ? getUserExt(candidateId) : getUserExtByLogin(login);

            if (userExt != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                Case personNameEn = getCasePersonName(userExt, "en", "Nominative");
                Case personNameKz = getCasePersonName(userExt, "kz", "Атау септік");
                Case personNameRu = getCasePersonName(userExt, "ru", "Именительный");

                Case jobNameEn = getCaseJobName(requisition, "en", "Nominative");
                Case jobNameKz = getCaseJobName(requisition, "kz", "Атау септік");
                Case jobNameRu = getCaseJobName(requisition, "ru", "Именительный");

                if (jobNameEn != null) {
                    paramsMap.put("positionNameEn", jobNameEn.getLongName() == null ? requisition.getJobGroup().getJob().getJobName() : jobNameEn.getLongName());
                } else {
                    paramsMap.put("positionNameEn", requisition.getJobGroup().getJob() == null ? "" : requisition.getJobGroup().getJob().getJobName());
                }
                if (jobNameKz != null) {
                    paramsMap.put("positionNameKz", jobNameKz.getLongName() == null ? requisition.getJobGroup().getJob().getJobName() : jobNameKz.getLongName());
                } else {
                    paramsMap.put("positionNameKz", requisition.getJobGroup().getJob() == null ? "" : requisition.getJobGroup().getJob().getJobName());
                }
                if (jobNameRu != null) {
                    paramsMap.put("positionNameRu", jobNameRu.getLongName() == null ? requisition.getJobGroup().getJob().getJobName() : jobNameRu.getLongName());
                } else {
                    paramsMap.put("positionNameRu", requisition.getJobGroup().getJob() == null ? "" : requisition.getJobGroup().getJob().getJobName());
                }

                //TODO: personGroup need to test
                PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(userExt.getId());
                paramsMap.put("personFullNameEn", getLongValueOrFullName(personNameEn, personGroup));
                paramsMap.put("personFullNameKz", getLongValueOrFullName(personNameKz, personGroup));
                paramsMap.put("personFullNameRu", getLongValueOrFullName(personNameRu, personGroup));
                paramsMap.put("code", requisition.getCode());
                //paramsMap.put("personFullName", userExt.getPersonGroup() == null ? "" : userExt.getPersonGroup().getPerson().getFullName()); //my params
                notificationService.sendParametrizedNotification(
                        templateCode,
                        userExt,
                        paramsMap);
            } else {
                errorMessage = "TsadvUser is null!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getLongValueOrFullName(Case personNameEn, PersonGroupExt personGroup) {
        String personFullNameEn = "";
        if (personNameEn != null) {
            if (personNameEn.getLongName() == null) {
                if (personGroup != null) {
                    personFullNameEn = personGroup.getPerson().getFullName();
                }
            } else {
                personFullNameEn = personNameEn.getLongName();
            }
        }else {
            if (personGroup != null) {
                personFullNameEn = personGroup.getPerson().getFullName();
            }
        }
        return personFullNameEn;
    }

    protected Case getCasePersonName(TsadvUser userExt, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personGroupId", employeeService.getPersonGroupByUserId(userExt.getId()).getId()); //TODO: personGroup need to test
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);
        Case personName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join base$PersonExt t on t.group.id = c.personGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between t.startDate and t.endDate " +
                        "and c.deleteTs is null " +
                        "and t.group.id = :personGroupId",
                paramMap,
                "caseJobName");
        return personName;
    }

    protected Case getCaseJobName(Requisition requisition, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobGroupId", requisition.getJobGroup().getId());
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);

        Case jobName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join tsadv$Job j on j.group.id = c.jobGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between j.startDate and j.endDate " +
                        "and c.jobGroup.id = :jobGroupId " +
                        "and c.deleteTs is null",
                paramMap,
                "caseJobName");
        return jobName;
    }

    public void checkFinalCollecDate() {
        try {
            LoadContext<Requisition> loadContext = LoadContext.create(Requisition.class);
            loadContext.setQuery(LoadContext.createQuery("Select e from tsadv$Requisition e where e.finalCollectDate =:sysDate")
                    .setParameter("sysDate", new Date()));
            loadContext.setView("recruitmentServiceBeanJava.view");
            List<Requisition> requisitionList = dataManager.loadList(loadContext);

            if (requisitionList != null && !requisitionList.isEmpty()) {
                for (Requisition requisition : requisitionList) {
                    if (requisition.getManagerPersonGroup() != null) {
                        TsadvUser manager;
                        manager = getUserExt(requisition.getManagerPersonGroup().getId());
                        sendMessageFinalCollectDate(manager, requisition);
                    }
                    if (requisition.getRecruiterPersonGroup() != null) {
                        TsadvUser recruiter;
                        recruiter = getUserExt(requisition.getRecruiterPersonGroup().getId());
                        sendMessageFinalCollectDate(recruiter, requisition);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    private void sendMessage(TsadvUser userExt, Requisition requisition) {
        try {
            if (userExt != null && requisition.getCode() != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("code", requisition.getCode());
                paramsMap.put("personFullName", employeeService.getPersonGroupByUserId(userExt.getId()).getPerson().getFullName()); //TODO: personGroup need to test
                paramsMap.put("requisitionNameRu", requisition.getNameForSiteLang1());
                paramsMap.put("requisitionNameEn", requisition.getNameForSiteLang2());
                paramsMap.put("requisitionNameKz", requisition.getNameForSiteLang3());

                notificationService.sendParametrizedNotification(
                        "requisition.notify.closeAuto",
                        userExt,
                        paramsMap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessageFinalCollectDate(TsadvUser userExt, Requisition requisition) {
        try {
            if (userExt != null && requisition.getCode() != null && requisition.getFinalCollectDate() != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("code", requisition.getCode());
                paramsMap.put("personFullName", employeeService.getPersonGroupByUserId(userExt.getId()).getPerson().getFullName()); //TODO: personGroup need to test
                paramsMap.put("requisitionNameRu", requisition.getNameForSiteLang1());
                paramsMap.put("requisitionNameKz", requisition.getNameForSiteLang2());
                paramsMap.put("requisitionNameEn", requisition.getNameForSiteLang3());

                notificationService.sendParametrizedNotification(
                        "requisition.notify.finalCollectDate",
                        userExt,
                        paramsMap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessageStartDate(TsadvUser userExt, Requisition requisition) {
        try {
            if (userExt != null && requisition.getCode() != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("code", requisition.getCode());
                paramsMap.put("personFullName", employeeService.getPersonGroupByUserId(userExt.getId()).getPerson().getFullName()); //TODO: personGroup need to test
                paramsMap.put("requisitionNameRu", requisition.getNameForSiteLang1());
                paramsMap.put("requisitionNameKz", requisition.getNameForSiteLang2());
                paramsMap.put("requisitionNameEn", requisition.getNameForSiteLang3());

                notificationService.sendParametrizedNotification("requisition.notify.startDate",
                        userExt,
                        paramsMap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    protected TsadvUser getUserExt(UUID personGroupId) {
        LoadContext<TsadvUser> loadContext = LoadContext.create(TsadvUser.class);
//        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$UserExt e where e.personGroup.id = :pgId")
//                .setParameter("pgId", personGroupId));
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    protected TsadvUser getUserExtByLogin(String login) {
        LoadContext<TsadvUser> loadContext = LoadContext.create(TsadvUser.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e where e.login = :login");
        query.setParameter("login", login);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    private boolean checkRequisitionHistory(Requisition requisition) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<RequisitionHistory> query = em.createQuery(
                    "select e from tsadv$RequisitionHistory e " +
                            "where e.requisition.id = ?1 " +
                            "order by e.createTs desc", RequisitionHistory.class);
            query.setParameter(1, requisition.getId());
            query.setViewName("requisitionHistory.browse");

            RequisitionHistory requisitionHistory = query.getFirstResult();

            if (requisitionHistory != null) {
                if (requisitionHistory.getCreateTs().after(DateUtils.addDays(CommonUtils.getSystemDate(), -getRequisitionDaysCountDown()))) {
                    return true;
                }
            }
            return false;
        }
    }

    private int getRequisitionDaysCountDown() {
        LoadContext<GlobalValue> loadContext = LoadContext.create(GlobalValue.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$GlobalValue e " +
                        "where e.name = :name " +
                        "and :sysDate between e.startDate and e.endDate")
                .setParameter("name", "requisitionDaysCountDown")
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("globalValue.edit");
        GlobalValue globalValue = dataManager.load(loadContext);

        int days = 7;

        if (globalValue != null) {
            try {
                days = Integer.parseInt(globalValue.getValue());
            } catch (Exception ex) {
                //ignore this exceptions
            }
        }
        return days;
    }

    private void fillPersonContacts(PersonGroupExt personGroup) {
        LoadContext<PersonContact> loadContext = LoadContext.create(PersonContact.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$PersonContact e " +
                        "where e.personGroup.id = :pgId order by e.type")
                .setParameter("pgId", personGroup.getId()))
                .setView("personContact.edit");

        List<PersonContact> resultList = dataManager.loadList(loadContext);
        personGroup.setPersonContacts(new ArrayList<>());

        if (resultList != null && !resultList.isEmpty()) {
            personGroup.getPersonContacts().addAll(resultList);
        }
    }

    private String addFilter(RequisitionSearchCandidate searchCandidate) {
        StringBuilder filter = new StringBuilder("");
        boolean added = false;
        if (BooleanUtils.isTrue(searchCandidate.getReserve())) {
            filter.append(" p_type.code = 'RESERVE' ");
            added = true;
        }

        if (BooleanUtils.isTrue(searchCandidate.getEmployee())) {
            if (added) filter.append(" or ");
            filter.append(" p_type.code = 'EMPLOYEE' ");
            added = true;
        }

        if (BooleanUtils.isTrue(searchCandidate.getStudent())) {
            if (added) filter.append(" or ");
            filter.append(" p_type.code = 'STUDENT' ");
            added = true;
        }

        if (BooleanUtils.isTrue(searchCandidate.getExternalCandidate())) {
            if (added) filter.append(" or ");
            filter.append(" p_type.code = 'CANDIDATE' ");
            added = true;
        }

        if (BooleanUtils.isTrue(searchCandidate.getReservedCandidate())) {
            if (added) filter.append(" or ");
            filter.append(" (p_type.code = 'CANDIDATE' and tt.is_reserved = true) ");
            added = true;
        }

        String personFilter = null;

        if (added) {
            personFilter = "and (" + filter.toString() + ")";
        }

        if (searchCandidate.getLevelEducation() != null) {
            personFilter += String.format(" and tp.group_id in (select person_group_id from TSADV_PERSON_EDUCATION where delete_ts is null and LEVEL_ID = '%s') ",
                    searchCandidate.getLevelEducation().getId());
        }

        if (BooleanUtils.isTrue(searchCandidate.getReadRelocation())) {
            personFilter += " and tp.group_id in (select person_group_id from TSADV_RE_LOCATION where delete_ts is null) ";
        }

        if (personFilter != null) {
            return personFilter;
        }

        return "";
    }

    private RequisitionSearchCandidateResult mappingSearchCandidateResult(Object[] row) {
        RequisitionSearchCandidateResult model = metadata.create(RequisitionSearchCandidateResult.class);

        PersonExt person = metadata.create(PersonExt.class);
        PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
        personGroup.setId((UUID) row[1]);

        person.setId((UUID) row[0]);
        person.setGroup(personGroup);
        person.setFirstName((String) row[3]);
        person.setLastName((String) row[4]);
        person.setMiddleName((String) row[5]);
        model.setPerson(person);

        model.setClickCount((long) row[2]);

        model.setPersonTypeOrder((int) row[6]);

        DicPersonType dicPersonType = metadata.create(DicPersonType.class);
        dicPersonType.setCode((String) row[7]);
        dicPersonType.setLangValue1((String) row[8]);
        dicPersonType.setLangValue2((String) row[9]);
        dicPersonType.setLangValue3((String) row[10]);
        dicPersonType.setLangValue4((String) row[11]);
        dicPersonType.setLangValue5((String) row[12]);
        person.setType(dicPersonType);

        model.setMatch((long) (((BigDecimal) row[13]).doubleValue() * 100));

        model.setPositionName((String) row[14]);
        model.setOrganizationName((String) row[15]);

        if (personGroup.getId() != null) {
            fillPersonContacts(personGroup);
        }

        model.setIsReserved((Boolean) row[16]);

        return model;
    }

    private RequisitionCount mappingRequisitionCount(Object[] row) {
        RequisitionCount requisitionCount = metadata.create(RequisitionCount.class);

        PersonExt person = metadata.create(PersonExt.class);
        PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
        personGroup.setId((UUID) row[0]);
        personGroup.setPerson(person);

        person.setId((UUID) row[1]);
        person.setFirstName((String) row[2]);
        person.setLastName((String) row[3]);
        person.setMiddleName((String) row[4]);
        person.setGroup(personGroup);

        requisitionCount.setPersonGroup(personGroup);
        requisitionCount.setCountRequisition((long) row[5]);
        requisitionCount.setCountJobRequest((long) row[6]);
        return requisitionCount;
    }

}
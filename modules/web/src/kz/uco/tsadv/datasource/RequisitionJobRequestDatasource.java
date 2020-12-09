package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import com.haulmont.cuba.gui.logging.UIPerformanceLogger;
import com.haulmont.cuba.security.entity.EntityOp;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
public class RequisitionJobRequestDatasource extends CustomCollectionDatasource<JobRequest, UUID> {
    private static final Logger log = LoggerFactory.getLogger(RequisitionJobRequestDatasource.class);

    private LoadContext context;

    @Override
    protected void loadData(Map<String, Object> params) {
        Security security = AppBeans.get(Security.NAME);
        if (!security.isEntityOpPermitted(metaClass, EntityOp.READ)) {
            return;
        }

        String tag = getLoggingTag("CDS");
        StopWatch sw = new Log4JStopWatch(tag, org.apache.log4j.Logger.getLogger(UIPerformanceLogger.class));

        if (params != null){
            if (params.size() > 0){
                if (needLoading()) {
                    context = beforeLoadData(params);
                    if (context == null) {
                        return;
                    }
                    try {
                        final Collection<JobRequest> entities = getEntities(params);

                        afterLoadData(params, context, entities);
                    } catch (Throwable e) {
                        dataLoadError = e;
                    }
                }
            }
        }

        sw.stop();
    }

    @Override
    protected Collection<JobRequest> getEntities(Map<String, Object> params) {
        DataManager dataManager = AppBeans.get(DataManager.NAME);
        CommonService commonService = AppBeans.get(CommonService.NAME);

        Collection<JobRequest> jobRequests = dataManager.loadList(context);
        Map<Integer, Object> queryParams = new HashMap<>();
        StringBuilder list = new StringBuilder(" jr.id in (");
        int size=list.length();
        for (JobRequest jobRequest : jobRequests) {
            list.append("'").append(jobRequest.getId().toString()).append("',");
        }
        if (list.toString().length() > size) {
            list.deleteCharAt(list.length() - 1).append(")");
        } else {
            list.delete(0, list.length() );
            list.append(" 1<>1 ");
        }
        queryParams.put(1, list.toString());

        List<Object[]> competenceMatchList = commonService.emNativeQueryResultList("select " +
                "                avg(case when rcl.level_number <= coalesce(cel.level_number, -1) then 1 else 0 end) as match_avg, " +
                "                sum(case when rcl.level_number <= coalesce(cel.level_number, -1) then 1 else 0 end) match_count," +
                "                count(1) as totsadv_count, " +
                "                jr.id as jr_id" +
                "           from tsadv_job_request jr" +
                "           join tsadv_requisition_competence rc on (rc.requisition_id = jr.requisition_id)" +
                "           join tsadv_scale_level rcl on (rcl.id = rc.scale_level_id)" +
                "      left join tsadv_competence_element ce on (ce.person_group_id = jr.candidate_person_group_id " +
                "            and ce.competence_group_id = rc.competence_group_id" +
                "            and ce.delete_ts is null)" +
                "      left join tsadv_scale_level cel on (cel.id = ce.scale_level_id)           " +
                "          where " + list.toString() +
                "            and jr.delete_ts is null " +
                "            and rc.delete_ts is null " +
                "       group by jr.id", new HashMap<>());

        List<Object[]> competenceQuestionnaireResultList = commonService.emNativeQueryResultList("" +
                "                     select t.questionnaire_id, sum(score) as score , max(cast(t.idjr as text)) as jr_id" +
                "                       from (select jr.id as idjr," +
                "                                    iq.questionnaire_id, " +
                "                                    q.answer_type, " +
                "                                    ia.interview_question_id," +
                "                                    sum(case when ia.boolean_answer = true then ia.weight else 0 end) as score" +
                "                               from tsadv_job_request jr" +
                "                               join tsadv_interview i on (i.job_request_id = jr.id and i.interview_status = 40)" +
                "                               join tsadv_interview_questionnaire iq on (iq.interview_id = i.id)" +
                "                               join tsadv_interview_question iqq on (iqq.interview_questionnaire_id = iq.id)" +
                "                               join tsadv_rc_question q on (q.id = iqq.question_id and q.answer_type in ('SINGLE', 'MULTI'))" +
                "                               join tsadv_interview_answer ia on (ia.interview_question_id = iqq.id)" +
                "                               join tsadv_rc_questionnaire rq on (rq.id = iq.questionnaire_id)" +
                "                               join tsadv_dic_rc_questionnaire_category rqc on (rqc.id = rq.category_id)" +
                "                              where " + list.toString() +
                "                               and rqc.code = 'COMPETENCE'   " +
                "                               and jr.delete_ts is null                            " +
                "                               and i.delete_ts is null" +
                "                               and iq.delete_ts is null" +
                "                               and iqq.delete_ts is null" +
                "                               and ia.delete_ts is null" +
                "                               and rq.delete_ts is null" +
                "                               and rqc.delete_ts is null" +
                "                          group by jr.id, iq.questionnaire_id, q.answer_type, ia.interview_question_id) t" +
                "                    group by t.questionnaire_id", new HashMap<>());

        List<Object[]> testResultsQuestionnaireResultList = commonService.emNativeQueryResultList(
                "                          select  iq.questionnaire_id, " +
                        "                                    q.id as question_id, " +
                        "                                    coalesce(sum(case when q.answer_type = 'NUM' then ia.number_answer when ia.boolean_answer = true then ia.weight else 0 end),0) as score, " +
                        "                                    max(cast(jr.id as text)) as jr_id " +
                        "                               from tsadv_job_request jr" +
                        "                               join tsadv_interview i on (i.job_request_id = jr.id and i.interview_status = 40)" +
                        "                               join tsadv_interview_questionnaire iq on (iq.interview_id = i.id)" +
                        "                               join tsadv_interview_question iqq on (iqq.interview_questionnaire_id = iq.id)" +
                        "                               join tsadv_rc_question q on (q.id = iqq.question_id and q.answer_type in ('SINGLE', 'MULTI', 'NUM'))" +
                        "                               join tsadv_interview_answer ia on (ia.interview_question_id = iqq.id)" +
                        "                               join tsadv_rc_questionnaire rq on (rq.id = iq.questionnaire_id)" +
                        "                               join tsadv_dic_rc_questionnaire_category rqc on (rqc.id = rq.category_id)" +
                        "                              where " + list.toString() +
                        "                                and rqc.code = 'TEST_RESULTS'   " +
                        "                                and jr.delete_ts is null " +
                        "                                and i.delete_ts is null" +
                        "                                and iq.delete_ts is null" +
                        "                                and iqq.delete_ts is null" +
                        "                                and ia.delete_ts is null" +
                        "                                and rq.delete_ts is null" +
                        "                                and rqc.delete_ts is null" +
                        "                           group by iq.questionnaire_id, q.id",
                new HashMap<>());

        List<Object[]> interviewList = commonService.emNativeQueryResultList("select count(rhs.id) as total, count(i.id) as passed, jr.id as jr_id " +
                "      from tsadv_job_request jr " +
                " left join tsadv_requisition_hiring_step rhs on (rhs.requisition_id = jr.requisition_id)" +
                " left join tsadv_interview i on (i.job_request_id = jr.id and i.requisition_hiring_step_id = rhs.id and i.interview_status = 40)" +
                "     where " + list.toString() +
                "       and jr.delete_ts is null" +
                "       and i.delete_ts is null" +
                "       and rhs.delete_ts is null" +
                "  group by jr.id", new HashMap<>());

        for (JobRequest jobRequest : jobRequests) {

            if (competenceMatchList != null && competenceMatchList.size() > 0) {
                for (Object[] row : competenceMatchList) {
                    if (row[2] != null && jobRequest.getId().toString().equals(row[2].toString())) {
                        jobRequest.setCompetenceMatchPercent(row[0] == null ? null : ((BigDecimal) row[0]).doubleValue() * 100);
                        jobRequest.setCompetenceMatchString((row[1] == null ? "0" : ((Long) row[1]).toString()) + "/" + (row[2] == null ? "0" : ((Long) row[2]).toString()));
                    }
                }
            }

            /*List<Object[]> questionnaireMatchList = commonService.emNativeQueryResultList("select sum(weight * score) / sum(weight) as match, 1 as dummy " +
                    "       from (select t.weight, sum(score) / sum(max_score) as score " +
                    "       from (select jr.id, rq.weight, q.answer_type, ia.interview_question_id , sum(case when ia.boolean_answer = true then ia.weight else 0 end) as score, case when q.answer_type = 'SINGLE' then MAX(ia.weight)  when q.answer_type = 'MULTI' then SUM(case when ia.weight > 0 then ia.weight else 0 end)  else 1 end as max_score" +
                    "           from tsadv_job_request jr" +
                    "           join tsadv_requisition_questionnaire rq on (rq.requisition_id = jr.requisition_id)" +
                    "           join tsadv_interview i on (i.job_request_id = jr.id and i.interview_status = 40)" +
                    "           join tsadv_interview_questionnaire iq on (iq.interview_id = i.id and iq.questionnaire_id = rq.questionnaire_id)" +
                    "           join tsadv_interview_question iqq on (iqq.interview_questionnaire_id = iq.id)" +
                    "           join tsadv_rc_question q on (q.id = iqq.question_id and q.answer_type in ('SINGLE', 'MULTI'))" +
                    "           join tsadv_interview_answer ia on (ia.interview_question_id = iqq.id)" +
                    "          where jr.id = ?1 " +
                    "       and jr.delete_ts is null " +
                    "       and rq.delete_ts is null  " +
                    "       and i.delete_ts is null" +
                    "       and iq.delete_ts is null" +
                    "       and iqq.delete_ts is null" +
                    "       and ia.delete_ts is null" +
                    "       group by jr.id, rq.weight, q.answer_type, ia.interview_question_id) t" +
                    "       group by t.weight) tt", queryParams);

            if (questionnaireMatchList != null && questionnaireMatchList.size() > 0) {
                Object[] row = questionnaireMatchList.get(0);
                jobRequest.setQuestionnaireMatchPercent(row[0] == null ? null : ((Double) row[0]) * 100);
            }*/

            if (competenceQuestionnaireResultList != null && competenceQuestionnaireResultList.size() > 0) {
                jobRequest.setCompetenceQuestionnaireTotals(competenceQuestionnaireResultList
                        .stream()
                        .filter(objects1 -> objects1[2].toString().equals(jobRequest.getId().toString()))
                        .collect(Collectors.toMap(row -> (UUID) row[0], row -> ((Double) row[1]))));
            }



            if (testResultsQuestionnaireResultList != null && testResultsQuestionnaireResultList.size() > 0) {
                jobRequest.setTestResultsQuestionnaireQuestions(testResultsQuestionnaireResultList
                        .stream()
                        .filter(objects1 -> objects1[3].toString().equals(jobRequest.getId().toString()))
                        .collect(Collectors.groupingBy(row -> (UUID) row[0], Collectors.toMap(row -> (UUID) row[1], row -> (Double) row[2]))));

            }


            if (interviewList != null && interviewList.size() > 0) {
                for (Object[] row : interviewList) {
                    if (row[2].toString().equals(jobRequest.getId().toString())) {
                        jobRequest.setTotalInterviews(row[0] == null ? 0 : ((Long) row[0]).intValue());
                        jobRequest.setPassedInterviews(row[1] == null ? 0 : ((Long) row[1]).intValue());
                    }
                }
            }
        }
        return jobRequests;
    }
}

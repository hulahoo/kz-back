package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author veronika.buksha
 */
public class CandidateJobRequestDatasource extends CustomCollectionDatasource<JobRequest, UUID> {
    @Override
    protected Collection<JobRequest> getEntities(Map<String, Object> params) {
        DataManager dataManager = AppBeans.get(DataManager.NAME);
        CommonService commonService = AppBeans.get(CommonService.NAME);

        Collection<JobRequest> jobRequests = dataManager.loadList(getCompiledLoadContext());

        for (JobRequest jobRequest : jobRequests) {
            Map<Integer, Object> queryParams = new HashMap<>();
            queryParams.put(1, jobRequest.getId());

            List<Object[]> competenceMatchList = commonService.emNativeQueryResultList("select " +
                    "                avg(case when rcl.level_number <= coalesce(cel.level_number, -1) then 1 else 0 end) as match_avg, " +
                    "                sum(case when rcl.level_number <= coalesce(cel.level_number, -1) then 1 else 0 end) match_count," +
                    "                count(1) as totsadv_count" +
                    "           from tsadv_job_request jr" +
                    "           join tsadv_requisition_competence rc on (rc.requisition_id = jr.requisition_id)" +
                    "           join tsadv_scale_level rcl on (rcl.id = rc.scale_level_id)" +
                    "      left join tsadv_competence_element ce on (ce.person_group_id = jr.candidate_person_group_id " +
                    "            and ce.competence_group_id = rc.competence_group_id" +
                    "            and ce.delete_ts is null)" +
                    "      left join tsadv_scale_level cel on (cel.id = ce.scale_level_id)           " +
                    "          where jr.delete_ts is null " +
                    "            and rc.delete_ts is null " +
                    "            and jr.id = ?1 " +
                    "       group by jr.id", queryParams);
            if (competenceMatchList != null && competenceMatchList.size() > 0) {
                Object[] row = competenceMatchList.get(0);
                jobRequest.setCompetenceMatchPercent(row[0] == null ? null : ((BigDecimal) row[0]).doubleValue() * 100);
                jobRequest.setCompetenceMatchString((row[1] == null ? "0" : ((Long) row[1]).toString()) + "/" + (row[2] == null ? "0" : ((Long) row[2]).toString()));
            }

            List<Object[]> questionnaireMatchList = commonService.emNativeQueryResultList("select sum(weight * score) / sum(weight) as match, 1 as dummy " +
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
            }

            List<Object[]> interviewList = commonService.emNativeQueryResultList("select count(rhs.id) as total, count(i.id) as passed" +
                    "      from tsadv_job_request jr " +
                    " left join tsadv_requisition_hiring_step rhs on (rhs.requisition_id = jr.requisition_id)" +
                    " left join tsadv_interview i on (i.job_request_id = jr.id and i.requisition_hiring_step_id = rhs.id and i.interview_status = 40)" +
                    "     where jr.id = ?1" +
                    "       and jr.delete_ts is null" +
                    "       and i.delete_ts is null" +
                    "       and rhs.delete_ts is null" +
                    "  group by jr.id", queryParams);

            if (interviewList != null && interviewList.size() > 0) {
                Object[] row = interviewList.get(0);
                jobRequest.setTotalInterviews(row[0] == null ? 0 : ((Long) row[0]).intValue());
                jobRequest.setPassedInterviews(row[1] == null ? 0 : ((Long) row[1]).intValue());
            }
        }
        return jobRequests;
    }
}

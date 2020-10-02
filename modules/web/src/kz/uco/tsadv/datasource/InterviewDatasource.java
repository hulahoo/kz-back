package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author veronika.buksha
 */
public class InterviewDatasource extends CustomCollectionDatasource<Interview, UUID> {
    private static final Logger log = LoggerFactory.getLogger(InterviewDatasource.class);

    @Override
    protected Collection<Interview> getEntities(Map<String, Object> params) {
        DataManager dataManager = AppBeans.get(DataManager.NAME);
        CommonService commonService = AppBeans.get(CommonService.NAME);

        Collection<Interview> interviews = getCompiledLoadContext().getQuery() == null ?
                new ArrayList<>() :
                dataManager.loadList(getCompiledLoadContext());

        for (Interview interview : interviews) {
            Map<Integer, Object> queryParams = new HashMap<>();
            queryParams.put(1, interview.getId());
            List<Object[]> questionnaireMatchList = commonService.emNativeQueryResultList("select sum(weight * score) / sum(weight) as match, 1 as dummy " +
                    "                           from (select t.weight, sum(score) / sum(max_score) as score " +
                    "                           from (select i.id, rq.weight, q.answer_type, ia.interview_question_id , sum(case when ia.boolean_answer = true then ia.weight else 0 end) as score, case when q.answer_type = 'SINGLE' then MAX(ia.weight)  when q.answer_type = 'MULTI' then SUM(case when ia.weight > 0 then ia.weight else 0 end)  else 1 end as max_score" +
                    "                               from tsadv_interview i" +
                    "                               join tsadv_job_request jr on (jr.id = i.job_request_id)" +
                    "                               join tsadv_requisition_questionnaire rq on (rq.requisition_id = jr.requisition_id)" +
                    "                               join tsadv_interview_questionnaire iq on (iq.interview_id = i.id and iq.questionnaire_id = rq.questionnaire_id)" +
                    "                               join tsadv_interview_question iqq on (iqq.interview_questionnaire_id = iq.id)" +
                    "                               join tsadv_rc_question q on (q.id = iqq.question_id and q.answer_type in ('SINGLE', 'MULTI'))" +
                    "                               join tsadv_interview_answer ia on (ia.interview_question_id = iqq.id)" +
                    "                              where i.id = ?1 " +
                    "                           and i.interview_status = 40 " +
                    "                           and jr.delete_ts is null " +
                    "                           and rq.delete_ts is null  " +
                    "                           and i.delete_ts is null" +
                    "                           and iq.delete_ts is null" +
                    "                           and iqq.delete_ts is null" +
                    "                           and ia.delete_ts is null" +
                    "                           group by i.id, rq.weight, q.answer_type, ia.interview_question_id) t" +
                    "                           group by t.weight) tt", queryParams);

            if (questionnaireMatchList != null && questionnaireMatchList.size() > 0) {
                Object[] row = questionnaireMatchList.get(0);
                interview.setQuestionnaireMatchPercent(row[0] == null ? null : ((Double) row[0]) * 100);
            }
        }
        return interviews;
    }
}

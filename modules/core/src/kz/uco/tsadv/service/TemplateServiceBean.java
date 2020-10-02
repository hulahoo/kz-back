package kz.uco.tsadv.service;

import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Case;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.base.service.common.CommonService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service(TemplateService.NAME)
public class TemplateServiceBean implements TemplateService {
    @Inject
    private CommonService commonService;

    public String getPersonFullName(Case personName, PersonGroupExt personGroup) {
        String personFullName = "";
        if (personName != null) {
            if (personName.getLongName() == null) {
                if (personGroup != null) {
                    personFullName = personGroup.getPerson().getFullName();
                }
                if (personName.getShortName() != null){
                    personFullName = personName.getShortName();
                }
            } else {
                personFullName = personName.getLongName();
            }
        } else {
            if (personGroup != null) {
                personFullName = personGroup.getPerson().getFullName();
            }
        }
        return personFullName;
    }

    public Case getCasePersonName(UUID personGroupId, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personGroupId", personGroupId);
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

    public Case getCaseJobName(Interview interview, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobGroupId", interview.getJobRequest().getRequisition().getJobGroup());
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

    public String getJobName(Case jobName, Interview interview){
        String name = "";
        if (jobName!=null) {
            if (jobName.getLongName() != null) {
                name = jobName.getLongName();
            } else {
                if (jobName.getShortName() != null){
                    name = jobName.getShortName();
                }else {
                    name = interview.getJobRequest().getRequisition().getJobGroup().getJob().getJobName();
                }
            }
        }else {
            if (interview.getJobRequest().getRequisition().getJobGroup() != null) {
                name = interview.getJobRequest().getRequisition().getJobGroup().getJob().getJobName();
            }else {
                name = "";
            }
        }
        return name;
    }
}
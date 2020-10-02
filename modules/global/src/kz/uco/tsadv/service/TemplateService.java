package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Case;
import kz.uco.tsadv.modules.recruitment.model.Interview;

import java.util.UUID;

public interface TemplateService {
    String NAME = "tsadv_TemplateService";
    String getPersonFullName(Case personName, PersonGroupExt personGroup);
    Case getCasePersonName(UUID personGroupId, String language, String caseName);
    Case getCaseJobName(Interview interview, String language, String caseName);
    String getJobName(Case jobName, Interview interview);
}
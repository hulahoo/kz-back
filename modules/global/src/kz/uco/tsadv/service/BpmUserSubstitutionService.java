package kz.uco.tsadv.service;


import kz.uco.tsadv.entity.tb.BpmUserSubstitution;
import kz.uco.tsadv.modules.administration.TsadvUser;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BpmUserSubstitutionService {
    String NAME = "tsadv_BpmUserSubstitutionService";

    String getBpmUserSubstitution(TsadvUser userExt, Date date, boolean path);

    String getCurrentBpmUserSubstitution(TsadvUser userExt, boolean path);

    BpmUserSubstitution reloadBpmUserSubstitution(String id);

    List<BpmUserSubstitution> getBpmUserSubstitutionList(TsadvUser userExt);

    boolean validate(BpmUserSubstitution bpmUserSubstitution);

    BpmUserSubstitution save(BpmUserSubstitution bpmUserSubstitution);

    boolean hasBpmUserSubstitution(BpmUserSubstitution bpmUserSubstitution);

    boolean hasBpmUserSubstitution(UUID entityId, TsadvUser user, Date startDate, Date endDate);

    boolean isCycle(BpmUserSubstitution bpmUserSubstitution);

}
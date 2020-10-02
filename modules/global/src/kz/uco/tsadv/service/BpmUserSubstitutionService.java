package kz.uco.tsadv.service;


import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.entity.tb.BpmUserSubstitution;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BpmUserSubstitutionService {
    String NAME = "tsadv_BpmUserSubstitutionService";

    String getBpmUserSubstitution(UserExt userExt, Date date, boolean path);

    /**
     * @param userExt
     * @param path
     * @return user id or user' ids
     */
    String getCurrentBpmUserSubstitution(UserExt userExt, boolean path);

    BpmUserSubstitution reloadBpmUserSubstitution(String id);

    List<BpmUserSubstitution> getBpmUserSubstitutionList(UserExt userExt);

    boolean hasBpmUserSubstitution(UUID entityId, UserExt user, Date startDate, Date endDate);

    boolean isCycle(BpmUserSubstitution bpmUserSubstitution);

}
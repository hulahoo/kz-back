package kz.uco.tsadv.service;

import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service(PersonDataService.NAME)
public class PersonDataServiceBean implements PersonDataService {

    @Inject
    protected CommonService commonService;

    @Override
    public boolean checkPositionForErrors(Date hireDate, AssignmentExt assignmentExt) {
        if (assignmentExt != null && assignmentExt.getPositionGroup() != null && assignmentExt.getPositionGroup().getPosition() != null) {
            Map map = new HashMap();
            map.put("id", assignmentExt.getPositionGroup().getPosition().getId());
            map.put("hireDate", hireDate);
            String query = "select e from base$PositionExt e " +
                    "       join e.positionStatus status " +
                    "       where e.id = :id" +
                    "       and :hireDate between e.startDate and e.endDate" +
                    "       and status.code = 'ACTIVE'";
            PositionExt position = commonService.getEntity(PositionExt.class, query, map, "position-view");
            if (position == null) {
                return true;
            }
        }
        return false;
    }
}
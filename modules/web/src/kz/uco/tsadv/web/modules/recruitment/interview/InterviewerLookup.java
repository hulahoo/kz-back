package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.model.HiringStepMember;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class InterviewerLookup extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "40px";

    @Inject
    private GroupDatasource<PersonGroupExt, UUID> personGroupsDs;
    @Inject
    private CommonService commonService;
    @Inject
    private EmployeeService employeeService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        params.put("systemDate", CommonUtils.getSystemDate());
        if (params.containsKey("hiringStepMember") && params.get("hiringStepMember") != null) {
            HiringStepMember hiringStepMember = (HiringStepMember) params.get("hiringStepMember");
                if ("USER".equals(hiringStepMember.getHiringMemberType().getCode())) {
                    params.put("hsmPersonGroupId", hiringStepMember.getUserPersonGroup() != null ? hiringStepMember.getUserPersonGroup().getId() : null);
                    personGroupsDs.setQuery("select e" +
                            "                  from base$PersonGroupExt e" +
                            "                  join e.list p" +
                            "                  left join e.assignments a" +
                            "                 where :param$systemDate between p.startDate and p.endDate" +
                            "                   and (p.type.code <> 'EMPLOYEE' " +
                            "                     OR :param$systemDate between a.startDate and a.endDate)" +
                            "                   and e.id = :param$hsmPersonGroupId");
                } else if ("ROLE".equals(hiringStepMember.getHiringMemberType().getCode())) {
                    if ("MANAGER".equals(hiringStepMember.getRole().getCode())){
                        personGroupsDs.setQuery("select e" +
                            "                      from base$PersonGroupExt e" +
                            "                      join e.list p" +
                            "                      left join e.assignments a" +
                            "                     where :param$systemDate between p.startDate and p.endDate" +
                            "                       and (p.type.code <> 'EMPLOYEE' OR :param$systemDate between a.startDate and a.endDate)" +
                            "                       and e.id = :param$managerPersonGroupId");
                    } else {
                        params.put("roleCode", hiringStepMember.getRole().getCode());
                        String qureyString = "select pg from base$PersonGroupExt pg " +
                                " where pg.id in (select e1.personGroupId.id " +
                                "   from tsadv$User e1 " +
                                "   join tsadv$OrganizationHrUser e on e.user.id = e1.id " +
                                "  where e.organizationGroup.id = :param$organizationGroupId " +
                                "    and e.deleteTs is null " +
                                "    and :param$systemDate between e.dateFrom and e.dateTo" +
                                "    and exists (select 1 from tsadv$HrUserRole ur, tsadv$DicHrRole r  " +
                                "                 where ur.user.id = e.user.id " +
                                "                   and r.id = ur.role.id" +
                                "                   and :param$systemDate between ur.dateFrom and ur.dateTo " +
                                "                   and r.code = :param$roleCode)) ";
                        if ("RECRUITING_SPECIALIST".equals(hiringStepMember.getRole().getCode()))
                            qureyString+=" or pg.id in (select pg1.id from base$PersonGroupExt pg1 where pg1.id = :param$recruiterPersonGroupId)";
                        personGroupsDs.setQuery(qureyString);
                    }
                }
        } else {
            personGroupsDs.clear();
        }
    }

    public Component generateUserImageCell(PersonGroupExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPerson(), IMAGE_CELL_HEIGHT, null);
    }
}
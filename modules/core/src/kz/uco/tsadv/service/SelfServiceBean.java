package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.NotificationConfig;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonDocument;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(SelfService.NAME)
public class SelfServiceBean implements SelfService {

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected NotificationService notificationService;

    @Inject
    protected CommonService commonService;

    @Inject
    protected NotificationConfig notificationConfig;

    @Inject
    private Persistence persistence;

    public PositionSsView getPositionSsView(PositionGroupExt positionGroupExt, Date date) {
        return persistence.callInTransaction(em ->
                em.createQuery(" select e from tsadv$PositionSsView e where :date between e.startDate and e.endDate and e.positionGroup.id = :positionGroupId"
                        , PositionSsView.class)
                        .setViewName("positionSsView-view")
                        .setParameter("date", date)
                        .setParameter("positionGroupId", positionGroupExt.getId())
                        .getFirstResult());
    }

    public OrganizationSsView getOrganizationSsView(OrganizationGroupExt organizationGroupExt, Date date) {
        return persistence.callInTransaction(em ->
                em.createQuery(" select e from tsadv$OrganizationSsView e where :date between e.startDate and e.endDate and e.organizationGroup.id = :organizationGroupId"
                        , OrganizationSsView.class)
                        .setViewName("organizationSsView-view")
                        .setParameter("date", date)
                        .setParameter("organizationGroupId", organizationGroupExt.getId())
                        .getFirstResult());
    }

    @Override
    public void checkDocumentEndExpiredDate() {
        Date oneWeek = CommonUtils.getSystemDate();
        oneWeek = DateUtils.addDays(oneWeek, 7);
        String query = "select e from tsadv$PersonDocument e where e.expiredDate = :oneWeek";
        List<PersonDocument> personDocumentList = commonService.getEntities(PersonDocument.class, query,
                ParamsMap.of("oneWeek", oneWeek), "personDocument.forNotification");
        personDocumentList.forEach(personDocument -> {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("personFullName", personDocument.getPersonGroup() != null ? personDocument.getPersonGroup().getPerson().getFullName() : null);
            paramMap.put("documentTypeRu", personDocument.getDocumentType() != null ? personDocument.getDocumentType().getLangValue1() : null);
            paramMap.put("documentTypeEn", personDocument.getDocumentType() != null ? personDocument.getDocumentType().getLangValue3() : null);
            paramMap.put("documentNumber", personDocument.getDocumentNumber());
            paramMap.put("expiredDate", new SimpleDateFormat("dd.MM.yyyy").format(personDocument.getExpiredDate()));
            if (personDocument.getPersonGroup().getCurrentAssignment() != null) {
                if (personDocument.getPersonGroup().getCurrentAssignment().getPositionGroup() != null) {
                    List<UserExt> userExts = employeeService.recursiveFindManager(personDocument.getPersonGroup().getCurrentAssignment().getPositionGroup().getId());
                    if (userExts != null && !userExts.isEmpty()) {
                        if (userExts.size() == 1) {
                            UserExt userExt = userExts.get(0);
                            if (personDocument.getPersonGroup() != null) {
                                if (personDocument.getPersonGroup().getPersonLatinFioWithEmployeeNumber() != null) {
                                    paramMap.put("personFullNameLatin", personDocument.getPersonGroup().getPersonLatinFioWithEmployeeNumber(userExt.getLanguage()));
                                }
                            }
                            notificationService.sendParametrizedNotification("document.expiredDateIsOver.forManager", userExt, paramMap);
                        } else {
                            if (notificationConfig.getAllManager()) {
                                for (UserExt userExt : userExts) {
                                    notificationService.sendParametrizedNotification("document.expiredDateIsOver.forManager", userExt, paramMap);
                                }
                            }
                        }
                    }
                }
            }
            UserExt userExt = employeeService.getUserExtByPersonGroupId(personDocument.getPersonGroup().getId());
            notificationService.sendParametrizedNotification("document.expiredDateIsOver.forPerson", userExt, paramMap);
        });
    }
}
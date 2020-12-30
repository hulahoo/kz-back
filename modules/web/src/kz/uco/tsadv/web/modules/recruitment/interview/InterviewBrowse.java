package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.model.HiringStep;
import kz.uco.tsadv.modules.recruitment.model.Interview;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class InterviewBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<Interview, UUID> interviewsDs;
    @Inject
    protected CollectionDatasource<HiringStep, UUID> hiringStepsDs;
    @Inject
    protected VBoxLayout filterBox;
    @Inject
    protected UserSession userSession;
    @Inject
    protected GroupTable<Interview> interviewsTable;
    protected boolean readOnly;

    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        interviewsTable.getAction("edit").setEnabled(false);
        interviewsDs.addItemChangeListener(e -> {
            interviewsTable.getAction("edit").setEnabled(e.getItem() != null);
        });

        if (params.get("jobRequestIdFromJobRequestSelf") != null) {
            interviewsDs.setQuery("select e from tsadv$Interview e " +
                    " where e.jobRequest.id = :param$jobRequestIdFromJobRequestSelf");
            readOnly = true;
        }

        if (isFromSelfService()) {
            PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            if (personGroup != null) {
                interviewsDs.setQuery(String.format("select e " +
                                "from tsadv$Interview e " +
                                "join e.jobRequest jr " +
                                "join jr.candidatePersonGroup pg " +
                                "join pg.list p " +
                                "join jr.requisition r " +
                                "join e.requisitionHiringStep rhs " +
                                "left join e.mainInterviewerPersonGroup ipg " +
                                "left join ipg.list ip " +
                                "where :session$systemDate between p.startDate and p.endDate " +
                                "and e.mainInterviewerPersonGroup.id = '%s' " +
                                "and e.interviewStatus <> %d",
                        personGroup.getId(),
                        InterviewStatus.DRAFT.getId()));
            }
        }

        initFilterMap();

        customFilter = CustomFilter.init(interviewsDs, interviewsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        if (getFullName() != null) {
            customFilter.selectFilter("mainInterviewerFullName", getFullName());
            customFilter.applyFilter();
        }
        customFilter.selectFilter("candidateFullName");
        customFilter.selectFilter("interviewDate");
    }
    public String getFullName() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userSession.getUser().getId());
        map.put("sysDate", CommonUtils.getSystemDate());
        StringBuilder builder = new StringBuilder("");
        PersonExt person = commonService.getEntity(PersonExt.class,
                "SELECT p " +
                        "FROM base$PersonExt p " +
                        "join tsadv$UserExt user ON p.group.id = user.personGroupId.id " +
                        " where user.id = :userId " +
                        " and :sysDate between p.startDate and p.endDate ",
                map,
                "person-view");
        if (person != null) {
            builder.append(person.getLastName()).append(" ");
            builder.append(person.getFirstName()).append(" ");
            if (person.getMiddleName() != null) builder.append(person.getMiddleName()).append(" ");
            return builder.toString();
        }else {
            return null;
        }
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("requisition",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("JobRequest.requisition"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(r.code) ?"));

        filterMap.put("candidateFullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("JobRequest.candidatePersonGroup"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(p.lastName,concat(' ', concat(p.firstName, concat(' ', coalesce(p.middleName,'')))))) ?")
        );

        filterMap.put("requisitionHiringStep",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Interview.requisitionHiringStep"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", hiringStepsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("rhs.hiringStep.id ?")
        );

        filterMap.put("interviewDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Interview.interviewDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.interviewDate ?"));

        filterMap.put("timeFrom",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Interview.timeFrom"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeFrom ?"));

        filterMap.put("timeTo",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Interview.timeTo"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeTo ?"));

        filterMap.put("mainInterviewerFullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage( "Interview.mainInterviewerPersonGroup"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(ip.lastName,concat(' ', concat(ip.firstName, concat(' ', coalesce(ip.middleName,'')))))) ?")
        );

        filterMap.put("interviewStatus",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Interview.interviewStatus"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsEnum", InterviewStatus.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.interviewStatus ?")
        );
    }

    protected boolean isFromSelfService() {
        return false;
    }

    public void onEdit(Component source) {
        Map<String, Object> map = new HashMap<>();
        if (readOnly) {
            map.put("readOnly", true);
        }
        InterviewEdit interviewEdit = (InterviewEdit) openEditor("tsadv$Interview.edit", interviewsDs.getItem(), WindowManager.OpenType.THIS_TAB, map);
        interviewEdit.addCloseListener(actionId -> {
            interviewsDs.refresh();
        });
    }
}
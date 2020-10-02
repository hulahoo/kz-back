package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.recruitment.model.HiringStep;
import kz.uco.tsadv.modules.recruitment.model.Interview;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ScheduledInterviewBrowse extends AbstractLookup {

    @Named("interviewsTable.create")
    private CreateAction interviewsTableCreate;
    @Named("interviewsTable.edit")
    private EditAction interviewsTableEdit;

    @Inject
    private GroupDatasource<Interview, UUID> interviewsDs;
    @Inject
    private CollectionDatasource<HiringStep, UUID> hiringStepsDs;
    @Inject
    private VBoxLayout filterBox;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        interviewsTableCreate.setWindowId("tsadv$ScheduledInterview.edit");
        interviewsTableEdit.setWindowId("tsadv$ScheduledInterview.edit");

        initFilterMap();

        customFilter = CustomFilter.init(interviewsDs, interviewsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        customFilter.selectFilter("requisition");
        customFilter.selectFilter("interviewDate");
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("requisition",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "JobRequest.requisition"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(r.code) ?"));

        filterMap.put("requisitionHiringStep",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.requisitionHiringStep"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", hiringStepsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("rhs.hiringStep.id ?")
        );

        filterMap.put("interviewDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.interviewDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.interviewDate ?"));

        filterMap.put("timeFrom",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.timeFrom"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeFrom ?"));

        filterMap.put("timeTo",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.timeTo"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeTo ?"));

        filterMap.put("mainInterviewerFullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.mainInterviewerPersonGroup"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(ip.lastName,concat(' ', concat(ip.firstName, concat(' ', coalesce(ip.middleName,'')))))) ?")
        );
    }
}
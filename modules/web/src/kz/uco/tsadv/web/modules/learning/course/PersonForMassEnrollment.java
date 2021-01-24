package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class PersonForMassEnrollment extends AbstractWindow {

    @Inject
    protected DataGrid<PersonGroupExt> personGroupExtsDataGrid;
    @Inject
    protected GroupDatasource<PersonGroupExt, UUID> personGroupExtsDs;
    @Inject
    protected Button chooseBtn;
    protected Map<String, Object> param;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
        Map<String, Object> paramforDS = new HashMap<>();
        String select = "select distinct e from base$PersonGroup e join base$AssignmentExt ae on ae.personGroupId = e";
        String where = " where ae.personGroup.id not in (select en.personGroup.id from " +
                " tsadv$Enrollment en where en.course.id = :param$courseId ) ";
        if (params.containsKey("organizationGroup") && params.get("organizationGroup") != null) {
            if (params.containsKey("inOrganizaiton")) {
                if ((Boolean) params.get("inOrganizaiton") == false) {
                    select = select + " join base$OrganizationExt oe on  ae.organizationGroup.id = oe.group.id ";
                    where = where + " and :custom$organizationGroup = oe.group.id ";
                    paramforDS.put("organizationGroup", params.get("organizationGroup"));
                } else {
                    where = where + " and ae.organizationGroup.id in (select os.organizationGroup.id from " +
                            " tsadv$OrganizationStructure os where os.path like concat('%',concat(:custom$organizationGroup,'%')) " +
                            " and CURRENT_DATE between os.startDate and os.endDate) ";
                    paramforDS.put("organizationGroup", ((OrganizationGroupExt) params.get("organizationGroup")).getId().toString());
                }
            } else {
                select = select + " join base$OrganizationExt oe on  ae.organizationGroup.id = oe.group.id ";
                where = where + " and and :custom$organizationGroup = oe.group.id ";
                paramforDS.put("organizationGroup", params.get("organizationGroup"));
            }
        }
        if (params.containsKey("positionGroup") && params.get("positionGroup") != null) {
            select = select + " join base$PositionExt pe on ae.positionGroup.id = pe.group.id ";
            where = where + " and :custom$positionGroup = pe.group.id " +
                    " and :session$systemDate between pe.startDate and pe.endDate";
            paramforDS.put("positionGroup", params.get("positionGroup"));
        }
        if (params.containsKey("jobGroup") && params.get("jobGroup") != null) {
            select = select + " join tsadv$Job j on ae.jobGroup.id = j.group.id ";
            where = where + " and :custom$jobGroup = j.group.id " +
                    " and :session$systemDate between j.startDate and j.endDate";
            paramforDS.put("jobGroup", params.get("jobGroup"));
        }
        personGroupExtsDs.setQuery(select + where);
        personGroupExtsDs.refresh(paramforDS);

        personGroupExtsDataGrid.addSelectionListener(event -> {
            personGroupExtsDataGridSelectionListener(event);
        });

        personGroupExtsDataGrid.addGeneratedColumn("attempDate", new DataGrid.ColumnGenerator<PersonGroupExt, Date>() {
            @Override
            public Date getValue(DataGrid.ColumnGeneratorEvent<PersonGroupExt> event) {
                Course course = (Course) param.get("courseId");
                List<CourseSectionAttempt> attempts = getAttempts(event.getItem(), course != null ? course.getId() : null);
                return !attempts.isEmpty() && attempts.size() != 0 ? attempts.get(0).getAttemptDate() : null;
            }

            @Override
            public Class<Date> getType() {
                return Date.class;
            }
        });
    }

    protected List<CourseSectionAttempt> getAttempts(PersonGroupExt personGroupExt, UUID courseId) {
        Map<String, Object> map = new HashMap<>();
//        map.put("courseId", courseId);
        map.put("personGroupExtId", personGroupExt != null ? personGroupExt.getId() : null);
        return commonService.getEntities(CourseSectionAttempt.class,
                " select e from tsadv$CourseSectionAttempt e " +
                        "where e.enrollment.personGroupId.id = :personGroupExtId " +
                        " order by e.attemptDate DESC", map, "courseSectionAttempt.edit");
    }

    protected void personGroupExtsDataGridSelectionListener(DataGrid.SelectionEvent<PersonGroupExt> event) {
        chooseBtn.setEnabled(!personGroupExtsDataGrid.getSelected().isEmpty());
    }


    public void onChooseBtnClick() {
        List<PersonGroupExt> selected = personGroupExtsDataGrid.getSelected().stream().collect(Collectors.toList());
        AbstractWindow window = openWindow("template-for-mass-enrollment", WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("persons", selected,
                        "courseId", param.containsKey("courseId") ? param.get("courseId") : null));
        window.addCloseListener(actionId -> {
            windowCloseListener(actionId);
        });
    }

    protected void windowCloseListener(String actionId) {
        if ("commit".equals(actionId)) {
            close(actionId, true);
        }
    }

    public void onCancelBtnClick() {
        close("cancel", true);
    }
}
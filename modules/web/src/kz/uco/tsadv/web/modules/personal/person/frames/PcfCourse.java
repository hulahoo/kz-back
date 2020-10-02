package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.base.service.common.CommonService;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PcfCourse extends EditableFrame {


    @Inject
    private CommonService commonService;

    public CollectionDatasource<Enrollment, UUID> enrollmentsApprovedDs;
    public CollectionDatasource <CourseSectionAttempt, UUID> courseSectionAttemptsDs;
    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
enrollmentsApprovedDs.addItemChangeListener(e -> {

    String query = "SELECT e FROM tsadv$CourseSectionAttempt e " +
            "WHERE e.enrollment.id = :ds$enrollmentsApprovedDs.id and e.id in :custom$uuidList \n" +
            "                order by e.attemptDate desc";

    String query0 = "SELECT MAX(e.id :: TEXT)  :: UUID FROM tsadv_course_section_attempt e\n" +
            "  JOIN tsadv_enrollment e2\n" +
            "  ON e2.id=e.enrollment_id\n" +
            "  JOIN  tsadv_course_section cs\n" +
            "    ON cs.id=e.course_section_id\n" +
            "WHERE e.enrollment_id = e2.id and  e2.id=?1::UUID and COALESCE(e.attempt_date, ?2) = (select max(coalesce(csa.attempt_date, ?2)) from tsadv_course_section_attempt csa where csa.course_section_id = e.course_section_id  ) GROUP BY e.course_section_id";


    Map<Integer, Object> map = new HashMap();

    if (enrollmentsApprovedDs.getItem()!= null)
        map.put(1, enrollmentsApprovedDs.getItem().getId());
    try {
        map.put(2, df.parse("1900-01-01"));
    } catch (ParseException e1) {
        e1.printStackTrace();
    }

    List list = commonService.emNativeQueryResultList(query0, map);

    courseSectionAttemptsDs.setQuery(query);

    try {
        Map<String, Object> map1 = new HashMap();
        map1.put("nullDate",df.parse("1900-01-01"));
        map1.put("uuidList", list);
        courseSectionAttemptsDs.refresh(map1);
    } catch (ParseException e1) {
        e1.printStackTrace();
    }
});

}

    @Override
    public void initDatasource() {
        enrollmentsApprovedDs = (CollectionDatasource<Enrollment, UUID>) getDsContext().get("enrollmentsApprovedDs");
        courseSectionAttemptsDs = (CollectionDatasource<CourseSectionAttempt, UUID>) getDsContext().get("courseSectionAttemptsDs");
    }
}
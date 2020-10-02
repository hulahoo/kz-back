package kz.uco.tsadv.web.modules.learning.coursesection;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.CourseSection;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class CourseSectionBrowse extends AbstractLookup {
    @Inject
    private GroupDatasource<CourseSection, UUID> courseSectionsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromCourseSectionSessionEdit")) {
            courseSectionsDs.setQuery(courseSectionsDs.getQuery() + " where e.course.id in " +
                    "(select ct.course.id from tsadv$CourseTrainer ct where ct.trainer.id = :param$trainerId)");
        }
    }
}
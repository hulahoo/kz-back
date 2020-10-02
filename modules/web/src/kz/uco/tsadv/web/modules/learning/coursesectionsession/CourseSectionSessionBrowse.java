package kz.uco.tsadv.web.modules.learning.coursesectionsession;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class CourseSectionSessionBrowse extends AbstractLookup {

    @WindowParam
    protected UUID courseSectionId;

    @WindowParam
    protected UUID courseId;

    @Inject
    private GroupDatasource<CourseSectionSession, UUID> courseSectionSessionsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (courseSectionId != null) {
            courseSectionSessionsDs.setQuery(String.format(
                    "select e from tsadv$CourseSectionSession e " +
                            "where e.courseSection.id = '%s'",
                    courseSectionId.toString()));
        }

        if (courseId != null) {
            courseSectionSessionsDs.setQuery(String.format(
                    "select e from tsadv$CourseSectionSession e " +
                            "join e.courseSection cs " +
                            "join cs.course c " +
                            "where c.id = '%s'",
                    courseId.toString()));
        }
    }
}
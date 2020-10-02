package kz.uco.tsadv.web.modules.learning.coursesectionsession;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RegisterCourseSessions extends AbstractLookup {

    @WindowParam
    protected UUID courseSectionId;

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
    }
}
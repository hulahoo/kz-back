package kz.uco.tsadv.importer;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * Created by Yelaman on 06.08.2018.
 */
@Component(CourseSectionAttemptImporter.NAME)
@Scope("prototype")
public class CourseSectionAttemptImporter extends XlsImporter {
    public static final String NAME = "tsadv_CourseSectionAttempt";

    public static final String EMPLOYEE = "employee";
    public static final String IIN = "iin";
    public static final String ACTIVE_ATTEMPT = "activeAttempt";
    public static final String SUCCESS = "success";
    public static final String GROUP_NAME = "group_name";

    @Inject
    protected CommonService commonService;
    protected PersonGroupExt personGroupExt;

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        int i = 0;
        Map<String, Integer> columns = new HashMap<>();
        columns.put(EMPLOYEE, i++);
        columns.put(IIN, i++);
        columns.put(ACTIVE_ATTEMPT, i++);
        columns.put(SUCCESS, i++);
        columns.put(GROUP_NAME, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
//        Map<String, Object> defaults = new HashMap<>();
//        defaults.put(SUCCESS, true);
        return null;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, EMPLOYEE);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();

        CourseSectionAttempt attempt = metadata.create(CourseSectionAttempt.class);
        personGroupExt = getEmployee(XlsHelper.getParameterStringValue(values, IIN));
        Boolean activeAttempt = Boolean.valueOf(XlsHelper.getParameterStringValue(values, ACTIVE_ATTEMPT));
        Boolean success = Boolean.valueOf(XlsHelper.getParameterStringValue(values, SUCCESS));
        attempt.setCourseSectionSession(getCourseSectionSession(XlsHelper.getParameterStringValue(values, GROUP_NAME)));

        attempt.setEnrollment(getEnrollment());
        attempt.setActiveAttempt(activeAttempt);
        attempt.setSuccess(success);
        result.add(attempt);

        return result;
    }

    protected PersonGroupExt getEmployee(String iin) {
        PersonExt person = commonService.getEntity(PersonExt.class, "select e from base$PersonExt e where e.nationalIdentifier = :iin",
                Collections.singletonMap("iin", iin),
                "person-edit");

        return person != null ? person.getGroup() : null;
    }

    protected Enrollment getEnrollment() {
        Enrollment enrollment = commonService.getEntity(Enrollment.class, "select e from tsadv$Enrollment e where e.personGroup.id = :personGroupId",
                Collections.singletonMap("personGroupId", personGroupExt != null ? personGroupExt.getId() : null), "enrollment-view");

        return enrollment;
    }

    protected CourseSectionSession getCourseSectionSession(String name) {
        CourseSectionSession session = commonService.getEntity(CourseSectionSession.class, "select e from tsadv$CourseSectionSession e where e.name = :name",
                Collections.singletonMap("name", name), "myCourseSectionSession.edit");

        return session;
    }
}

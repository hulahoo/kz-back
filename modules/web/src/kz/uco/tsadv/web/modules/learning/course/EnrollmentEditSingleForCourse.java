package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Named;
import java.util.Map;

public class EnrollmentEditSingleForCourse extends AbstractEditor<Enrollment> {
    @Named("fieldGroup.personGroup")
    protected PickerField personGroupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personGroupField.getLookupAction().setLookupScreen("base$PersonGroupExt.for.enrollment.browse");
        personGroupField.getLookupAction().setLookupScreenParams(ParamsMap.of("courseId",
                params.get("courseId")));
    }

    @Override
    public void ready() {
        super.ready();
        if (!PersistenceHelper.isNew(getItem())) {
            personGroupField.setEditable(false);
        }
    }
}
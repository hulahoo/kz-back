package kz.uco.tsadv.web.modules.learning.coursesectionsession;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.performance.model.Trainer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class CourseSectionSessionEdit extends AbstractEditor<CourseSectionSession> {
    @Named("fieldGroup.courseSection")
    protected PickerField courseSectionField;

    @Named("fieldGroup.trainer")
    protected PickerField trainerField;

    @Inject
    protected Datasource<CourseSectionSession> courseSectionSessionDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromMyCourseSectionSession")) {
            courseSectionField.getLookupAction().setLookupScreenParamsSupplier(() ->
                    ParamsMap.of("fromCourseSectionSessionEdit", true,
                            "trainerId", (trainerField.getValue() != null
                                    ? ((Trainer) trainerField.getValue()).getId() : null)));
            courseSectionField.setVisible(true);
        }
    }
}
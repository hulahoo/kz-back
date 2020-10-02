package kz.uco.tsadv.web.personlearninghistory;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.TextField;
import kz.uco.tsadv.modules.personal.model.PersonLearningHistory;

import javax.inject.Named;
import java.util.Map;

public class PersonLearningHistoryEdit extends AbstractEditor<PersonLearningHistory> {

    @Named("fieldGroup.course")
    protected PickerField courseField;
    @Named("fieldGroup.courseName")
    protected TextField courseNameField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setupCalculationCourseName();
    }

    protected void setupCalculationCourseName() {
        courseField.addValueChangeListener(e -> {
            if (courseField != null) {
                getItem().setCourseName(getItem().getCourse() != null ? getItem().getCourse().getName() : null);
                if (getItem().getCourse() != null) {
                    courseNameField.setEnabled(false);
                } else {
                    courseNameField.setEnabled(true);
                }
            }
        });
    }
}
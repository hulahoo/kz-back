package kz.uco.tsadv.web.modules.learning.internship;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.Internship;

import javax.inject.Inject;
import javax.inject.Named;


public class InternshipEdit extends AbstractEditor<Internship> {
    @Named("fieldGroup.mainMentor")
    protected PickerField mainMentorField;
    @Inject
    private ComponentsFactory componentsFactory;

    public Component commentTextArea(Datasource datasource, String fieldId) {
        TextArea comment = componentsFactory.createComponent(TextArea.class);
        comment.setRows(5);
        comment.setDatasource(datasource, fieldId);
        return comment;
    }
}
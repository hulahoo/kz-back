package kz.uco.tsadv.web.modules.personal.personattachment;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.FileUploadField;
import kz.uco.tsadv.modules.recruitment.model.PersonAttachment;
import com.haulmont.cuba.core.entity.FileDescriptor;

import javax.inject.Inject;
import java.util.Map;

public class PersonAttachmentEdit extends AbstractEditor<PersonAttachment> {

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);


    }

    @Override
    protected void postInit() {
        super.postInit();
        FileUploadField attachmentField = (FileUploadField) fieldGroup.getField("attachment").getComponent();
        attachmentField.addValueChangeListener((e) -> {
            if (e.getValue() != null)
            {
                FileDescriptor attachment = (FileDescriptor) e.getValue();
                getItem().setFilename(attachment.getName());
            }
        });
    }
}
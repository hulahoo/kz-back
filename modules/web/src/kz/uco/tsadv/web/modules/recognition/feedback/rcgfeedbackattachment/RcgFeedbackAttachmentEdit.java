package kz.uco.tsadv.web.modules.recognition.feedback.rcgfeedbackattachment;

import com.google.common.collect.Sets;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FileUploadField;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackAttachment;

import javax.inject.Named;
import java.util.Map;

public class RcgFeedbackAttachmentEdit extends AbstractEditor<RcgFeedbackAttachment> {

    @Named("fieldGroup.file")
    private FileUploadField fileField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        fileField.setFileSizeLimit(10 * 1024 * 1024);
        fileField.setPermittedExtensions(Sets.newHashSet(".jpg", ".jpeg", ".png"));
    }
}
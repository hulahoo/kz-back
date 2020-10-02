package kz.uco.tsadv.web.lmssliderimage;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FileUploadField;
import kz.uco.tsadv.lms.entity.LmsSliderImage;

import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

public class LmsSliderImageEdit extends AbstractEditor<LmsSliderImage> {
    @Named("fieldGroup.image")
    private FileUploadField imageField;

    @Override
    public void init(Map<String, Object> params) {
        imageField.setPermittedExtensions(Collections.singleton(".jpg"));
        super.init(params);
    }
}
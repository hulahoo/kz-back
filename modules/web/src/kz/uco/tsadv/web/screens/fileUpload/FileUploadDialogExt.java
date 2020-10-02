package kz.uco.tsadv.web.screens.fileUpload;

import com.haulmont.cuba.core.global.ClientType;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Window;

import javax.inject.Inject;
import java.util.Map;

public class FileUploadDialogExt extends FileUploadDialog {
    @Inject
    protected CheckBox checkBox;

    protected boolean isLogSuccessful;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        fileUpload.addFileUploadStartListener(e -> isLogSuccessful = checkBox.isChecked());
    }


    public boolean isLogSuccessful() {
        return isLogSuccessful;
    }
}
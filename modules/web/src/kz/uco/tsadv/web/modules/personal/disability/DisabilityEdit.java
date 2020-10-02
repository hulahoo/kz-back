package kz.uco.tsadv.web.modules.personal.disability;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.Disability;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;

public class DisabilityEdit extends AbstractEditor<Disability> {

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private FieldGroup fieldGroup;

    @Override
    protected void postInit() {
        super.postInit();
        initUploader();
    }

    private void initUploader() {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);

        TextField fileName = componentsFactory.createComponent(TextField.class);
        fileName.setWidth("250px");
        fileName.setEditable(false);
        fileName.setValue(PersistenceHelper.isNew(getItem()) ? "" : getItem().getAttachmentName());
        hBoxLayout.add(fileName);

        FileUploadField fileUploadField = componentsFactory.createComponent(FileUploadField.class);
        fileUploadField.setFileSizeLimit(1024000);
        fileUploadField.setUploadButtonCaption("");
        fileUploadField.setClearButtonCaption("");
        fileUploadField.setUploadButtonIcon("icons/reports-template-upload.png");
        fileUploadField.setClearButtonIcon("icons/item-remove.png");
        fileUploadField.setShowClearButton(true);
        fileUploadField.addFileUploadSucceedListener(event -> {
            Disability disability = getItem();
            if (disability != null && fileUploadField.getFileContent() != null) {
                try {
                    disability.setAttachment(IOUtils.toByteArray(fileUploadField.getFileContent()));
                    disability.setAttachmentName(fileUploadField.getFileName());
                    fileName.setValue(fileUploadField.getFileName());

                } catch (IOException e) {
                    showNotification(getMessage("fileUploadErrorMessage"), NotificationType.ERROR);
                }
            }
            fileUploadField.setValue(null);
        });

        fileUploadField.addBeforeValueClearListener(beforeEvent -> {
            beforeEvent.preventClearAction();
            showOptionDialog("", "Are you sure you want to delete this file?", MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                                public void actionPerform(Component component) {
                                        getItem().setAttachment(null);
                                        getItem().setAttachmentName(null);
                                    fileName.setValue("");
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });


        hBoxLayout.add(fileUploadField);

        fieldGroup.getFieldNN("attachment").setWidth("500px");
        fieldGroup.getFieldNN("attachment").setComponent(hBoxLayout);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close){
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), Frame.NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}
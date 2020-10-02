package kz.uco.tsadv.web.modules.learning.learningobject;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.modules.learning.enums.ContentType;
import kz.uco.tsadv.modules.learning.model.LearningObject;
import kz.uco.tsadv.service.LearningService;
import kz.uco.tsadv.service.VideoService;

import javax.inject.Inject;
import javax.inject.Named;

public class LearningObjectEdit extends AbstractEditor<LearningObject> {

    @Inject
    protected Datasource<LearningObject> learningObjectDs;
    @Named("fieldGroup.contentType")
    protected LookupField contentTypeField;
    @Named("fieldGroup.url")
    protected TextField urlField;
    @Named("fieldGroup.file")
    protected FileUploadField fileField;
    @Inject
    protected ResizableTextArea htmlText;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected RichTextArea text;
    @Inject
    protected Button playButton;
    @Inject
    protected CommonConfig commonConfig;
    @Inject
    protected VideoService videoService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected LearningService learningService;
    @Named("fieldGroup.objectName")
    protected TextField objectNameField;
    protected String mainApp;


    @Override
    public void ready() {
        mainApp = commonConfig.getMainApp();
        if ("knu".equals(mainApp)) {
            fileField.addFileUploadSucceedListener(e -> {
                if (ContentType.VIDEO.equals(learningObjectDs.getItem().getContentType())) {
                    videoService.startScheduler(learningObjectDs.getItem().getFile());
                }
            });
        } else {
            urlField.setVisible(true);
            urlField.setRequired(true);
            if (PersistenceHelper.isNew(learningObjectDs.getItem())) {
                learningObjectDs.getItem().setContentType(ContentType.URL);
            }
            contentTypeField.setVisible(false);
            playButton.setVisible(false);
        }
        learningObjectDs.addItemPropertyChangeListener(this::itemPropertyChanged);
        setVisibleField();

        objectNameField.addValidator(value -> {
            if (ContentType.SCORM_ZIP.equals(learningObjectDs.getItem().getContentType())) {
                Long count = commonService.getCount(LearningObject.class,
                        "select e from tsadv$LearningObject e where e.objectName = :objectName " +
                                " and e.contentType = :scormZip",
                        ParamsMap.of("objectName", learningObjectDs.getItem().getObjectName()
                                , "scormZip", ContentType.SCORM_ZIP));
                if (count > 0) {
                    throw new ValidationException(getMessage("scorm-is-exist"));
                }
            }
        });
        fileField.addValidator(value -> {
            if (ContentType.SCORM_ZIP.equals(learningObjectDs.getItem().getContentType())) {
                if (!"zip".equals(learningObjectDs.getItem().getFile().getExtension())) {
                    throw new ValidationException(getMessage("zip-required"));
                }
            }
        });
    }

    protected void itemPropertyChanged(Datasource.ItemPropertyChangeEvent<LearningObject> e) {
        if (e.getProperty().equals("contentType")) {
            setVisibleField();
        }
        if ("knu".equals(mainApp) && e.getProperty().equals("file")
                && ContentType.SCORM_ZIP.equals(e.getDs().getItem().getContentType())) {
            if (e.getPrevValue() != null) {
                learningService.deletePackage(((FileDescriptor) e.getPrevValue()).getName());
            }
            if (e.getValue() != null) {
                String result = learningService.unzipPackage(e.getDs().getItem().getObjectName(),
                        ((FileDescriptor) e.getValue()));
                if (result.equals("Error")) {
                    showNotification(getMessage("not-published"));
                } else {
                    learningObjectDs.getItem().setUrl(result);
                }
            }
        }
    }

    protected void setVisibleField() {
        if (ContentType.URL.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setVisible(true);
            urlField.setRequired(true);
            fileField.setVisible(false);
            fileField.setRequired(false);
            if (fileField.getValue() != null) {
                clearFileDescriptor();
            }
            htmlText.setRequired(false);
            htmlText.setVisible(false);
            htmlText.setValue(null);
            text.setRequired(false);
            text.setVisible(false);
            text.setValue(null);
            playButton.setVisible(true);

        } else if (ContentType.VIDEO.equals(learningObjectDs.getItem().getContentType())
                || ContentType.PDF.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setValue(null);
            urlField.setVisible(false);
            urlField.setRequired(false);
            fileField.setVisible(true);
            fileField.setRequired(true);
            htmlText.setRequired(false);
            htmlText.setVisible(false);
            htmlText.setValue(null);
            text.setRequired(false);
            text.setVisible(false);
            text.setValue(null);
            playButton.setVisible(true);

        } else if (ContentType.FILE.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setValue(null);
            urlField.setVisible(false);
            urlField.setRequired(false);
            fileField.setVisible(true);
            fileField.setRequired(true);
            htmlText.setRequired(false);
            htmlText.setVisible(false);
            htmlText.setValue(null);
            text.setRequired(false);
            text.setVisible(false);
            text.setValue(null);
            playButton.setVisible(false);

        } else if (ContentType.HTML.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setValue(null);
            urlField.setVisible(false);
            urlField.setRequired(false);
            fileField.setVisible(false);
            fileField.setRequired(false);
            if (fileField.getValue() != null) {
                clearFileDescriptor();
            }
            htmlText.setRequired(true);
            htmlText.setVisible(true);
            text.setRequired(false);
            text.setVisible(false);
            text.setValue(null);
            playButton.setVisible(true);

        } else if (ContentType.TEXT.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setValue(null);
            urlField.setVisible(false);
            urlField.setRequired(false);
            fileField.setVisible(false);
            fileField.setRequired(false);
            if (fileField.getValue() != null) {
                clearFileDescriptor();
            }
            htmlText.setRequired(false);
            htmlText.setVisible(false);
            htmlText.setValue(null);
            text.setRequired(true);
            text.setVisible(true);
            playButton.setVisible(false);
        } else if (ContentType.SCORM_ZIP.equals(learningObjectDs.getItem().getContentType())) {
            urlField.setVisible(true);
            urlField.setRequired(false);
            urlField.setEditable(false);
            fileField.setVisible(true);
            fileField.setRequired(true);
            htmlText.setRequired(false);
            htmlText.setVisible(false);
            htmlText.setValue(null);
            text.setRequired(false);
            text.setVisible(false);
            playButton.setVisible(true);
        }
    }

    protected void clearFileDescriptor() {
        FileDescriptor file = learningObjectDs.getItem().getFile();
        learningObjectDs.getItem().setFile(null);
        try {
            fileUploadingAPI.deleteFile(file.getId());
        } catch (FileStorageException e1) {
            e1.printStackTrace();
        }
    }

    public void play() {
        if (learningObjectDs.getItem().getContentType() != null
                && (ContentType.URL.toString().equals(learningObjectDs.getItem().getContentType().toString())
                || ContentType.SCORM_ZIP.toString().equals(learningObjectDs.getItem().getContentType().toString()))
                && learningObjectDs.getItem().getUrl() != null && !learningObjectDs.getItem().getUrl().isEmpty()) {
            showWebPage(learningObjectDs.getItem().getUrl(), null);
        } else {
            fixYouTube();
            openWindow("learning-object-player", WindowManager.OpenType.DIALOG,
                    ParamsMap.of("item", learningObjectDs.getItem()));
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (ContentType.PDF.equals(learningObjectDs.getItem().getContentType())) {
            if (!"pdf".equals(learningObjectDs.getItem().getFile().getExtension())) {
                errors.add(getMessage("pdf-required"));
            }
        }
        super.postValidate(errors);
    }


    @Override
    protected boolean preCommit() {
        fixYouTube();
        return super.preCommit();
    }

    protected void fixYouTube() {
        if (ContentType.HTML.equals(learningObjectDs.getItem().getContentType())
                && learningObjectDs.getItem().getHtml() != null
                && learningObjectDs.getItem().getHtml().toLowerCase().contains("youtube.com")) {
            try {
                int length = learningObjectDs.getItem().getHtml().length();
                int start = learningObjectDs.getItem().getHtml().indexOf("width=\"") + 7;
                int end = learningObjectDs.getItem().getHtml().indexOf("\"", start);
                String html = learningObjectDs.getItem().getHtml().substring(0, start)
                        + "100%" + learningObjectDs.getItem().getHtml().substring(end, length);
                learningObjectDs.getItem().setHtml(html);
                length = learningObjectDs.getItem().getHtml().length();
                start = learningObjectDs.getItem().getHtml().indexOf("height=\"") + 8;
                end = learningObjectDs.getItem().getHtml().indexOf("\"", start);
                html = learningObjectDs.getItem().getHtml().substring(0, start)
                        + "100%" + learningObjectDs.getItem().getHtml().substring(end, length);
                learningObjectDs.getItem().setHtml(html);

            } catch (Exception e) {
            }
        }
    }
}
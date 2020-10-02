package kz.uco.tsadv.web.modules.learning.administration;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog;
import com.haulmont.cuba.gui.backgroundwork.BackgroundWorkWindow;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.learning.model.ImportLearningLog;
import kz.uco.tsadv.service.LearningService;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ImportLearningData extends AbstractWindow {

    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private DataManager dataManager;
    @Inject
    private LearningService learningService;
    @Inject
    private CollectionDatasource<ImportLearningLog, UUID> importLearningLogDs;
    @Inject
    private FileUploadingAPI fileUploadingAPI;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    public void importQuestionBankData() {
        final FileUploadDialog fileUploadDialog = (FileUploadDialog) openWindow("fileUploadDialog", WindowManager.OpenType.DIALOG);

        fileUploadDialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileDescriptor fileDescriptor = fileUploadingAPI.getFileDescriptor(fileUploadDialog.getFileId(), fileUploadDialog.getFileName());
                try {
                    fileUploadingAPI.putFileIntoStorage(fileUploadDialog.getFileId(), fileDescriptor);

                    runQuestionBankTask(dataManager.commit(fileDescriptor));

                } catch (Exception e) {
                    showNotification(e.getMessage(), NotificationType.TRAY);
                }
            }
        });
    }

    public void importTestData() {
        final FileUploadDialog fileUploadDialog = (FileUploadDialog) openWindow("fileUploadDialog", WindowManager.OpenType.DIALOG);

        fileUploadDialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileDescriptor fileDescriptor = fileUploadingAPI.getFileDescriptor(fileUploadDialog.getFileId(), fileUploadDialog.getFileName());
                try {
                    fileUploadingAPI.putFileIntoStorage(fileUploadDialog.getFileId(), fileDescriptor);

                    runTestTask(dataManager.commit(fileDescriptor));

                    showNotification(getMessage("msg.info.title"),
                            getMessage("test.success.upload"),
                            NotificationType.TRAY);
                } catch (Exception e) {
                    showNotification(e.getMessage(), NotificationType.TRAY);
                }
            }
        });
    }

    public void downloadFile(ImportLearningLog importLearningLog, String name) {
        exportDisplay.show(importLearningLog.getFile());
    }

    private void runQuestionBankTask(FileDescriptor fileDescriptor) throws Exception {
        BackgroundTask<Integer, Void> task = new BackgroundTask<Integer, Void>(300, this) {
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                learningService.importQuestionBank(fileDescriptor);
                return null;
            }

            @Override
            public void canceled() {
                // Do something in UI thread if the task is canceled
            }

            @Override
            public void done(Void result) {
                importLearningLogDs.refresh();
                showNotification(getMessage("msg.info.title"),
                        getMessage("test.success.upload"),
                        NotificationType.TRAY);
            }

            @Override
            public boolean handleException(Exception ex) {
                showNotification(ex.getMessage(),
                        NotificationType.TRAY);
                return true;
            }
        };

        BackgroundWorkWindow.show(task, true);
    }

    private void runTestTask(FileDescriptor fileDescriptor) throws Exception {
        BackgroundTask<Integer, Void> task = new BackgroundTask<Integer, Void>(300, this) {
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                learningService.importTests(fileDescriptor);
                return null;
            }

            @Override
            public void canceled() {
                // Do something in UI thread if the task is canceled
            }

            @Override
            public void done(Void result) {
                importLearningLogDs.refresh();
            }
        };

        BackgroundWorkWindow.show(task, true);
    }

}
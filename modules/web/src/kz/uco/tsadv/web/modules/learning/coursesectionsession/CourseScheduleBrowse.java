package kz.uco.tsadv.web.modules.learning.coursesectionsession;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog;
import com.haulmont.cuba.gui.components.*;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.administration.importer.ImportLog;
import kz.uco.tsadv.modules.administration.importer.ImportScenario;
import kz.uco.tsadv.modules.administration.importer.LogRecordLevel;
import kz.uco.tsadv.service.ImporterService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class CourseScheduleBrowse extends AbstractLookup {
    @Named("courseSectionSessionsTable.create")
    protected CreateAction courseSectionSessionsTableCreate;
    @Named("courseSectionSessionsTable.edit")
    protected EditAction courseSectionSessionsTableEdit;
    @Named("courseSectionSessionsTable")
    protected GroupTable courseSectionSessionsTable;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private ImporterService importerService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        String windowEditId = "tsadv$CourseSchedule.edit";
        courseSectionSessionsTableCreate.setWindowId(windowEditId);
        courseSectionSessionsTableEdit.setWindowId(windowEditId);

        courseSectionSessionsTable.addAction(new BaseAction("upload"){
            @Override
            public void actionPerform(Component component) {
                uploadData();
            }
        });
    }

    protected void uploadData(){
        ImportScenario scenario = findImportScenario("tsadv_CourseSectionAttempt");
        if (scenario == null){
            showNotification(getMessage("msg.error.title"),
                    getMessage("import.scenario.null"),
                    NotificationType.TRAY);
            return;
        }

        FileUploadDialog uploadDialog = (FileUploadDialog) openWindow("fileUploadDialog", WindowManager.OpenType.DIALOG);
        uploadDialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)){
                FileUploadingAPI fileUploadingAPI = AppBeans.get(FileUploadingAPI.class);
                FileDescriptor descriptor = fileUploadingAPI.getFileDescriptor(uploadDialog.getFileId(), uploadDialog.getFileName());
                try {
                    fileUploadingAPI.putFileIntoStorage(uploadDialog.getFileId(), descriptor);
                    descriptor = dataManager.commit(descriptor);

                    ImportLog log = metadata.create(ImportLog.class);
                    log.setImportScenario(scenario);
                    log.setFile(descriptor);
                    log = dataManager.commit(log);
                    log = importerService.doImport(log, null, true);

                    showCompletionMessage(log);
                } catch (FileStorageException e) {
                    e.printStackTrace();
                    showNotification(getMessage("ImportScenarioBrowse.uploadError"), NotificationType.ERROR);
                }
            }
        });
    }



    protected ImportScenario findImportScenario(String value){
        LoadContext<ImportScenario> loadContext = LoadContext.create(ImportScenario.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$ImportScenario e " +
                        "where e.importerBeanName = :importerBeanName");

        query.setParameter("importerBeanName", value);
        loadContext.setQuery(query);
        loadContext.setView("importScenario.view");
        return dataManager.load(loadContext);
    }



    private void showCompletionMessage(ImportLog log) {
        long errorCount = log.getRecords().stream().filter(r -> r.getLevel().equals(LogRecordLevel.ERROR)).count();
        long warnCount = log.getRecords().stream().filter(r -> r.getLevel().equals(LogRecordLevel.WARN)).count();

        if ((errorCount + warnCount) == 0) {
            showOptionDialog(getMessage("ImportScenarioBrowse.importResult")
                    , getMessage("ImportScenarioBrowse.importSuccess")
                    , MessageType.CONFIRMATION_HTML
                    , new Action[]{
                            new DialogAction(DialogAction.Type.OK, true)
                    });
        } else {
            showOptionDialog(getMessage("ImportScenarioBrowse.importResult")
                    , String.format(getMessage("ImportScenarioBrowse.importError"), errorCount, warnCount)
                    , MessageType.WARNING_HTML
                    , new Action[]{
                            new DialogAction(DialogAction.Type.OK, true) {
                                @Override
                                public void actionPerform(Component component) {
                                    super.actionPerform(component);
                                    openWindow("tsadv$ImportLog.browse", WindowManager.OpenType.NEW_TAB,
                                            ParamsMap.of("selectLogItem", log));
                                }
                            }
                            , new DialogAction(DialogAction.Type.CANCEL)
                    });
        }
    }

}
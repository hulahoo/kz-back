package kz.uco.tsadv.web.modules.administration.importscenario;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
import kz.uco.tsadv.modules.administration.importer.*;
import kz.uco.tsadv.service.ImporterService;
import kz.uco.tsadv.web.screens.fileUpload.FileUploadDialogExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImportScenarioBrowse extends AbstractLookup {
    private static final Logger log = LoggerFactory.getLogger(ImportScenarioBrowse.class);

    @Inject
    private ImporterService importerService;

    @Inject
    private CollectionDatasource<ImportScenario, UUID> importFilesDs;

    @Inject
    private Button btnImport;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        btnImport.setEnabled(importFilesDs.getItem() != null);
        importFilesDs.addItemChangeListener(e -> btnImport.setEnabled(e.getItem() != null));
    }

    public void onBtnImportClick() {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

        final FileUploadDialogExt dialog = (FileUploadDialogExt) App.getInstance().getWindowManager().
                openWindow(windowConfig.getWindowInfo("fileUploadDialog"), WindowManager.OpenType.DIALOG);

        final ImportScenario scenario = importFilesDs.getItem();

        dialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileUploadingAPI fileUploading = AppBeans.get(FileUploadingAPI.NAME);
                FileDescriptor descriptor = fileUploading.getFileDescriptor(dialog.getFileId(), dialog.getFileName());
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("writeLog", dialog.isLogSuccessful());
                    fileUploading.putFileIntoStorage(dialog.getFileId(), descriptor);
                    descriptor = dataManager.commit(descriptor);

                    ImportHistory importHistory = metadata.create(ImportHistory.class);
                    importHistory.setImportScenario(scenario);
                    importHistory.setFile(descriptor);
                    importHistory = dataManager.commit(importHistory);
                    importHistory = importerService.doImport(importHistory, params, true);

                    showCompletionMessage(importHistory);

                } catch (FileStorageException e) {
                    log.error("File upload has failed", e);
                    showNotification(getMessage("ImportScenarioBrowse.uploadError"), NotificationType.ERROR);
                }
            }
        });
    }

    private void showCompletionMessage(ImportHistory log) {
        ImportHistory result = dataManager.reload(log, "importHistory.browse");

        long errorCount = result.getRecords().stream().filter(r -> r.getLevel().equals(ImportLogLevel.ERROR)).count();
        long warnCount = result.getRecords().stream().filter(r -> r.getLevel().equals(ImportLogLevel.LOG)).count();
        long successCount = result.getRecords().stream().filter(r -> r.getLevel().equals(ImportLogLevel.SUCCESS)).count();

        if ((errorCount + warnCount + successCount) == 0) {
            showOptionDialog(getMessage("ImportScenarioBrowse.importResult")
                    , getMessage("ImportScenarioBrowse.importSuccess")
                    , MessageType.CONFIRMATION_HTML
                    , new Action[]{
                            new DialogAction(DialogAction.Type.OK, true)
                    });
        } else {
            showOptionDialog(getMessage("ImportScenarioBrowse.importResult")
                    , String.format(getMessage("ImportScenarioBrowse.importError"), errorCount, successCount, warnCount)
                    , MessageType.WARNING_HTML
                    , new Action[]{
                            new DialogAction(DialogAction.Type.OK, true) {
                                @Override
                                public void actionPerform(Component component) {
                                    super.actionPerform(component);
                                    openWindow("tsadv$ImportHistory.browse", WindowManager.OpenType.NEW_TAB,
                                            ParamsMap.of("selectLogItem", result));
                                }
                            }
                            , new DialogAction(DialogAction.Type.CANCEL)
                    });
        }
    }

    public Component generateTemplateCell(ImportScenario entity) {
        if (entity.getTemplate() != null) {
            final ExportDisplay exportDisplay = AppBeans.get(ExportDisplay.NAME);
            Button button = componentsFactory.createComponent(Button.class);
            button.setStyleName("link");
            button.setAction(new AbstractAction("download") {
                @Override
                public void actionPerform(Component component) {
                    exportDisplay.show(entity.getTemplate());
                }

                @Override
                public String getCaption() {
                    return entity.getTemplate().getName();
                }
            });
            return button;
        }

        return null;
    }
}
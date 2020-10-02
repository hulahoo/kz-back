package kz.uco.tsadv.web.talentprogramrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.DicTalentProgramRequestStatus;
import kz.uco.tsadv.entity.TalentProgram;
import kz.uco.tsadv.entity.TalentProgramPersonStep;
import kz.uco.tsadv.entity.TalentProgramRequest;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;
import kz.uco.tsadv.modules.administration.importer.ImportHistory;
import kz.uco.tsadv.modules.administration.importer.ImportLogLevel;
import kz.uco.tsadv.modules.administration.importer.ImportScenario;
import kz.uco.tsadv.service.ImporterService;
import kz.uco.tsadv.web.screens.fileUpload.FileUploadDialogExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class AllTalentProgramRequestBrowse extends AbstractLookup {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected static final String ACTIVE_FILTER = "progress-step-active-filter";

    protected Set<TalentProgramRequest> inviteSet;

    // ------------- Свойства -------------
    protected List<String> queryFilterStatus = new ArrayList<>();
    protected List<String> queryFilterStep = new ArrayList<>();
    protected String defaultQuery;

    @Inject
    protected Datasource<TalentProgram> talentProgramDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CollectionDatasource<DicTalentProgramRequestStatus, UUID> dicTalentProgramRequestStatusesDs;
    @Inject
    protected CollectionDatasource<DicTalentProgramStep, UUID> dicTalentProgramStepsDs;
    @Inject
    protected GroupDatasource<TalentProgramRequest, UUID> talentProgramRequestsDs;
    @Inject
    protected Table<TalentProgramRequest> talentProgramRequestsTable;
    @Inject
    protected CssLayout progressBarStatus;
    @Inject
    protected CssLayout progressBarStep;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Filter filter;
    @Inject
    private DataManager dataManager;
    @Inject
    private ImporterService importerService;
    @Named("talentProgramRequestsTable.invite")
    protected Action talentProgramRequestsTableInvite;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        defaultQuery = talentProgramRequestsDs.getQuery();

        inviteSet = new HashSet<>();
        talentProgramRequestsDs.addCollectionChangeListener(e -> {
            talentProgramRequestsTableInvite.setEnabled(false);
        });

        talentProgramRequestsTable.addGeneratedColumn("personGroup.fioWithEmployeeNumber", this::linkButtonFio);

        dicTalentProgramRequestStatusesDs.addCollectionChangeListener(e -> initProgressBar(progressBarStatus, e.getDs()));
        dicTalentProgramStepsDs.addCollectionChangeListener(e -> initProgressBar(progressBarStep, e.getDs()));

        filter.setBeforeFilterAppliedHandler(() -> {
            talentProgramDs.setItem((TalentProgram) filter.getParamValue("talentProgram15440"));
            dicTalentProgramStepsDs.refresh();
            return true;
        });
    }

    protected <T extends AbstractDictionary> void initProgressBar(CssLayout progressBar, CollectionDatasource<T, UUID> ds) {
        progressBar.removeAll();
        int index = 1;
        for (T dictionary : ds.getItems()) {
            progressBar.add(createProgressBar(index++, dictionary));
        }
        progressBar.setVisible(!CollectionUtils.isEmpty(progressBar.getComponents()));
    }

    protected Component linkButtonFio(TalentProgramRequest talentProgramRequest) {
        LinkButton fioLink = componentsFactory.createComponent(LinkButton.class);
        fioLink.setCaption(talentProgramRequest.getPersonGroup().getFioWithEmployeeNumber());
        fioLink.setAction(new BaseAction("open") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                openWindow("tsadv$TalentProgramRequest.view", WindowManager.OpenType.THIS_TAB, ParamsMap.of("talentProgramRequest", talentProgramRequest));
            }
        });
        return fioLink;
    }

    public Component checkBox(TalentProgramRequest entity) {
        CheckBox box = componentsFactory.createComponent(CheckBox.class);
        box.setValue(inviteSet.contains(entity));
        box.addValueChangeListener(e -> boxValueChangeListener(e, entity));
        return box;
    }

    protected void boxValueChangeListener(HasValue.ValueChangeEvent<Boolean> event, TalentProgramRequest entity) {
        if (Boolean.TRUE.equals(event.getValue())) inviteSet.add(entity);
        else inviteSet.remove(entity);
        talentProgramRequestsTableInvite.setEnabled(!inviteSet.isEmpty());
    }

    @Override
    public void ready() {
        super.ready();
        dicTalentProgramRequestStatusesDs.refresh();
    }

    protected Component createProgressBar(int count, AbstractDictionary dictionary) {
        String dicId = dictionary.getId().toString();

        Label label = componentsFactory.createComponent(Label.class);
        CssLayout wrapper = componentsFactory.createComponent(CssLayout.class);
        label.setValue(count + " " + dictionary.getLangValue());
        label.addStyleName("progress-step-label");
        wrapper.addStyleName("progress-step progress-step-uco");
        wrapper.add(label);

        wrapper.addLayoutClickListener(event -> {
            if (!event.getMouseEventDetails().isDoubleClick()) {
                boolean clicked = wrapper.getStyleName().contains(ACTIVE_FILTER);
                if (clicked) {
                    wrapper.removeStyleName(ACTIVE_FILTER);
                    queryFilterStatus.remove(dicId);
                    queryFilterStep.remove(dicId);
                } else {
                    if (DicTalentProgramRequestStatus.class.isAssignableFrom(dictionary.getClass())) {
                        queryFilterStatus.add(dicId);
                    } else {
                        queryFilterStep.add(dicId);
                    }
                    wrapper.addStyleName(ACTIVE_FILTER);
                }

                reloadTable();
            }
        });

        return wrapper;
    }

    protected void reloadTable() {
        int indexOfOrderBy = defaultQuery.indexOf("order by");
        String parsedQuery = defaultQuery.substring(0, indexOfOrderBy);
        String orderBy = defaultQuery.substring(indexOfOrderBy);
        if (!queryFilterStatus.isEmpty()) {
            String conditionStatus = queryFilterStatus.stream().collect(Collectors.joining("', '", "'", "'"));
            parsedQuery += " and e.currentStepStatus.id in (" + conditionStatus + ") ";
        }

        if (!queryFilterStep.isEmpty()) {
            String conditionStep = queryFilterStep.stream().collect(Collectors.joining("', '", "'", "'"));
            parsedQuery += " and e.currentStep.id in (" + conditionStep + ") ";
        }

        talentProgramRequestsDs.setQuery(parsedQuery + orderBy);
        talentProgramRequestsDs.refresh();
    }

    public void onInvite() {
        if (CollectionUtils.isEmpty(inviteSet)) return;

        TalentProgramPersonStep personStep = metadata.create(TalentProgramPersonStep.class);
        AbstractEditor abstractEditor = openEditor("tsadv$TalentProgramPersonStep.invite", personStep,
                WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("inviteList", inviteSet,
                        "talentProgramId", inviteSet.iterator().next().getTalentProgram().getId()));
        abstractEditor.addCloseListener(actionId -> {
            if (!CLOSE_ACTION_ID.equals(actionId)) showNotification(getMessage("invitation.sent"));
            else {
                inviteSet = new HashSet<>();
                talentProgramRequestsTableInvite.setEnabled(false);
            }
        });
    }

    public void onImportXls() {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

        final FileUploadDialogExt dialog = (FileUploadDialogExt) App.getInstance().getWindowManager().
                openWindow(windowConfig.getWindowInfo("fileUploadDialog"), WindowManager.OpenType.DIALOG);

        dialog.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileUploadingAPI fileUploading = AppBeans.get(FileUploadingAPI.NAME);
                FileDescriptor descriptor = fileUploading.getFileDescriptor(dialog.getFileId(), dialog.getFileName());
                try {
                    fileUploading.putFileIntoStorage(dialog.getFileId(), descriptor);
                    descriptor = dataManager.commit(descriptor);

                    ImportHistory importHistory = metadata.create(ImportHistory.class);
                    importHistory.setImportScenario(getScenario());
                    importHistory.setFile(descriptor);
                    importHistory = dataManager.commit(importHistory);
                    importHistory = importerService.doImport(importHistory, ParamsMap.of("writeLog", dialog.isLogSuccessful()), true);
                    talentProgramRequestsDs.refresh();
                    showCompletionMessage(importHistory);

                } catch (FileStorageException e) {
                    log.error("File upload has failed", e);
                    showNotification(getMessage("ImportScenarioBrowse.uploadError"), NotificationType.ERROR);
                }
            }
        });
    }

    private ImportScenario getScenario() {
        return commonService.getEntity(ImportScenario.class,
                "select e from tsadv$ImportScenario e where e.importerBeanName = 'tsadv_TalentProgramPersonStepImporter'",
                ParamsMap.empty(),
                View.LOCAL);
    }

    private void showCompletionMessage(ImportHistory log) {
        ImportHistory result = dataManager.reload(log, "importHistory.browse");

        long errorCount = result.getRecords().stream().filter(r -> r.getLevel().equals(ImportLogLevel.ERROR)).count();
        long warnCount = result.getRecords().stream().filter(r -> r.getLevel().equals(ImportLogLevel.LOG)).count();

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
                                    openWindow("tsadv$ImportHistory.browse", WindowManager.OpenType.NEW_TAB,
                                            ParamsMap.of("selectLogItem", result));
                                }
                            }
                            , new DialogAction(DialogAction.Type.CANCEL)
                    });
        }
    }

    public void chooseAll() {
        inviteSet.addAll(talentProgramRequestsDs.getItems());
        talentProgramRequestsTable.repaint();
    }
}
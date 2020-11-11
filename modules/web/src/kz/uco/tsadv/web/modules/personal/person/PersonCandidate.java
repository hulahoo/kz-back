package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.theme.ThemeConstantsManager;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.datasource.CandidateJobRequestDatasource;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.modules.personal.model.PersonDocument;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.model.PersonAttachment;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.person.candidateframes.CandidateJobRequest;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueFioDateException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings("all")
public class PersonCandidate extends AbstractHrEditor<PersonExt> {

    protected TabSheet.Tab previewTab;
    protected String defaultTabFromLink;
    protected boolean editAction;
    protected boolean setTabFromCode = false;

    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected Datasource<PersonExt> personDs;
    @Inject
    protected Frame windowActions;
    @Inject
    protected Image userImage;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected CollectionDatasource<PersonContact, UUID> personContactsDs;
    @Inject
    protected CollectionDatasource<PersonDocument, UUID> personDocumentsDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    public CollectionDatasource<CompetenceElement, UUID> competenceElementsDs;
    @Inject
    protected TabSheet tabSheet;
    @WindowParam
    protected String personTypeCode;
    @WindowParam
    protected String redirectTab;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CandidateJobRequest jobRequestFrameId;
    @Inject
    protected Datasource<PersonGroupExt> personGroupDs;
    @Inject
    private CandidateJobRequestDatasource jobRequestsDs;
//    @Named("personDocumentsTable.create")
//    protected CreateAction personDocumentsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setShowSaveNotification(false);

        initImageUpload();

        previewTab = tabSheet.getTab();

        tabSheet.addSelectedTabChangeListener((a) -> {
            tabListenerMethod(a);
        });
        if (params.containsKey("openTab")) {
            defaultTabFromLink = (String) params.get("openTab");
        }
        if (params.containsKey("editAction")) {
            editAction = ((Boolean) params.get("editAction"));
        }
    }

    public void tabListenerMethod(TabSheet.SelectedTabChangeEvent a) {

        if (!setTabFromCode && getDsContext().isModified()) {
            showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                    messages.getMainMessage("saveUnsaved"),
                    MessageType.WARNING,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                    .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                    .withHandler(e -> {
                                whenChangeTab(a.getSelectedTab().getName());
                                commit();
                                previewTab = tabSheet.getTab();
                            }),
                            new BaseAction("discard")
                                    .withIcon(AppBeans.get(ThemeConstantsManager.class).getThemeValue("actions.dialog.Cancel.icon"))
                                    .withCaption(messages.getMainMessage("closeUnsaved.discard"))
                                    .withHandler(e -> {
                                whenChangeTab(a.getSelectedTab().getName());
                                resetDsContext();
                                previewTab = tabSheet.getTab();
                            }),
                            new DialogAction(DialogAction.Type.CANCEL) {
                                @Override
                                public void actionPerform(Component component) {
                                    setTabFromCode = true;
                                    tabSheet.setTab(previewTab);
                                }
                            }
                    });
        } else {
            whenChangeTab(a.getSelectedTab().getName());
            previewTab = tabSheet.getTab();
            setTabFromCode = false;
        }
    }

    public void whenChangeTab(String tabName) {
        Map<String, Object> competenceParams = new HashMap<>();
        competenceParams.put("recruitmentFilter", Boolean.TRUE);
        if ("competences".equals(tabName)) {
            List<CompetenceGroup> competenceGroups = new ArrayList<>();
            competenceElementsDs.refresh();
            competenceElementsDs.getItems().forEach(competenceElement1 -> competenceGroups.add(competenceElement1.getCompetenceGroup()));
            competenceParams.put("alreadyExistCompetence", competenceGroups);
            Table competencesTable = (Table) getComponent("personCompetencesTable");
            ((CreateAction) competencesTable.getAction("create")).setWindowParams(competenceParams);
            ((EditAction) competencesTable.getAction("edit")).setWindowParams(competenceParams);
        }
        if ("addresses".equals(tabName)) {
            Table tableAddress = (Table) getComponent("addressesTable");
            Map map = new HashMap();
            map.put("personGroup", getDsContext().get("personGroupDs").getItem());
            ((CreateAction) tableAddress.getAction("create")).setWindowParams(map);
        }
        if ("mainData".equals(tabName))
            windowActions.setVisible(true);
        else
            windowActions.setVisible(false);

        if ("documents".equals(tabName)) {
//            if (getItem() != null) {
//                if (getItem().getGroup() != null) {
//                    personDocumentsTableCreate.setInitialValuesSupplier(() ->
//                            Collections.singletonMap("personGroup", getItem().getGroup())
//                    );
//                } else {
//                    showNotification(getMessage("personGroupIsNull"));
//                }
//            }
            Map map = new HashMap();
            map.put("personGroup", getDsContext().get("personGroupDs").getItem());

            ((CreateAction) ((Table) getComponent("personDocumentsTable")).getAction("create")).setWindowParams(map);
        }
        if ("jobRequests".equals(tabName)) {
            ((CreateAction) ((Table) jobRequestFrameId.getComponent("jobRequestsTable")).getAction("create"))
                    .setInitialValues(Collections.singletonMap("candidatePersonGroup", getDsContext().get("personGroupDs").getItem()));
        }
    }

    protected void resetDsContext() {
        for (Datasource datasource : getDsContext().getAll()) {
            ((AbstractDatasource) datasource).setModified(false);
        }
        getDsContext().refresh();
    }

    protected void hasCandidate(PersonExt person) throws Exception {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);

        boolean isCreate = PersistenceHelper.isNew(person);

        StringBuilder query = new StringBuilder("select e from base$PersonExt e where (:sysDate between e.startDate and e.endDate)");

        LoadContext.Query loadContextQuery = LoadContext.createQuery("");

        String iin = person.getNationalIdentifier();

        boolean checkIin = false;
        if (iin != null && iin.trim().length() > 0 && !iin.equals("000000000000")) {
            checkIin = true;
            query.append(" and e.nationalIdentifier = :iin");
            loadContextQuery.setParameter("iin", iin);
        }

        if (isCreate && !checkIin) {
            query.append(" and e.firstName = :p1 and e.lastName = :p2 and e.dateOfBirth = :p3");
            loadContextQuery.setParameter("p1", person.getFirstName());
            loadContextQuery.setParameter("p2", person.getLastName());
            loadContextQuery.setParameter("p3", person.getDateOfBirth());
        }

        if (!isCreate) {
            query.append(" and e.id <> :pId");
            loadContextQuery.setParameter("pId", person.getId());
        }
        loadContextQuery.setParameter("sysDate", new Date());
        loadContextQuery.setQueryString(query.toString());
        loadContext.setQuery(loadContextQuery);

        if (dataManager.getCount(loadContext) > 0) {
            //if (checkIin) throw new UniqueIinException(getMessage("candidate.exist.iin"));

            /**
             * check unique FIO and dateOfBirth only create form
             * */
            if (isCreate) {
                throw new UniqueFioDateException(getMessage("candidate.exist.fid"));
            }
        }
    }

    @Override
    public void commitAndClose() {
        if (!validateAll()) {
            return;
        }

        try {
            hasCandidate(getItem());

            commitChanges();
        } catch (UniqueFioDateException ex) {
            showOptionDialog(getMessage("msg.warning.title"),
                    getMessage(ex.getMessage()),
                    MessageType.CONFIRMATION,
                    new DialogAction[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    commitChanges();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
        }
    }

    protected void commitChanges() {
        boolean changed = false;

        if (PersistenceHelper.isNew(getItem())) {
            changed = super.commit();
        } else {
            changed = true;
            commit();

            postInit();
        }

        if (changed) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
    }

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        if (!PersistenceHelper.isNew(getItem())) {
            setCaption(getItem().getFioWithEmployeeNumber());
        }

        FileDescriptor userImageFile = getItem().getImage();
        if (userImageFile == null) {
            userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        } else {
            userImage.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
        }

        boolean isStudent = false;
        if (personTypeCode != null && personTypeCode.equalsIgnoreCase("student")) {
            isStudent = true;
        }

        initVisibleTabs(PersistenceHelper.isNew(getItem()), isStudent);

        if (redirectTab != null) {
            TabSheet.Tab tab = tabSheet.getTab(redirectTab);
            if (tab != null) tabSheet.setTab(tab);
        }
        if (StringUtils.isNotEmpty(defaultTabFromLink)) {
            tabSheet.setSelectedTab(defaultTabFromLink);
        }
    }

    protected void initVisibleTabs(boolean isCreate, boolean isStudent) {
        for (TabSheet.Tab tab : tabSheet.getTabs()) {
            if (!tab.getName().equalsIgnoreCase("mainData")) {
                tab.setVisible(!isCreate);

                if (tab.getName().equalsIgnoreCase("studentGrants")) {
                    tab.setVisible(isStudent && !isCreate);
                }
            }
        }
    }

    @Override
    protected void initNewItem(PersonExt item) {
        super.initNewItem(item);
        item.setType(getPersonTypeByCode(personTypeCode != null ? personTypeCode.toUpperCase() : "CANDIDATE"));
    }

    protected void initImageUpload() {
        imageUpload.addFileUploadSucceedListener(event -> {
            PersonExt person = getItem();
            if (person != null && imageUpload.getFileContent() != null) {
                try {
                    FileDescriptor fd = imageUpload.getFileDescriptor();
                    fileUploadingAPI.putFileIntoStorage(imageUpload.getFileId(), fd);

                    FileDescriptor committedImage = dataSupplier.commit(fd);
                    userImage.setSource(FileDescriptorResource.class).setFileDescriptor(committedImage);

                    person.setImage(committedImage);
                    ((AbstractDatasource) personDs).modified(person);
                } catch (Exception e) {
                    showNotification(getMessage("fileUploadErrorMessage"), NotificationType.ERROR);
                }
            }
            imageUpload.setValue(null);
        });

        imageUpload.addBeforeValueClearListener(beforeEvent -> {
            beforeEvent.preventClearAction();
            showOptionDialog("", "Are you sure you want to delete this photo?", MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                                public void actionPerform(Component component) {
                                    FileDescriptor currentImage = getItem().getImage();
                                    if (currentImage != null) {
                                        try {
                                            fileUploadingAPI.deleteFile(currentImage.getId());
                                        } catch (FileStorageException e) {
                                            e.printStackTrace();
                                        }

                                        getItem().setImage(null);
                                        ((AbstractDatasource) personDs).modified(getItem());
                                    }

                                    userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });
    }

    public Component getPersonAttachmentDownloadBtn(PersonAttachment entity) {
        return Utils.getFileDownload(entity.getAttachment(), PersonCandidate.this);
    }

    protected DicPersonType getPersonTypeByCode(String code) {
        LoadContext<DicPersonType> loadContext = LoadContext.create(DicPersonType.class)
                .setQuery(LoadContext.createQuery(
                        "select e from tsadv$DicPersonType e " +
                                "where e.code = :code")
                        .setParameter("code", code));
        return dataManager.load(loadContext);
    }



 /*   public void createCompetence(Component source) {
        CompetenceElement competenceElement = metadata.create(CompetenceElement.class);
        competenceElement.setPersonGroup(personDs.getItem().getGroup());
        Map<String, Object> params = new HashMap<>();


        Window.Editor editor = openEditor("tsadv$CompetenceElement.edit", competenceElement, WindowManager.OpenType.DIALOG, params);
        editor.addCloseWithCommitListener(() -> elementDs.refresh());
    }*/
}
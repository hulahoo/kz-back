package kz.uco.tsadv.web.modules.personal.beneficiary;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.model.Beneficiary;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.RelationshipTypeBeneficiary;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BeneficiaryView extends AbstractEditor<Beneficiary> {
    @Inject
    protected Datasource<PersonExt> personDs;
    @Inject
    protected Image userImage;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected Datasource<Beneficiary> beneficiaryDs;
    @Inject
    protected FieldGroup beneficiaryFieldGroup;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionDatasource<Beneficiary, UUID> beneficiariesDs;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected DataSupplier dataSupplier;
    @Named("personContactsTable.create")
    protected CreateAction personContactsTableCreate;
    @Named("personDocumentsTable.create")
    protected CreateAction personDocumentsTableCreate;

    @SuppressWarnings("all")
    @Override
    public void init(Map<String, Object> params) {
        this.setShowSaveNotification(false);
        personContactsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("personGroup", getItem().getPersonGroupChild()));
        personDocumentsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("personGroup", getItem().getPersonGroupChild()));
        if (params.containsKey("fromSS") && (boolean) params.get("fromSS")) {
            fieldGroup.getFieldNN("type").setEditable(false);
        }
        initImageUpload();
    }

    protected void initImageUpload() {
        imageUpload.setUploadButtonCaption("");
        imageUpload.setClearButtonCaption("");
        imageUpload.addFileUploadSucceedListener(event -> {
            PersonExt person = personDs.getItem();
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
                                    FileDescriptor currentImage = personDs.getItem().getImage();
                                    if (currentImage != null) {
                                        try {
                                            fileUploadingAPI.deleteFile(currentImage.getId());
                                        } catch (FileStorageException e) {
                                            e.printStackTrace();
                                        }

                                        personDs.getItem().setImage(null);
                                        ((AbstractDatasource)personDs).modified(personDs.getItem());
                                    }

                                    userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });
    }

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        if (beneficiaryDs.getItem().getPersonGroupChild().getPerson().getType().getCode().equals("BENEFICIARY")) {
        } else {
            fieldGroup.setEditable(false);
            imageUpload.setEditable(false);
        }
        PersonExt personExt = personDs.getItem();
        FileDescriptor userImageFile = personExt != null ? personExt.getImage() : null;
        if (userImageFile == null) {
            userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        } else {
            userImage.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
        }
    }

    @Override
    public void ready() {
        LoadContext<RelationshipTypeBeneficiary> loadContext = LoadContext.create(RelationshipTypeBeneficiary.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$RelationshipTypeBeneficiary e"))
                .setView("relationshipTypeBeneficiaryView");
        List<RelationshipTypeBeneficiary> relationshipTypeBeneficiaryArrayList = dataManager.loadList(loadContext);
        LoadContext<Beneficiary> loadContext1 = loadContext.create(Beneficiary.class);
        loadContext1.setQuery(LoadContext.createQuery("select e from tsadv$Beneficiary e " +
                "where e.personGroupParent.id = :childPersonGroupId and e.personGroupChild.id = :parentPersonGroupId")
                .setParameter("childPersonGroupId", beneficiaryDs.getItem().getPersonGroupChild().getId())
                .setParameter("parentPersonGroupId", beneficiaryDs.getItem().getPersonGroupParent().getId()))
                .setView("beneficiaryView");
        List<Beneficiary> list = dataManager.loadList(loadContext1);
        Beneficiary beneficiary = list.isEmpty() ? null : list.get(0);
        ((LookupField) beneficiaryFieldGroup.getField("personGroupField").getComponent()).addValueChangeListener(e -> {
            if (beneficiary != null) {
                for (RelationshipTypeBeneficiary relationshipTypeBeneficiary : relationshipTypeBeneficiaryArrayList) {
                    if (relationshipTypeBeneficiary.getChild().equals((beneficiaryFieldGroup.getField("personGroupField")).getOptionsDatasource().getItem())
                            | relationshipTypeBeneficiary.getParent().equals((beneficiaryFieldGroup.getField("personGroupField")).getOptionsDatasource().getItem())) {
                        if (relationshipTypeBeneficiary.getChild().equals((beneficiaryFieldGroup.getField("personGroupField")).getOptionsDatasource().getItem())) {
                            beneficiary.setRelationshipType(relationshipTypeBeneficiary.getParent());
                        } else
                            beneficiary.setRelationshipType(relationshipTypeBeneficiary.getChild());
                        break;
                    } else {
                        if (relationshipTypeBeneficiary.getChild().equals((beneficiaryFieldGroup.getField("personGroupField")).getOptionsDatasource().getItem())) {
                            beneficiary.setRelationshipType(relationshipTypeBeneficiary.getParent());
                        } else
                            beneficiary.setRelationshipType(relationshipTypeBeneficiary.getChild());
                    }
                }
                beneficiariesDs.modifyItem(beneficiary);
            }
        });

        //getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(personDs.getItem()));
        super.ready();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        return super.postCommit(committed, close);
    }

}
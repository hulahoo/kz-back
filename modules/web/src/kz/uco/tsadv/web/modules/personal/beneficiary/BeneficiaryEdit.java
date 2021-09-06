package kz.uco.tsadv.web.modules.personal.beneficiary;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicKato;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Beneficiary;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.RelationshipTypeBeneficiary;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class BeneficiaryEdit extends AbstractEditor<Beneficiary> {

    protected String KZ_COUNTRY_CODE = "1";
    protected PersonExt person;
    protected PersonGroupExt personGroupChild;
    protected Beneficiary personBeneficiary;
    protected CommonConfig nationalIdentifierCheckConfig;
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected Datasource<PersonExt> personDs;
    @Inject
    private Image userImage;
    @Inject
    protected FileUploadField imageUpload;
    @Inject
    protected Datasource<Beneficiary> beneficiaryDs;
    @Inject
    protected PickerField<Entity> personGroupPickerField;
    @Inject
    protected FieldGroup fieldGroupPerson;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Configuration configuration;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FieldGroup fieldGroupPersonName;
    @Named("fieldGroupPerson.country")
    private PickerField<DicCountry> countryField;
    @Named("fieldGroupPerson.addressKATOCode")
    private PickerField<DicKato> addressKATOCodeField;

    @Override
    protected void initNewItem(Beneficiary item) {
        super.initNewItem(item);
        item.setDateFrom(CommonUtils.getSystemDate());
    }

    protected void setNewPersonGroup() {
        personGroupChild = metadata.create(PersonGroupExt.class);
        person = metadata.create(PersonExt.class);
        person.setGroup(personGroupChild);
        person.setStartDate(CommonUtils.getSystemDate());
        person.setEndDate(CommonUtils.getEndOfTime());
        List<PersonExt> personList = new ArrayList<>();
        personList.add(person);
        LoadContext<DicPersonType> loadContext = LoadContext.create(DicPersonType.class)
                .setQuery(
                        LoadContext.createQuery("select e from tsadv$DicPersonType e where e.code = :code")
                                .setParameter("code", "BENEFICIARY")
                );
        DicPersonType type = dataManager.load(loadContext);
        person.setType(type);
        personGroupChild.setList(personList);
        getItem().setPersonGroupChild(personGroupChild);
    }

    protected void setRelationshipType() {
        LoadContext<RelationshipTypeBeneficiary> loadContext = LoadContext.create(RelationshipTypeBeneficiary.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$RelationshipTypeBeneficiary e"))
                .setView("relationshipTypeBeneficiaryView");
        List<RelationshipTypeBeneficiary> relationshipTypeBeneficiaryArrayList = dataManager.loadList(loadContext);
        for (RelationshipTypeBeneficiary relationshipTypeBeneficiary : relationshipTypeBeneficiaryArrayList) {
            if (relationshipTypeBeneficiary.getChild().getLangValue().equals(beneficiaryDs.getItem().getRelationshipType().getLangValue())) {
                personBeneficiary.setRelationshipType(relationshipTypeBeneficiary.getParent());
                break;
            } else {
                if (relationshipTypeBeneficiary.getParent().getLangValue().equals(beneficiaryDs.getItem().getRelationshipType().getLangValue())) {
                    personBeneficiary.setRelationshipType(relationshipTypeBeneficiary.getChild());
                    break;
                }
            }
        }
    }

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem()))
            setNewPersonGroup();
        setImage(personDs.getItem());
    }

    protected void setImage(PersonExt person) {
        FileDescriptor userImageFile = person != null ?
                person.getImage() : null;
        if (userImageFile == null) {
            userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        } else {
            userImage.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
        }
    }

    @SuppressWarnings("all")
    @Override
    public void init(Map<String, Object> params) {

        FieldGroup.FieldConfig config = fieldGroupPerson.getField("personType");
        config.setEditable(false);
        personGroupPickerField.addValueChangeListener(e -> {
            PersonGroupExt personGroup = (PersonGroupExt) e.getValue();
            boolean isPersonGroupNull = personGroup == null;

            if (!isPersonGroupNull) {
                personGroup = dataManager.reload(personGroup, "personGroupBeneficiary");
                getItem().setPersonGroupChild(personGroup);
                setImage(personGroup.getPerson());
                setImage(personGroup.getPerson());
            } else {
                getItem().setPersonGroupChild(personGroupChild);
            }

            imageUpload.setVisible(isPersonGroupNull);
            fieldGroupPerson.setEditable(isPersonGroupNull);
            fieldGroupPersonName.setEditable(isPersonGroupNull);
        });
        nationalIdentifierCheckConfig = configuration.getConfig(CommonConfig.class);

        getDsContext().addBeforeCommitListener(context -> {

            if (getItem().getPersonGroupChild() != null && !isPersonExists(getItem().getPersonGroupChild())) {

                if (person != null) {
                    context.getCommitInstances().add(person);
                }
                if (personGroupChild != null) {
                    context.getCommitInstances().add(personGroupChild);
                }
            }
        });

        initImageUpload();
    }

    @Override
    public void ready() {
        super.ready();
        autoWriteNameLatin();
        countryField.addValueChangeListener(dicCountryValueChangeEvent -> {
            if (dicCountryValueChangeEvent.getValue() == null) {
                addressKATOCodeField.setRequired(false);
                return;
            }
            DicCountry dicCountry = dicCountryValueChangeEvent.getValue();
            if(dicCountry.getCode().equals(KZ_COUNTRY_CODE)) addressKATOCodeField.setRequired(true);
        });
    }

    protected void autoWriteNameLatin() {
        final List<String> propertyList = Arrays.asList("lastName", "firstName", "middleName");

        personDs.addItemPropertyChangeListener(e -> {
            if (propertyList.contains(e.getProperty())) {
                String propertyLantin = e.getProperty() + "Latin";
                TextField nameLantin = (TextField) fieldGroupPersonName.getFieldNN(propertyLantin).getComponentNN();
                if (nameLantin.getValue() == null ||
                        e.getPrevValue() != null && nameLantin.getValue().equals(e.getPrevValue())) {
                    nameLantin.setValue(e.getValue());
                }
            }
        });
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
                                    }

                                    userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem()) && !getItem().getPersonGroupChild().getId().equals(personGroupChild.getId())) {
            dataManager.remove(personGroupChild);
        }
        return super.preCommit();
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (nationalIdentifierCheckConfig.getNationalIdentifierCheckEnabled()) {
            TextField<String> nationalIdentifier = (TextField) fieldGroupPerson.getField("nationalIdentifier").getComponent();
            if (nationalIdentifier.getValue() != null) {
                if (nationalIdentifier.getValue().equals("000000000000")) {
                } else {
                    DateField dateOfBirth = (DateField) fieldGroupPerson.getField("dateOfBirth").getComponent();
                    String s = nationalIdentifier.getValue();
                    if (s.length() == 12) {
                        if (s.substring(0, 6).equals(dateFormat.format(dateOfBirth.getValue()).toString())) {
                            int sex = parseInt(s.substring(6, 7));
                            String sexCode;
                            if (((sex % 2) == 1)) {
                                sexCode = "M";
                            } else
                                sexCode = "F";
                            if (beneficiaryDs.getItem().getPersonGroupChild().getPerson().getSex().getCode().equals(sexCode)) {
                                int[] b1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
                                int[] b2 = {3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 2};
                                int[] a = new int[12];
                                int controll = 0;
                                for (int i = 0; i < 12; i++) {
                                    a[i] = parseInt(s.substring(i, i + 1));
                                    if (i < 11)
                                        controll += a[i] * b1[i];
                                }
                                controll = controll % 11;
                                if (controll == 10) {
                                    controll = 0;
                                    for (int i = 0; i < 11; i++) {
                                        controll += a[i] * b2[i];
                                        controll = controll % 11;
                                    }
                                }
                                if (controll != a[11]) {
                                    errors.add(getMessage("iinValid"));
                                }
                            } else {
                                errors.add(getMessage("iinValid"));
                            }
                        } else
                            errors.add(getMessage("iinValid"));
                    } else
                        errors.add(getMessage("iinValid"));
                }
            }
        }

    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            personBeneficiary = metadata.create(Beneficiary.class);
            Beneficiary beneficiary = beneficiaryDs.getItem();
            personBeneficiary.setPersonGroupParent(beneficiary.getPersonGroupChild());
            personBeneficiary.setPersonGroupChild(beneficiary.getPersonGroupParent());
            personBeneficiary.setDateFrom(beneficiary.getDateFrom());
            personBeneficiary.setCountry(beneficiary.getCountry());
            personBeneficiary.setAddressType(beneficiary.getAddressType());
            personBeneficiary.setStreetType(beneficiary.getStreetType());

            setRelationshipType();
            dataManager.commit(personBeneficiary);

            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
            if (!close) {
                postInit();
            }
        }

        return true;
    }

    /**
     * Проверяет, нет ли в базе аналогичной записи PersonGroupExt
     *
     * @param personGroupChild объект PersonGroupExt
     * @return true если переданный параметр равняется объекту, полученному из базы
     */
    private boolean isPersonExists(PersonGroupExt personGroupChild) {
        if (personGroupChild == null) {
            return false;
        }
        String queryString = "SELECT e from base$PersonGroupExt e where e.id = :personGroupChildId";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupChildId", personGroupChild.getId());
        List<PersonGroupExt> list = commonService.getEntities(PersonGroupExt.class, queryString, params, null);
        return !list.isEmpty();
    }

}
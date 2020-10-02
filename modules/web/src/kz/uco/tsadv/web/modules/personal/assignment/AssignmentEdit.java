package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.EmployeNumberOf;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.personal.model.PositionStructure;
import kz.uco.tsadv.modules.recruitment.config.CalcEndTrialPeriod;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueFioDateException;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueIinException;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class AssignmentEdit extends AbstractEditor<AssignmentExt> {

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    @Inject
    protected Metadata metadata;

    @Inject
    protected FileUploadingAPI fileUploadingAPI;

    @Inject
    protected FileUploadField imageUpload;

    @Inject
    protected Image userImage;

    @Inject
    protected Datasource<PersonExt> personDs;

    @Inject
    protected DataSupplier dataSupplier;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Datasource<AssignmentExt> assignmentDs;

    @Inject
    protected CommonService commonService;

    @Inject
    protected FieldGroup fieldGroupIdentification;

    @Named("fieldGroup.type")
    protected PickerField personType;
    @Named("fieldGroup.hireDate")
    protected DateField<Date> hireDate;
    @Named("fieldGroup2.probationEndDate")
    protected DateField<Date> probationEndDate;
    @Named("fieldGroup2.durationProbationPeriod")
    protected TextField<Integer> durationProbationPeriod;
    @Inject
    protected FieldGroup fieldGroup2;
    @Named("fieldGroup2.unit")
    protected LookupField unit;
    protected CalcEndTrialPeriod calcEndTrialPeriod;
    @Inject
    protected Configuration configuration;
    @Inject
    protected EmployeNumberOf employeNumberOf;
    @Inject
    protected EmployeeConfig employeeConfig;


    @Override
    protected void initNewItem(AssignmentExt item) {
        super.initNewItem(item);

        PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
        PersonExt person = metadata.create(PersonExt.class);
        person.setGroup(personGroup);
        if(this.employeeConfig.getGenerateEmployeeNumber()){
            this.employeNumberOf.accept(person);
        }
        person.setHireDate(CommonUtils.getSystemDate());
        person.setStartDate(CommonUtils.getSystemDate());
        person.setEndDate(CommonUtils.getEndOfTime());
        List<PersonExt> personList = new ArrayList<>();
        personList.add(person);
        personGroup.setList(personList);

        item.setPersonGroup(personGroup);

        PositionGroupExt positionGroup = dataSupplier.reload(item.getPositionGroup(), "positionGroup.list");
        item.setGradeGroup(positionGroup.getPosition().getGradeGroup());
        item.setJobGroup(positionGroup.getPosition().getJobGroup());
        item.setLocation(positionGroup.getPosition().getLocation());

        LoadContext<PositionExt> positionExtLoadContext = LoadContext.create(PositionExt.class)
                .setQuery(LoadContext.createQuery("select e " +
                        " from base$PositionExt e " +
                        " where e.group.id = :positionGroupId " +
                        " and :hireDateAndSystemDate between e.startDate and e.endDate ")
                        .setParameter("positionGroupId", positionGroup.getId())
                        .setParameter("hireDateAndSystemDate", personDs.getItem() != null ? personDs.getItem().getHireDate() : CommonUtils.getSystemDate()))
                .setView("position-view");
        PositionExt positionExt = dataManager.load(positionExtLoadContext);
        OrganizationGroupExt organizationGroup = metadata.create(OrganizationGroupExt.class);
        organizationGroup.setId(positionExt.getOrganizationGroupExt().getId());
        item.setOrganizationGroup(organizationGroup);

        item.setAssignDate(person.getHireDate());
        item.setPrimaryFlag(Boolean.TRUE);
    }

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        PersonExt person = personDs.getItem();
        FileDescriptor userImageFile = person.getImage();
        if (userImageFile == null) {
            userImage.setSource(ThemeResource.class).setPath(StaticVariable.DEFAULT_USER_IMAGE_PATH);
        } else {
            userImage.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
        }
        DicAssignmentStatus dicAssignmentStatus = commonService.emQuerySingleRelult(DicAssignmentStatus.class, "select e from tsadv$DicAssignmentStatus e where e.code='ACTIVE'", null);
        assignmentDs.getItem().setAssignmentStatus(dicAssignmentStatus);
        DicPersonType dicPersonType = commonService.getEntity(DicPersonType.class, "EMPLOYEE");
        if (dicPersonType!=null) {
            personType.setValue(dicPersonType);
        }
        calcEndTrialPeriod = configuration.getConfig(CalcEndTrialPeriod.class);
        durationProbationPeriod.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (unit.getValue() != null && hireDate.getValue()!=null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods)unit.getValue()).getId()));
                }
            }
        });

        unit.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (durationProbationPeriod.getValue() != null && hireDate.getValue()!=null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods)unit.getValue()).getId()));
                }
            }
        });

        hireDate.addValueChangeListener(e -> {
            if (calcEndTrialPeriod.getCalcCountProbationPeriod()) {
                if (durationProbationPeriod.getValue() != null && unit.getValue()!=null && hireDate.getValue()!=null) {
                    probationEndDate.setValue(getCountProbationPeriod(hireDate.getValue(), durationProbationPeriod.getValue(), ((HS_Periods)unit.getValue()).getId()));
                }
            }
        });
    }

    protected Date getCountProbationPeriod(Date date, int count, int units) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        if (units == 10) {
            instance.add(Calendar.DAY_OF_WEEK, count);
        } else if (units == 20) {
            instance.add(Calendar.WEEK_OF_MONTH, count);
        } else if (units == 30) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 3);
            }
        } else if (units == 40) {
            instance.add(Calendar.MONTH, count);
        } else if (units == 50) {
            for (int i = 0; i < count; ) {
                i++;
                instance.add(Calendar.MONTH, 6);
            }
        } else if (units == 60) {
            instance.add(Calendar.YEAR, count);
        }
        instance.add(Calendar.DAY_OF_WEEK, -1);
        return instance.getTime();
    }

    @Override
    protected boolean preCommit() {
        getItem().setStartDate(getItem().getAssignDate());
        PersonExt person = personDs.getItem();

        if (person != null) {
            person.setStartDate(getItem().getAssignDate());
            person.setEndDate(CommonUtils.getEndOfTime());
        }
        return super.preCommit();
    }

    @SuppressWarnings("all")
    @Override
    public void init(Map<String, Object> params) {
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
    public void commitAndClose() {
        if (!validateAll()) {
            return;
        }

        try {
            PersonExt person = personDs.getItem();
            if (person != null) {
                hasCandidate(person);
                super.commitAndClose();
            }
        } catch (UniqueFioDateException ex) {
            showOptionDialog(getMessage("msg.warning.title"),
                    getMessage(ex.getMessage()),
                    MessageType.CONFIRMATION,
                    new DialogAction[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    AssignmentEdit.super.commitAndClose();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
        }
    }

    protected void hasCandidate(PersonExt person) throws Exception {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);

        StringBuilder query = new StringBuilder("select e from base$PersonExt e where (:sysDate between e.startDate and e.endDate)");

        LoadContext.Query loadContextQuery = LoadContext.createQuery("");

        String iin = person.getNationalIdentifier();

        boolean checkIin = false;
        if (iin != null && iin.trim().length() > 0 && !iin.equals("000000000000")) {
            checkIin = true;
            query.append(" and e.nationalIdentifier = :iin");
            loadContextQuery.setParameter("iin", iin);
        } else {
            query.append(" and e.firstName = :p1 and e.lastName = :p2 and e.dateOfBirth = :p3");
            loadContextQuery.setParameter("p1", person.getFirstName());
            loadContextQuery.setParameter("p2", person.getLastName());
            loadContextQuery.setParameter("p3", person.getDateOfBirth());
        }

        loadContextQuery.setParameter("sysDate", new Date());
        loadContextQuery.setQueryString(query.toString());
        loadContext.setQuery(loadContextQuery);

        if (dataManager.getCount(loadContext) > 0) {
            //if (checkIin) throw new UniqueIinException(getMessage("candidate.exist.iin"));

            throw new UniqueFioDateException(getMessage("candidate.exist.fid"));
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        TextField<String> nationalIdentifier = (TextField) fieldGroupIdentification.getField("nationalIdentifier").getComponent();
        String iin = nationalIdentifier.getValue();
        if (iin != null && !iin.equals("000000000000")) {
            Map<String, Object> map = new HashMap<>();
            map.put("nationalIdentifier", assignmentDs.getItem().getPersonGroup().getPerson().getNationalIdentifier());
            Long l = commonService.emQuerySingleRelult(Long.class, "select count(e.nationalIdentifier) from base$PersonExt e where e.nationalIdentifier = :nationalIdentifier", map);
            if (l == 0) {
                DateField dateOfBirth = (DateField) fieldGroupIdentification.getField("dateOfBirth").getComponent();
                String s = nationalIdentifier.getValue();
                if (s.length() == 12) {
                    if (s.substring(0, 6).equals(dateFormat.format(dateOfBirth.getValue()).toString())) {
                        int sex = parseInt(s.substring(6, 7));
                        String sexCode;
                        if (((sex % 2) == 1)) {
                            sexCode = "M";
                        } else
                            sexCode = "F";
                        if (assignmentDs.getItem().getPersonGroup().getPerson().getSex().getCode().equals(sexCode)) {
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
            } else
                errors.add(getMessage("iinCopy"));
        }
    }


}
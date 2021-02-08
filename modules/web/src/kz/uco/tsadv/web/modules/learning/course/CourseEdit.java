package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.common.IMAGE_SIZE;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSchedule;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.io.IOException;

@UiController("tsadv$Course.edit")
@UiDescriptor("course-edit.xml")
@EditedEntityContainer("courseDc")
public class CourseEdit extends StandardEditor<Course> {

    @Inject
    protected FileUploadField imageUpload;

    @Inject
    protected Embedded courseImage;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected InstanceContainer<Course> courseDc;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected InstanceLoader<Course> courseDl;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected CollectionContainer<Enrollment> enrollmentDc;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionLoader<Enrollment> enrollmentDl;
    @Inject
    protected CollectionPropertyContainer<CourseSchedule> courseScheduleDc;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        courseDl.load();
        enrollmentDl.setParameter("course", courseDc.getItem());
        enrollmentDl.load();
    }


    @Subscribe
    protected void onInitEntity(InitEntityEvent<Course> event) {
//        PickerField.LookupAction lookupAction = partyField.getLookupAction();
//        lookupAction.setLookupScreen("base$PartyExt.browse");
//        lookupAction.setLookupScreenParams(ParamsMap.of(PartyExtBrowse.TRAINING_PROVIDER, true));


        imageUpload.setUploadButtonCaption("");
        imageUpload.setClearButtonCaption("");

        imageUpload.addFileUploadSucceedListener(this::fileUploadSucceed);

        imageUpload.addBeforeValueClearListener(this::beforeValueClearPerformed);

//        preRequisitionTable.addAction(new BaseAction("selectPreRequisition") {
//            @Override
//            public void actionPerform(Component component) {
//                openLookup("tsadv$Course.browse", new Lookup.Handler() {
//                    @Override
//                    public void handleLookup(Collection items) {
//                        for (Course selectedCourse : (Collection<Course>) items) {
//                            CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
//                            coursePreRequisition.setCourse(courseDs.getItem());
//                            coursePreRequisition.setRequisitionCourse(selectedCourse);
//                            preRequisitionDs.addItem(coursePreRequisition);
//                        }
//                    }
//                }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
//                    StringBuilder sb = new StringBuilder("'" + courseDs.getItem().getId() + "',");
//                    for (CoursePreRequisition coursePreRequisition : preRequisitionDs.getItems()) {
//                        sb.append("'").append(coursePreRequisition.getRequisitionCourse().getId()).append("',");
//                    }
//                    put(StaticVariable.EXIST_COURSE, sb.toString().substring(0, sb.toString().length() - 1));
//                }});
//            }
//        });
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        Course course = courseDc.getItem();
        Utils.getCourseImageEmbedded(course, null, courseImage);
    }


//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//
//        PickerField.LookupAction lookupAction = partyField.getLookupAction();
//        lookupAction.setLookupScreen("base$PartyExt.browse");
//        lookupAction.setLookupScreenParams(ParamsMap.of(PartyExtBrowse.TRAINING_PROVIDER, true));
//
//
//        imageUpload.setUploadButtonCaption("");
//        imageUpload.setClearButtonCaption("");
//
//        imageUpload.addFileUploadSucceedListener(this::fileUploadSucceed);
//
//        imageUpload.addBeforeValueClearListener(this::beforeValueClearPerformed);
//
//        preRequisitionTable.addAction(new BaseAction("selectPreRequisition") {
//            @Override
//            public void actionPerform(Component component) {
//                openLookup("tsadv$Course.browse", new Lookup.Handler() {
//                    @Override
//                    public void handleLookup(Collection items) {
//                        for (Course selectedCourse : (Collection<Course>) items) {
//                            CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
//                            coursePreRequisition.setCourse(courseDs.getItem());
//                            coursePreRequisition.setRequisitionCourse(selectedCourse);
//                            preRequisitionDs.addItem(coursePreRequisition);
//                        }
//                    }
//                }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
//                    StringBuilder sb = new StringBuilder("'" + courseDs.getItem().getId() + "',");
//                    for (CoursePreRequisition coursePreRequisition : preRequisitionDs.getItems()) {
//                        sb.append("'").append(coursePreRequisition.getRequisitionCourse().getId()).append("',");
//                    }
//                    put(StaticVariable.EXIST_COURSE, sb.toString().substring(0, sb.toString().length() - 1));
//                }});
//            }
//        });
//    }

//    @Override
//    protected void initNewItem(Course item) {
//        super.initNewItem(item);
//        item.setSelfEnrollment(false);
//    }

//    protected void checkAccessEdit(Course course) {
        /*boolean access = course != null && !hasEnrollments(course) && !hasCertificationEnrollments(course);

        richTextArea.setEditable(access);
        fieldGroup.setEditable(access);
        imageUpload.setVisible(access);

        if (access) {
            Boolean activeFlag = getItem().getActiveFlag();
            enableDisableActions(activeFlag == null || !activeFlag);
        } else {
            enableDisableActions(false);
        }*/

//        enableDisableActions(true);
//    }

//    protected void enableDisableActions(boolean enable) {
//        ComponentsHelper.walkComponents(tabSheet, new ComponentVisitor() {
//            @Override
//            public void visit(Component component, String name) {
//                if (component.getClass().getSimpleName().equalsIgnoreCase("WebTable")) {
//                    Table table = ((Table) component);
//                    table.getActions().forEach(new Consumer<Action>() {
//                        @Override
//                        public void accept(Action action) {
//                            action.setEnabled(enable);
//                        }
//                    });
//
//                    table.getButtonsPanel().setVisible(enable);
//                }
//            }
//        });
//    }

//    protected boolean hasEnrollments(Course course) {
//        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
//        LoadContext.Query query = LoadContext.createQuery(
//                "select e from tsadv$Enrollment e " +
//                        "where e.course.id = :cId");
//        query.setParameter("cId", course.getId());
//        loadContext.setQuery(query);
//        return dataManager.getCount(loadContext) > 0;
//    }

//    protected boolean hasCertificationEnrollments(Course course) {
//        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
//        LoadContext.Query query = LoadContext.createQuery(
//                "select e from tsadv$CertificationEnrollment e join e.certification c " +
//                        "where c.course.id = :cId");
//        query.setParameter("cId", course.getId());
//        loadContext.setQuery(query);
//        return dataManager.getCount(loadContext) > 0;
//    }

    protected void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        Course course = courseDc.getItem();
        if (imageUpload.getFileContent() != null) {
            try {
                course.setLogo(CommonUtils.resize(imageUpload.getFileContent(), IMAGE_SIZE.XSS));
                Utils.getCourseImageEmbedded(course, null, courseImage);
            } catch (IOException e) {
                notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                        .withCaption(messageBundle.getMessage("fileUploadErrorMessage")).show();
            }
        }
        imageUpload.setValue(null);
    }

    protected void beforeValueClearPerformed(FileUploadField.BeforeValueClearEvent beforeEvent) {
        beforeEvent.preventClearAction();
        dialogs.createOptionDialog()
                .withCaption(messageBundle.getMessage("confirmation"))
                .withMessage(messageBundle.getMessage("aUSure"))
                .withType(Dialogs.MessageType.CONFIRMATION)
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(actionPerformedEvent -> {
                                            courseDc.getItem().setLogo(null);
                                            courseImage.resetSource();
                                        }
                                ),
                        new DialogAction(DialogAction.Type.NO))
                .show();
    }

    @Subscribe("enrollmentsTable.create")
    protected void onEnrollmentsTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(PersonExt.class, this)
                .withScreenId("base$PersonForKpiCard.browse")
                .withSelectHandler(personList -> {
                    CommitContext commitContext = new CommitContext();
                    personList.forEach(personExt -> {
                        boolean isNew = true;
                        for (Enrollment enrollment : enrollmentDc.getItems()) {
                            if (enrollment.getPersonGroup().equals(personExt.getGroup())) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            Enrollment enrollment = metadata.create(Enrollment.class);
                            enrollment.setCourse(courseDc.getItem());
                            enrollment.setPersonGroup(personExt.getGroup());
                            enrollment.setStatus(EnrollmentStatus.REQUEST);
                            enrollment.setDate(BaseCommonUtils.getSystemDate());
                            if (courseScheduleDc.getItems().size() == 1) {
                                enrollment.setCourseSchedule(courseScheduleDc.getItems().get(0));
                            }
                            commitContext.addInstanceToCommit(enrollment);
                        }
                    });
                    dataManager.commit(commitContext);
                    enrollmentDl.load();
                })
                .build().show();
    }
}
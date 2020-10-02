package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ComponentVisitor;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.IMAGE_SIZE;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.partyext.PartyExtBrowse;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CourseEdit extends AbstractEditor<Course> {

    @Inject
    protected FileUploadField imageUpload;

    @Named("fieldGroup.party")
    protected PickerField partyField;

    @Inject
    protected Embedded courseImage;

    @Inject
    protected Datasource<Course> courseDs;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected CollectionDatasource<CoursePreRequisition, UUID> preRequisitionDs;

    @Inject
    protected Table<CourseCompetence> competencesTable;

    @Inject
    protected Table<CoursePreRequisition> preRequisitionTable;

    @Inject
    protected Table<CourseSection> sectionsTable;

    @Inject
    protected RichTextArea richTextArea;

    @Inject
    protected TabSheet tabSheet;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        PickerField.LookupAction lookupAction = partyField.getLookupAction();
        lookupAction.setLookupScreen("base$PartyExt.browse");
        lookupAction.setLookupScreenParams(ParamsMap.of(PartyExtBrowse.TRAINING_PROVIDER, true));


        imageUpload.setUploadButtonCaption("");
        imageUpload.setClearButtonCaption("");

        imageUpload.addFileUploadSucceedListener(this::fileUploadSucceed);

        imageUpload.addBeforeValueClearListener(this::beforeValueClearPerformed);

        preRequisitionTable.addAction(new BaseAction("selectPreRequisition") {
            @Override
            public void actionPerform(Component component) {
                openLookup("tsadv$Course.browse", new Lookup.Handler() {
                    @Override
                    public void handleLookup(Collection items) {
                        for (Course selectedCourse : (Collection<Course>) items) {
                            CoursePreRequisition coursePreRequisition = metadata.create(CoursePreRequisition.class);
                            coursePreRequisition.setCourse(courseDs.getItem());
                            coursePreRequisition.setRequisitionCourse(selectedCourse);
                            preRequisitionDs.addItem(coursePreRequisition);
                        }
                    }
                }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
                    StringBuilder sb = new StringBuilder("'" + courseDs.getItem().getId() + "',");
                    for (CoursePreRequisition coursePreRequisition : preRequisitionDs.getItems()) {
                        sb.append("'").append(coursePreRequisition.getRequisitionCourse().getId()).append("',");
                    }
                    put(StaticVariable.EXIST_COURSE, sb.toString().substring(0, sb.toString().length() - 1));
                }});
            }
        });
    }

    @Override
    protected void initNewItem(Course item) {
        super.initNewItem(item);
        item.setSelfEnrollment(false);
    }

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        Course course = courseDs.getItem();
        Utils.getCourseImageEmbedded(course, null, courseImage);

        checkAccessEdit(getItem());
    }

    protected void checkAccessEdit(Course course) {
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

        enableDisableActions(true);
    }

    protected void enableDisableActions(boolean enable) {
        ComponentsHelper.walkComponents(tabSheet, new ComponentVisitor() {
            @Override
            public void visit(Component component, String name) {
                if (component.getClass().getSimpleName().equalsIgnoreCase("WebTable")) {
                    Table table = ((Table) component);
                    table.getActions().forEach(new Consumer<Action>() {
                        @Override
                        public void accept(Action action) {
                            action.setEnabled(enable);
                        }
                    });

                    table.getButtonsPanel().setVisible(enable);
                }
            }
        });
    }

    protected boolean hasEnrollments(Course course) {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$Enrollment e " +
                        "where e.course.id = :cId");
        query.setParameter("cId", course.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    protected boolean hasCertificationEnrollments(Course course) {
        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CertificationEnrollment e join e.certification c " +
                        "where c.course.id = :cId");
        query.setParameter("cId", course.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    protected void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        Course course = courseDs.getItem();
        if (course != null && imageUpload.getFileContent() != null) {
            try {
                course.setLogo(CommonUtils.resize(imageUpload.getFileContent(), IMAGE_SIZE.XSS));
                Utils.getCourseImageEmbedded(course, null, courseImage);
            } catch (IOException e) {
                showNotification(getMessage("fileUploadErrorMessage"), NotificationType.ERROR);
            }
        }
        imageUpload.setValue(null);
    }

    protected void beforeValueClearPerformed(FileUploadField.BeforeValueClearEvent beforeEvent) {
        beforeEvent.preventClearAction();
        showOptionDialog("", getMessage("removeImage"), MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                            public void actionPerform(Component component) {
                                courseDs.getItem().setLogo(null);
                                courseImage.resetSource();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                });
    }
}
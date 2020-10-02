package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.service.CourseService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class AssignedTestBrowse extends AbstractWindow {


    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;
    @Inject
    protected Button passTest, attempts, removeEnrollment;
    @Inject
    protected VBoxLayout filterBox;

    @Inject
    protected CourseService courseService;

    @Inject
    protected AssignedTestDatasource assignedTestDs;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        assignedTestDs.setQuery("select e from tsadv$AssignedTestPojo e");
        super.init(params);

        assignedTestDs.addItemChangeListener(e -> {
            AssignedTestPojo assignedTestPojo = e.getItem();

            boolean enabled = assignedTestPojo != null;
            attempts.setEnabled(enabled);

            if (assignedTestPojo != null) {
                removeEnrollment.setEnabled(assignedTestPojo.getAttemptsCount() == 0);
            } else {
                removeEnrollment.setEnabled(false);
            }
        });


    }

    @Override
    public void ready() {
        super.ready();
        initFilterMap();
        customFilter = CustomFilter.init(assignedTestDs, assignedTestDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());
        initApply();
        customFilter.selectFilter("fullName");
    }

    protected void initApply() {
        Button searchButton = (Button) filterBox.getComponents().stream().filter(component ->
                component instanceof Button).findFirst().orElse(null);

        if (searchButton != null) {
            searchButton.setAction(new BaseAction("search1") {
                @Override
                public void actionPerform(Component component) {
                    Map<String, Object> mapToDs = new HashMap<>();
                    VBoxLayout filterComponent = (VBoxLayout) customFilter.getFilterComponent();
                    FlowBoxLayout flowBoxLayout = (FlowBoxLayout) filterComponent.getComponents().stream().filter(component1 ->
                            component1 instanceof FlowBoxLayout).findFirst().orElse(null);
                    if (flowBoxLayout != null) {
                        List<Component> webCssLayout = flowBoxLayout.getComponents().stream().filter(component1 ->
                                component1 instanceof CssLayout).collect(Collectors.toList());
                        ArrayList<Map<String, String>> list = new ArrayList<>();
                        for (Component component1 : webCssLayout) {
                            CssLayout cssLayout = (CssLayout) component1;
                            String key = null;
                            String value = null;
                            String operation = null;
                            for (Component component2 : cssLayout.getComponents()) {
                                if (component2 instanceof Label && ((Label) component2).getValue() != null && !((Label) component2).getValue().equals("")) {
                                    key = (String) ((Label) component2).getValue();
                                }
                                if (component2 instanceof TextField && ((TextField) component2).getValue() != null) {
                                    value = (String) ((TextField) component2).getValue();
                                } else if (component2 instanceof LookupField && ((LookupField) component2).getValue() != null) {
                                    value = ((LookupField) component2).getValue().toString();
                                }
                                if (component2 instanceof PopupButton && ((PopupButton) component2).getCaption() != null) {
                                    operation = ((PopupButton) component2).getCaption();
                                }

                            }
                            if (key != null && value != null && !value.equals("") && operation != null) {
                                Map<String, String> mapToList = new HashMap<>();
                                mapToList.put("column", key);
                                mapToList.put("operation", operation);
                                mapToList.put("value", value);
                                list.add(mapToList);
                            }
                        }
                        mapToDs.put("filter", list);
                        mapToDs.put("searchButton", true);
                    }
                    assignedTestDs.refresh(mapToDs);
                    mapToDs.remove("searchButton");
                }
            });
        }
    }

    protected void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("fullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.fullName"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN)
        );
        filterMap.put("position",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN)
        );
        filterMap.put("organization",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExt"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN)
        );
        filterMap.put("organizationWithDaughter",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExtWithDaughter"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN)
        );
        filterMap.put("status",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.web.modules.learning.enrollment", "Enrollment.status"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsEnum", EnrollmentStatus.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
        );
        filterMap.put("test",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.web.modules.learning.enrollment", "Test"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN)
        );
    }

    public void passTest() {

        AssignedTestPojo assignedTest = assignedTestDs.getItem();
        if (assignedTest != null && hasTestAttempt()) {

            UUID enrollmentId = assignedTest.getEnrollmentId();
            if (enrollmentId == null) {
                showNotification("Enrollment ID is null!");
                return;
            }

            Enrollment enrollment = loadEnrollment(enrollmentId);
            Course course = enrollment.getCourse();
            List<CourseSection> courseSections = course.getSections();
            CourseSection courseSection = null;
            if (courseSections != null && !courseSections.isEmpty()) {
                for (CourseSection searchCourseSection : courseSections) {
                    if (searchCourseSection.getId().equals(assignedTest.getCourseSectionId())) {
                        courseSection = searchCourseSection;
                        break;
                    }
                }
            }

            if (courseSection == null) {
                showNotification("CourseSection not found!");
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("courseSection", courseSection);
            params.put("enrollment", enrollment);

            openOnlineSectionWindow(params, "start-online-section");
        }
    }

    protected void openOnlineSectionWindow(Map<String, Object> params, String windowAlias) {
        openWindow(windowAlias, WindowManager.OpenType.THIS_TAB, params)
                .addCloseListener(actionId -> assignedTestDs.refresh());
    }

//    protected void abstractWindowCloseListener(String actionId) {
//        assignedTestDs.refresh();
//    }

    protected boolean hasTestAttempt() {
        AssignedTestPojo assignedTestPojo = assignedTestDs.getItem();

        UUID testId = assignedTestPojo.getTestId();
        LoadContext<CourseSectionAttempt> loadContext = LoadContext.create(CourseSectionAttempt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$CourseSectionAttempt e " +
                        "where e.test.id =:testId " +
                        "and e.enrollment.id = :enrollmentId");
        query.setParameter("testId", assignedTestPojo.getTestId());
        query.setParameter("enrollmentId", assignedTestPojo.getEnrollmentId());
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        Long attemptCount = dataManager.getCount(loadContext);

        Test test = loadTest(testId);
        if (test == null) {
            showNotification("Test not found");
        } else if (attemptCount >= test.getMaxAttempt()) {
            showNotification(getMessage("noAttempts"));
        }
        return !(test == null || attemptCount >= test.getMaxAttempt());
    }

    private Test loadTest(UUID testId) {
        return dataManager.load(LoadContext.create(Test.class).setId(testId).setView(View.LOCAL));
    }

    public void attempts() {
        try {
            AssignedTestPojo assignedTest = assignedTestDs.getItem();
            if (assignedTest != null) {
                UUID enrollmentId = assignedTest.getEnrollmentId();
                if (enrollmentId == null) {
                    throw new RuntimeException("Enrollment ID is null!");
                }

                openWindow("assigned-test-attempts",
                        WindowManager.OpenType.DIALOG,
                        ParamsMap.of("enrollmentId", enrollmentId));
            }
        } catch (Exception ex) {
            showNotification(ex.getMessage(), NotificationType.TRAY);
        }
    }

    public void addEnrollment() {
        AbstractEditor abstractEditor = openEditor(metadata.create(Enrollment.class), WindowManager.OpenType.DIALOG);
        abstractEditor.addCloseWithCommitListener(() -> assignedTestDs.refresh());
    }

    public void removeEnrollment() {
        showOptionDialog("Подтверждение",
                "Вы действительно хотите удалить выбранную запись?",
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                try {
                                    AssignedTestPojo assignedTest = assignedTestDs.getItem();
                                    if (assignedTest == null) {
                                        throw new RuntimeException("Assigned test not selected!");
                                    }

                                    UUID enrollmentId = assignedTest.getEnrollmentId();
                                    if (enrollmentId == null) {
                                        throw new RuntimeException("Enrollment ID is null!");
                                    }

                                    courseService.removeEnrollment(enrollmentId);
                                    assignedTestDs.refresh();
                                } catch (Exception ex) {
                                    showNotification(ex.getMessage(), NotificationType.TRAY);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }

    private Enrollment loadEnrollment(UUID enrollmentId) {
        return dataManager.load(LoadContext.create(Enrollment.class).setId(enrollmentId).setView("enrollment.for.testing"));
    }


}
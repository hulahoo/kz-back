package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.ViewRepository;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class EnrollmentPersonLookup extends AbstractLookup {

    @Inject
    private CollectionDatasource<Enrollment, UUID> enrollmentsDs;

    @Inject
    private HBoxLayout actionsHBox;
    @Inject
    private Metadata metadata;
    @Inject
    private DataGrid<Enrollment> enrollmentDataGrid;

    boolean isFromCopyAction;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ViewRepository viewRepository;
    @Inject
    private Filter filter;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromSingleCourseSection")) {
            enrollmentDataGrid.setSelectionMode(DataGrid.SelectionMode.SINGLE);
        }

        if (params.containsKey("fromCopyAction")) {
            getAction(Window.SELECT_ACTION_ID).setVisible(false);
            enrollmentDataGrid.setVisible(false);
            filter.setVisible(false);
            addCourseSectionSessionTable();
        }

        if (params.containsKey("fromNotEnrolledAction")) {
            enrollmentsDs.setQuery(enrollmentsDs.getQuery() + " and e.id not in (select cse.enrollment.id " +
                    "                                     from tsadv$CourseSessionEnrollment cse)");
        }
    }

    private void addCourseSectionSessionTable() {
        Table<CourseSectionSession> courseSectionSessionTable = componentsFactory.createComponent(Table.class);
        Button nextButton = createButton();
        Filter courseSectionSessionFilter = componentsFactory.createComponent(Filter.class);
        CollectionDatasource<CourseSectionSession, UUID> courseSectionSessionDs =
                createCourseSectionSessionDs();
        Action action = createNextAction(courseSectionSessionTable, courseSectionSessionFilter);

        courseSectionSessionTable.setDatasource(courseSectionSessionDs);
        courseSectionSessionTable.setSizeFull();

        nextButton.setAction(action);

        courseSectionSessionFilter.setDatasource(courseSectionSessionDs);

        courseSectionSessionDs.addItemChangeListener(e -> nextButton.getAction().setEnabled(e.getItem() != null));

        action.setEnabled(false);

        actionsHBox.add(nextButton, 0);
        add(courseSectionSessionFilter, 0);
        add(courseSectionSessionTable, 1);
        expand(courseSectionSessionTable);
    }

    private CollectionDatasource<CourseSectionSession, UUID> createCourseSectionSessionDs() {
        CollectionDatasource<CourseSectionSession, UUID> courseSectionSessionDs = new DsBuilder(getDsContext())
                .setJavaClass(CourseSectionSession.class)
                .setView(viewRepository.getView(CourseSectionSession.class, "myCourseSectionSession.browse"))
                .setId("courseSectionSessionDs")
                .buildCollectionDatasource();
        courseSectionSessionDs.setQuery("select e from tsadv$CourseSectionSession e" +
                "                         where e.courseSection.course.id = :param$courseId" +
                "                           and e.id <> :param$excludedCourseSectionSessionId" +
                "                           and e.trainer.employee.id = :session$userPersonGroupId");
        courseSectionSessionDs.refresh();

        return courseSectionSessionDs;
    }

    private Button createButton() {
        Button nextButton = componentsFactory.createComponent(Button.class);
        nextButton.setCaption(getMessage("actions.Select"));
        return nextButton;
    }

    private Action createNextAction(Table courseSectionSessionTable, Filter courseSectionSessionFilter) {
        return new BaseAction("next") {
                @Override
                public void actionPerform(Component component) {
                    courseSectionSessionTable.setVisible(false);
                    courseSectionSessionFilter.setVisible(false);
                    this.setVisible(false);
                    enrollmentDataGrid.setVisible(true);
                    filter.setVisible(true);
                    getAction(Window.SELECT_ACTION_ID).setVisible(true);
                    expand(enrollmentDataGrid);
                    enrollmentsDs.setQuery("select e from tsadv$Enrollment e" +
                            "                where e.id in (select cse.enrollment.id " +
                            "                                 from tsadv$CourseSessionEnrollment cse" +
                            "                                where cse.courseSession.id = :ds$courseSectionSessionDs) " +
                            "                       and e.id not in (select csa.enrollment.id from tsadv$CourseSectionAttempt csa " +
                            "                               where csa.courseSectionSession.id = :param$excludedCourseSectionSessionId)");
                    enrollmentsDs.refresh();
                }
            };
    }
}
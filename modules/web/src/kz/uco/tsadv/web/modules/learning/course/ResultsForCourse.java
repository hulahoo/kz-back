package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.DataGrid.Column;
import com.haulmont.cuba.gui.components.HtmlBoxLayout;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.ForPivotTable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.CourseService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class ResultsForCourse extends AbstractWindow {
    @Inject
    protected CourseService courseService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected CollectionDatasource<ForPivotTable, UUID> forPivotTablesDs;
    @Inject
    protected DataGrid<ForPivotTable> forPivotTablesDataGrid;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionDatasource<CourseSectionAttempt, UUID> courseSectionAttemptsDs;
    protected Cell[][] table;
    @Inject
    protected TextField<String> personFullNameTextField;
    protected Collection<CourseSectionAttempt> courseSectionAttempts;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addBeforeCloseWithCloseButtonListener(event -> {
            close(CLOSE_ACTION_ID, true);
        });
        personFullNameTextField.addEnterPressListener(e -> search());
    }

    @Override
    public boolean close(String actionId) {
        return close(actionId, true);
    }

    @Override
    public void ready() {
        super.ready();
//        createHTMLTable();
        createTable();

    }

    protected void createTable() {
        courseSectionAttempts = getCourseSectionAttempts();
        ArrayList<CourseSection> courseSections = getCourseSections(courseSectionAttempts);
        fillMainTableDsBySortedPersons(courseSectionAttempts);

        removeAllColumns();
        addPersonColumn();
        addCourseSectionsColumns(courseSectionAttempts, courseSections);

        forPivotTablesDataGrid.repaint();
    }

    protected void updateTable() {
        fillMainTableDsBySortedPersons(courseSectionAttempts);

        forPivotTablesDataGrid.repaint();
    }

    protected void removeAllColumns() {
        while (forPivotTablesDataGrid.getColumns().iterator().hasNext())
            forPivotTablesDataGrid.removeColumn(forPivotTablesDataGrid.getColumns().iterator().next());
    }

    private void addCourseSectionsColumns(Collection<CourseSectionAttempt> courseSectionAttempts, ArrayList<CourseSection> courseSections) {
        for (CourseSection section : courseSections) {
            if (section != null && section.getSectionName() != null && section.getSectionObject() != null
                    && section.getSectionObject().getObjectType() != null
                    && section.getSectionObject().getObjectType().getCode() != null) {
                Column column = forPivotTablesDataGrid.addGeneratedColumn(
                        "section" + section.getOrder(),
                        new DataGrid.ColumnGenerator<ForPivotTable, String>() {
                            @Override
                            public String getValue(DataGrid.ColumnGeneratorEvent<ForPivotTable> event) {
                                int max = 0;
                                boolean isTest = section.getSectionObject().getObjectType().getCode().equals("TEST");
                                boolean isSuccess = false;
                                for (CourseSectionAttempt attempt : courseSectionAttempts) {
                                    if (attempt != null && attempt.getEnrollment() != null
                                            && attempt.getEnrollment().getPersonGroup() != null
                                            && attempt.getCourseSection() != null
                                            && attempt.getSuccess() != null) {
                                        if (attempt.getEnrollment().getPersonGroup().equals(event.getItem().getPersonGroup()) &&
                                                attempt.getCourseSection().equals(section)) {
                                            if (isTest) {
                                                if (max < (attempt.getTestResult() != null ? attempt.getTestResult() : 0)) {
                                                    max = (attempt.getTestResult() != null ? attempt.getTestResult() : 0);
                                                }
                                            } else {
                                                if (!isSuccess && attempt.getSuccess() != null ? attempt.getSuccess() : false) {
                                                    isSuccess = attempt.getSuccess();
                                                }
                                            }
                                        }
                                    }
                                }
                                if (isTest) {
                                    return Integer.toString(max);
                                } else {
                                    if (isSuccess) {
                                        return getMessage("Succesed");
                                    } else {
                                        return getMessage("UnSuccesed");
                                    }
                                }
                            }

                            @Override
                            public Class<String> getType() {
                                return String.class;
                            }
                        });
                column.setCaption(section.getSectionName());
            }
        }
    }

    protected Collection<CourseSectionAttempt> getCourseSectionAttempts() {
        courseSectionAttemptsDs.refresh();

        return courseSectionAttemptsDs.getItems();
    }

    protected void addPersonColumn() {
        Column personColumn = forPivotTablesDataGrid.addGeneratedColumn(
                "personGroup",
                new DataGrid.ColumnGenerator<ForPivotTable, String>() {
                    @Override
                    public String getValue(DataGrid.ColumnGeneratorEvent<ForPivotTable> event) {
                        return event.getItem().getPersonGroup().getFullName();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        personColumn.setCaption(getMessage("fullName"));
    }

    protected ArrayList<CourseSection> getCourseSections(Collection<CourseSectionAttempt> courseSectionAttempts) {
        ArrayList<CourseSection> courseSections = new ArrayList<>();

        for (CourseSectionAttempt attempt : courseSectionAttempts) {
            if (!courseSections.contains(attempt.getCourseSection())) {
                courseSections.add(attempt.getCourseSection());
            }
        }
        courseSections.sort((o1, o2) -> {
            return o1.getOrder() < o2.getOrder() ? -1 : 1;
        });
        return courseSections;
    }

    /**
     * Возвращает ФИО + Id для дальнейшей сортировки (по ФИО)
     */
    protected String getSortableKey(PersonGroupExt personGroup) {
        if (personGroup == null) return null;
        return personGroup.getFullName() + "+" + personGroup.getId();
    }

    /**
     * Заполняет основной источник данных работниками в отсортированном виде
     */
    protected void fillMainTableDsBySortedPersons(Collection<CourseSectionAttempt> courseSectionAttempts) {
        forPivotTablesDs.clear();
        Map<String, PersonGroupExt> persons = getParticipants(courseSectionAttempts);

        List<String> sortedPersonsNames = persons.keySet().stream().sorted().collect(Collectors.toList());

        for (String key : sortedPersonsNames) {
            ForPivotTable forPivotTable = metadata.create(ForPivotTable.class);
            forPivotTable.setId(persons.get(key).getId());
            forPivotTable.setPersonGroup(persons.get(key));
            forPivotTablesDs.addItem(forPivotTable);
        }
    }

    /**
     * Возвращает уникальный список (карту) участников раздела курса
     */
    protected Map<String, PersonGroupExt> getParticipants(Collection<CourseSectionAttempt> courseSectionAttempts) {
        Map<String, PersonGroupExt> persons = new HashMap<>();

        for (CourseSectionAttempt attempt : courseSectionAttempts) {
            if (attempt != null && attempt.getEnrollment() != null
                    && attempt.getEnrollment().getPersonGroup() != null) {

                PersonGroupExt person = attempt.getEnrollment().getPersonGroup();
                if (isMatchedByFilter(person)) {
                    String sortableKey = getSortableKey(person);

                    if (!persons.containsKey(sortableKey)) {
                        persons.put(sortableKey, person);
                    }
                }
            }
        }
        return persons;
    }

    protected boolean isMatchedByFilter(PersonGroupExt person) {
        return personFullNameTextField.getValue() == null
                || person == null
                ||
                (person.getRelevantPerson() != null
                        && person.getRelevantPerson().getFullNameCyrillic() != null
                        &&
                        StringUtils.containsIgnoreCase(
                                person.getRelevantPerson().getFullNameCyrillic(),
                                personFullNameTextField.getValue()
                        )
                );
    }


    protected void createHTMLTable() {
        courseSectionAttemptsDs.refresh();
        ArrayList<CourseSection> courseSections = new ArrayList<>();


        Map<Object, String> rowHeaders = new HashMap<>();
        Collection<CourseSectionAttempt> items = courseSectionAttemptsDs.getItems();
        for (CourseSectionAttempt attempt : items) {
            if (!courseSections.contains(attempt.getCourseSection())) {
                courseSections.add(attempt.getCourseSection());
            }
            if (!rowHeaders.containsKey(attempt.getEnrollment().getPersonGroup())) {
                rowHeaders.put(attempt.getEnrollment().getPersonGroup(), attempt.getEnrollment().getPersonGroup().getFullName());
            }
        }
        table = new Cell[rowHeaders.size() + 1][courseSections.size() + 1];
        table[0][0] = new Cell(null, "");
        int i = 1;
        for (CourseSection section : courseSections) {
            table[0][i] = new Cell(section, section.getSectionName());
            i++;
        }
        i = 1;
        for (Map.Entry<Object, String> entry : rowHeaders.entrySet()) {
            table[i][0] = new Cell(entry.getKey(), entry.getValue());
            i++;
        }
        for (i = 1; i < table.length; i++) {
            int max = 0;
            boolean isTest = false;
            boolean isSuccessed = false;
            Cell personCell = table[i][0];
            for (int j = 1; j < table[i].length; j++) {
                PersonGroupExt person = (PersonGroupExt) (personCell.getObject());
                CourseSection section = (CourseSection) (table[0][j].getObject());
                isTest = section.getSectionObject().getObjectType().getCode().equals("TEST");
                for (CourseSectionAttempt attempt : items) {
                    if (attempt.getEnrollment().getPersonGroup().equals(person) &&
                            attempt.getCourseSection().equals(section)) {
                        if (isTest) {
                            if (max < attempt.getTestResult()) {
                                max = attempt.getTestResult();
                            }
                        } else {
                            if (!isSuccessed && attempt.getSuccess()) {
                                isSuccessed = attempt.getSuccess();
                            }
                        }
                    }
                }
                if (isTest) {
                    table[i][j] = new Cell(null, Integer.toString(max));
                } else {
                    if (isSuccessed) {
                        table[i][j] = new Cell(null, getMessage("Succesed"));
                    } else {
                        table[i][j] = new Cell(null, getMessage("UnSuccesed"));
                    }
                }
            }
        }
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table style=\"border:1px solid grey;\"><tr>");
        for (i = 0; i < table[0].length; i++) {
            htmlTable.append("<th style=\"border:1px solid grey;\" >").append(table[0][i].getName() != null ? table[0][i].getName() : "").append("</th>");
        }
        htmlTable.append("</tr>");
        for (i = 1; i < table.length; i++) {
            htmlTable.append("<tr>");
            for (int j = 0; j < table[i].length; j++) {
                htmlTable.append("<td style=\"border:1px solid grey;\">").append(table[i][j].getName() != null ? table[i][j].getName() : "").append("</td>");
            }
            htmlTable.append("</tr>");
        }
        htmlTable.append("</table>");


        HtmlBoxLayout htmlBoxLayout = componentsFactory.createComponent(HtmlBoxLayout.class);
        htmlBoxLayout.setTemplateContents(htmlTable.toString());
        add(htmlBoxLayout);

    }

    public void search() {
        updateTable();
    }

    protected class Cell {
        public Cell(Object object, String name) {
            this.object = object;
            this.name = name;
        }


        Object object;
        String name;

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
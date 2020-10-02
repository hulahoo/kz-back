package kz.uco.tsadv.web.modules.personal.gradegroup;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.model.Grade;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.service.AbsenceBalanceService;
import kz.uco.tsadv.web.modules.personal.grade.GradeEdit;

import javax.inject.Inject;
import java.util.*;

public class GradeGroupBrowse extends AbstractLookup {

    @Inject
    private AbsenceBalanceService absenceBalanceService;
    @Inject
    private GroupTable<GradeGroup> gradeGroupsTable;
    @Inject
    private GroupDatasource<GradeGroup, UUID> gradeGroupsDs;

    @Inject
    private CollectionDatasource<Grade, UUID> listDs;
    @Inject
    private Metadata metadata;

    @Inject
    private VBoxLayout filterBox;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("fromTalentProgram")) {
            String query = String.format("select e from tsadv$GradeGroup e  \n" +
                    "  where (e.id not in (select a.gradeGroup.id from tsadv$TalentProgramGrade a  \n" +
                    "  where a.talentProgram.id='%s')) ", (UUID) params.get("fromTalentProgram"));
            if (params.containsKey("existingGrades")){
               query = query.concat(String.format(" and (e.id not in %s)",params.get("existingGrades")));
            }
            gradeGroupsDs.setQuery(query);
        }

        initFilterMap();

        customFilter = CustomFilter.init(gradeGroupsDs, gradeGroupsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("gradeName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Grade.gradeName"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(g.gradeName) ?")
        );

    }

    public void openGrade() {
        openGradeEditor(metadata.create(Grade.class), null);
    }

    public void edit() {
        GradeGroup gradeGroup = gradeGroupsDs.getItem();
        if (gradeGroup != null) {
            Grade grade = gradeGroup.getGrade();
            if (grade != null) {
                openGradeEditor(grade, null);
            }
        }
    }

    private void openGradeEditor(Grade grade, Map<String, Object> params) {
        GradeEdit gradeEdit = (GradeEdit) openEditor("tsadv$Grade.edit", grade, WindowManager.OpenType.DIALOG, params);
        gradeEdit.addCloseListener(actionId -> gradeGroupsDs.refresh());
    }

    public void editHistory() {
        Grade item = listDs.getItem();

        List<Grade> items = gradeGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openGradeEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        Grade item = listDs.getItem();
        List<Grade> items = gradeGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        listDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        listDs.modifyItem(items.get(index + 1));
                                    }

                                    listDs.removeItem(item);

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        gradeGroupsDs.removeItem(item.getGroup());
                                        gradeGroupsDs.getDsContext().commit();
                                    }

                                    gradeGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        Grade item = listDs.getItem();
        List<Grade> items = gradeGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    items.get(index - 1).setEndDate(items.get(items.size() - 1).getEndDate());
                                    listDs.modifyItem(items.get(index - 1));

                                    while (index < items.size())
                                        listDs.removeItem(items.get(index));

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        gradeGroupsDs.removeItem(item.getGroup());
                                        gradeGroupsDs.getDsContext().commit();
                                    }

                                    gradeGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }
}

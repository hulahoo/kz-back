package kz.uco.tsadv.web.modules.personal.graderule;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.IntegerDatatype;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.GradeRuleValueGroup;
import kz.uco.tsadv.modules.personal.model.GradeRule;
import kz.uco.tsadv.modules.personal.model.GradeRuleValue;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.web.modules.personal.graderulevalue.GradeRuleValueEdit;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GradeRuleBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<GradeRule, UUID> gradeRulesDs;
    @Inject
    private GroupDatasource<GradeRuleValueGroup, UUID> gradeRuleValueGroupsDs;
    @Inject
    private CollectionDatasource<GradeRuleValue, UUID> listDs;

    @Inject
    private ButtonsPanel gradeRuleValueGroupsButtonsPanel;

    @Inject
    private Metadata metadata;
    @Inject
    private VBoxLayout filterBox;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Inject
    private CollectionDatasource<GradeGroup, UUID> gradeGroupsDs;

    @WindowParam
    private GradeGroup gradeGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        gradeRuleValueGroupsButtonsPanel.setEnabled(gradeRulesDs.getItem() != null);

        if (gradeGroup != null) {
            updateGradeRuleValueGroupsDatasource();
            updateGradeRulesDatasource();
        }

        gradeRulesDs.addItemChangeListener((e) ->
        {
            gradeRuleValueGroupsDs.refresh();
            gradeRuleValueGroupsButtonsPanel.setEnabled(gradeRulesDs.getItem() != null);
        });

        initGradeRuleValueFilterMap();

        customFilter = CustomFilter.init(gradeRuleValueGroupsDs, gradeRuleValueGroupsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());
    }

    private void initGradeRuleValueFilterMap() {
        filterMap = new HashMap<>();

        filterMap.put("grade",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Grade"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", gradeGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "grade.gradeName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("grv.gradeGroup.id ?")
        );

        filterMap.put("value",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "GradeRuleValue.value"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("grv.value ?")
        );

        filterMap.put("min",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "GradeRuleValue.min"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("grv.min ?")
        );

        filterMap.put("mid",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "GradeRuleValue.mid"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("grv.mid ?")
        );

        filterMap.put("max",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "GradeRuleValue.max"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("grv.max ?")
        );
    }

    public void openGradeRuleValue() {
        GradeRuleValue grv = metadata.create(GradeRuleValue.class);
        grv.setGradeRule(gradeRulesDs.getItem());
        openGradeRuleValueEditor(grv, null);
    }

    public void edit() {
        GradeRuleValueGroup gradeRuleValueGroup = gradeRuleValueGroupsDs.getItem();
        if (gradeRuleValueGroup != null) {
            GradeRuleValue gradeRuleValue = gradeRuleValueGroup.getGradeRuleValue();
            if (gradeRuleValue != null) {
                openGradeRuleValueEditor(gradeRuleValue, null);
            }
        }
    }

    private void openGradeRuleValueEditor(GradeRuleValue gradeRuleValue, Map<String, Object> params) {
        GradeRuleValueEdit gradeRuleValueEdit = (GradeRuleValueEdit) openEditor("tsadv$GradeRuleValue.edit", gradeRuleValue, WindowManager.OpenType.DIALOG, params);
        gradeRuleValueEdit.addCloseWithCommitListener(() -> gradeRuleValueGroupsDs.refresh());
    }

    public void editHistory() {
        GradeRuleValue item = listDs.getItem();

        List<GradeRuleValue> items = gradeRuleValueGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openGradeRuleValueEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        GradeRuleValue item = listDs.getItem();
        List<GradeRuleValue> items = gradeRuleValueGroupsDs.getItem().getList();
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
                                        gradeRuleValueGroupsDs.removeItem(item.getGroup());
                                        gradeRuleValueGroupsDs.getDsContext().commit();
                                    }

                                    gradeRuleValueGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        GradeRuleValue item = listDs.getItem();
        List<GradeRuleValue> items = gradeRuleValueGroupsDs.getItem().getList();
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
                                        gradeRuleValueGroupsDs.removeItem(item.getGroup());
                                        gradeRuleValueGroupsDs.getDsContext().commit();
                                    }

                                    gradeRuleValueGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        }
    }

    private void updateGradeRuleValueGroupsDatasource() {
        gradeRuleValueGroupsDs.setQuery(
                " SELECT e" +
                        " FROM tsadv$GradeRuleValueGroup e" +
                        " JOIN e.list grv" +
                        " WHERE grv.gradeGroup.id = :custom$gradeGroupId" +
                        " AND grv.gradeRule.id = :ds$gradeRulesDs" +
                        " AND :session$systemDate BETWEEN grv.startDate AND grv.endDate");
        Map<String, Object> map = new HashMap<>();
        map.put("gradeGroupId", gradeGroup.getId());
        gradeRuleValueGroupsDs.refresh(map);
    }

    private void updateGradeRulesDatasource() {
        gradeRulesDs.setQuery(
                " SELECT e.gradeRule " +
                        " FROM tsadv$GradeRuleValue e" +
                        " WHERE e.gradeGroup.id = :custom$gradeGroupId");
        Map<String, Object> map = new HashMap<>();
        map.put("gradeGroupId", gradeGroup.getId());
        gradeRulesDs.refresh(map);
    }


}
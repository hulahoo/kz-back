package kz.uco.tsadv.web.modules.personal.competencegroup;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.Competence;
import kz.uco.tsadv.modules.personal.model.Scale;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.competence.CompetenceEdit;

import javax.inject.Inject;
import java.util.*;

public class CompetenceGroupBrowse extends AbstractLookup {
    @Inject
    private GroupTable<CompetenceGroup> competenceGroupsTable;
    @Inject
    private GroupDatasource<CompetenceGroup, UUID> competenceGroupsDs;

    @Inject
    private CollectionDatasource<Competence, UUID> listDs;
    @Inject
    private Metadata metadata;

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private VBoxLayout filterBox;
    @Inject
    private CollectionDatasource<Scale, UUID> scalesDs;
    private Map<String, CustomFilter.Element> filterMap;
    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
//        initFilterMap();

//        customFilter = CustomFilter.init(competenceGroupsDs, competenceGroupsDs.getQuery(), filterMap);
//        filterBox.add(customFilter.getFilterComponent());
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("competenceNameLang1",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Competence.competenceNameRu"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(c.competenceNameLang1) ?")
        );
        filterMap.put("competenceNameLang2",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Competence.competenceNameKz"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(c.competenceNameLang2) ?")
        );
        filterMap.put("competenceNameLang3",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Competence.competenceNameEn"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(c.competenceNameLang3) ?")
        );

        filterMap.put("scale",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Scale"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", scalesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("c.scale.id ?")
        );

    }

    public void openCompetence() {
        openCompetenceEditor(metadata.create(Competence.class), null);
    }

    public void edit() {
        CompetenceGroup competenceGroup = competenceGroupsDs.getItem();
        if (competenceGroup != null) {
            Competence competence = competenceGroup.getCompetence();
            if (competence != null) {
                openCompetenceEditor(competence, null);
            }
        }
    }

    private void openCompetenceEditor(Competence competence, Map<String, Object> params) {
        CompetenceEdit competenceEdit = (CompetenceEdit) openEditor("tsadv$Competence.edit", competence, WindowManager.OpenType.DIALOG, params);
        competenceEdit.addCloseListener(actionId -> competenceGroupsDs.refresh());
    }

    public Component getCompetenceDownloadBtn(Competence entity) {
        return Utils.getFileDownload(entity.getAttachment(), CompetenceGroupBrowse.this);
    }

    public Component getCompetenceGroupDownloadBtn(CompetenceGroup entity) {
        return Utils.getFileDownload(entity.getCompetence().getAttachment(), CompetenceGroupBrowse.this);
    }

    public void editHistory() {
        Competence item = listDs.getItem();

        List<Competence> items = competenceGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openCompetenceEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        Competence item = listDs.getItem();
        List<Competence> items = competenceGroupsDs.getItem().getList();
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
                                        competenceGroupsDs.removeItem(item.getGroup());
                                        competenceGroupsDs.getDsContext().commit();
                                    }

                                    competenceGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        Competence item = listDs.getItem();
        List<Competence> items = competenceGroupsDs.getItem().getList();
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
                                        competenceGroupsDs.removeItem(item.getGroup());
                                        competenceGroupsDs.getDsContext().commit();
                                    }

                                    competenceGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }
}
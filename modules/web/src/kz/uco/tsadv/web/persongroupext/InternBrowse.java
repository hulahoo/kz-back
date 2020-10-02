package kz.uco.tsadv.web.persongroupext;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Internship;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class InternBrowse extends AbstractLookup {
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionDatasource<PersonExt, UUID> personExtsDs;
    @Inject
    protected Table<PersonExt> personGroupExtsTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Button editButtonId;
    @Inject
    protected Button removeButtonId;
    @Inject
    protected Button goToInternship;
    protected Internship internship;
    protected List<Internship> internships;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        internships = new ArrayList<>();
    }

    @Override
    public void ready() {
        super.ready();
        loadLists();
        personExtsDs.addCollectionChangeListener(e -> {
            loadLists();
        });
        personExtsDs.addItemChangeListener(e -> {
            personExtsDsItemChangeListener(e);

        });

        personGroupExtsTable.addGeneratedColumn("mentorField", entity ->
                personGroupExtsTableGeneratedColumnMentorField(entity)
        );

        personGroupExtsTable.addGeneratedColumn("organization", entity ->
                personGroupExtsTableGeneratedColumnOrganization(entity));

        personGroupExtsTable.sort("fioWithEmployeeNumberWithSortSupported",Table.SortDirection.ASCENDING);
    }

    protected Component personGroupExtsTableGeneratedColumnOrganization(PersonExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        List<Internship> internships = getInternships(entity);
        if (!internships.isEmpty()) {
            internships.get(0);
            for (Internship internship : internships) {
                OrganizationGroupExt organizationGroup = internship.getOrganizationGroup();
                if (organizationGroup != null) {
                    String orgName = internship.getOrganizationGroup().getOrganizationName();
                    label.setValue(orgName);
                } else {
                    label.setValue(null);
                }
            }
        }
        return label;
    }

    protected Component personGroupExtsTableGeneratedColumnMentorField(PersonExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        List<Internship> internships = getInternships(entity);
        if (!internships.isEmpty()) {
            internships.get(0);
            for (Internship internship : internships) {
                PersonGroupExt personGroupExt = internship.getMainMentor();
                if (personGroupExt != null) {
                    String mentorName = internship.getMainMentor().getFullName();
                    label.setValue(mentorName);
                } else {
                    label.setValue(null);
                }
            }
        }
        return label;
    }

    protected void personExtsDsItemChangeListener(Datasource.ItemChangeEvent<PersonExt> e) {
        if (personGroupExtsTable.getSingleSelected() != null) {
            editButtonId.setEnabled(true);
            goToInternship.setEnabled(true);
            if (getInternships(e.getItem()).isEmpty()) {
                removeButtonId.setEnabled(true);
            } else {
                removeButtonId.setEnabled(false);
            }
        } else {
            editButtonId.setEnabled(false);
            removeButtonId.setEnabled(false);
            goToInternship.setEnabled(false);
        }
    }

    protected void loadLists() {
        internships = commonService.getEntities(Internship.class, "select e" +
                        " from tsadv$Internship e where e.personGroup.id in :personGroupIdList",
                ParamsMap.of("personGroupIdList",
                        personExtsDs.getItems().stream().map(personExt -> personExt.getGroup().getId()).collect(Collectors.toList())),
                "internship.for.loadList");
    }

    protected List<Internship> getInternships(PersonExt personExt) {
        return internships.stream().filter(internship1 -> personExt != null
                && personExt.getGroup() != null && personExt.getGroup().getId() != null &&
                internship1.getPersonGroup().getId().equals(personExt.getGroup().getId())).sorted((o1, o2) ->
                o1.getStartDate().after(o2.getStartDate()) ? 1 : -1).collect(Collectors.toList());
    }


    public void createIntern() {
        DicPersonType type = commonService.getEntity(DicPersonType.class, "INTERNSHIP");
        if (type != null) {
            PersonExt personExt = metadata.create(PersonExt.class);
            openEditor(personExt);
        } else {
            showNotification(getMessage("warningForIntern"));
        }
    }

    public void EditIntern() {
        PersonExt personExt = personGroupExtsTable.getSingleSelected();
        openEditor(personExt);
    }

    protected void openEditor(PersonExt personExt) {
        Editor internEditor = (Editor) openEditor("tsadv$intern.edit", personExt, WindowManager.OpenType.THIS_TAB);
        internEditor.addCloseListener(actionId -> {
            personExtsDs.refresh();
        });
    }

    public void deleteIntern() {
        PersonExt personExt = personGroupExtsTable.getSingleSelected();
        if (personExt != null) {
            showOptionDialog(
                    getMessage("msg.warning.title"),
                    getMessage("msg.remove.title"),
                    MessageType.WARNING,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    personExtsDs.removeItem(personExt);
                                    personExtsDs.commit();
                                    removeButtonId.setEnabled(false);
                                    editButtonId.setEnabled(false);
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    });
        }
//        personGroupExtsDs.removeItem(personGroupExt);
//        personGroupExtsDs.commit();
//        removeButtonId.setEnabled(false);
//        editButtonId.setEnabled(false);
    }

    public Component generatePersonFioWithEmployeeNumberCell(PersonExt entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getFioWithEmployeeNumberWithSortSupported());
        linkButton.setAction(new BaseAction("linkButton") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                openEditor(entity);
            }
        });
        return linkButton;
    }

    public void goToInternship() {
        PersonGroupExt personGroupExt = personGroupExtsTable.getSingleSelected().getGroup();
        Window internBrowse = openWindow("tsadv$Internship.browse", WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("personGroupExt", personGroupExt));
        internBrowse.addCloseListener(actionId -> {
            personExtsDs.refresh();
        });
    }

}
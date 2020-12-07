package kz.uco.tsadv.web.structure.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.event.ExpandEvent;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.MyTeamComponent;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.MyTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class StructurePerson extends AbstractLookup {
    @Inject
    protected HierarchicalDatasource<MyTeamNew, UUID> teamDs;
    @Inject
    protected Tree<MyTeamNew> tree;
    @Inject
    protected MyTeamComponent myTeamComponent;
    @Inject
    protected Button searchButton;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    @Inject
    protected Datasource<PersonExt> personExtDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected MyTeamService myTeamService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected LinkButton firstNameLink;
    @Inject
    protected LinkButton middleNameLink;
    @Inject
    protected LinkButton lastNameLink;

    protected boolean isEng;
    protected String pathParent;
    protected String vacantPosition;

    protected UUID positionStructureId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        positionStructureId = positionStructureConfig.getPositionStructureId();
        isEng = AppBeans.get(UserSession.class).getLocale().getLanguage().equals("en");
        vacantPosition = messages.getMessage("kz.uco.tsadv.web.modules.personal.assignment", "vacantPosition");

        personExtDs.addItemChangeListener(e -> {
            PersonExt personExt = e.getItem();
            firstNameLink.setCaption(getValue(personExt, "firstName", isEng));
            middleNameLink.setCaption(getValue(personExt, "middleName", isEng));
            lastNameLink.setCaption(getValue(personExt, "lastName", isEng));
        });

        tree.setIconProvider(entity -> "font-icon:USER");
        tree.setStyleName("b-tree");

        initTeamDs();

        tree.collapseTree();
    }

    protected String getValue(PersonExt personExt, String property, boolean isEng) {
        String value = personExt != null ?
                personExt.getValue(property + (isEng ? "Latin" : ""))
                : null;
        if (isEng && personExt != null && StringUtils.isBlank(value))
            return getValue(personExt, property, false);
        return value;
    }

    @Override
    public void ready() {
        super.ready();
        tree.unwrap(com.vaadin.ui.Tree.class).addExpandListener(this::itemExpand);
//        tree.expandTree();
        searchField.addEnterPressListener(e -> searchBtn());
        teamDs.addItemChangeListener(this::itemChangeListener);
    }

    public void itemExpand(ExpandEvent event) {
        if (pathParent != null) return;
        myTeamComponent.onExpand(event, teamDs);
    }

    protected void itemChangeListener(Datasource.ItemChangeEvent<MyTeamNew> event) {
        personExtDs.setItem(getPerson(event.getItem() != null ? event.getItem().getPersonGroupId() : null));
    }

    protected PersonExt getPerson(UUID personGroupId) {
        if (personGroupId == null) return null;
        return commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :personGroupId and :sysDate between e.startDate and e.endDate",
                ParamsMap.of("personGroupId", personGroupId,
                        "sysDate", CommonUtils.getSystemDate()), "person-edit");
    }

    public void searchBtn() {
        teamDs.clear();
        if (StringUtils.isBlank(searchField.getValue())) initTeamDs();
        else search();
    }

    protected void initTeamDs() {
        pathParent = null;
        myTeamComponent.addChildren(teamDs, null, null);
    }

    protected void search() {
        String searchFieldValue = searchField.getValue();
        List<Object[]> objectList = myTeamService.searchMyTeam(positionStructureId, null,
                searchFieldValue, null, null, false);

        if (objectList.isEmpty()) {
            initTeamDs();
            showNotification(String.format(messages.getMainMessage("search.not.found"), searchFieldValue));
            return;
        }

        for (Object[] objects : objectList) {
            pathParent = (String) objects[1];
            String[] path = (pathParent).split("\\*");
            UUID foundPosGroupId = (UUID) objects[2];
            UUID foundPersonGroupId = (UUID) objects[3];

            for (int i = 0; i < path.length; i++) {
                UUID posId = UUID.fromString(path[i]);
                if (!contain(posId)) {
                    UUID parentId = i == 0 ? null : UUID.fromString(path[i - 1]);
                    UUID personGroupId = posId.equals(foundPosGroupId) ? foundPersonGroupId : null;
                    addItems(posId, parentId, personGroupId);
                }
            }
            List<UUID> childIdList = myTeamService.getChildPositionIdList(foundPosGroupId, positionStructureId);
            if (!CollectionUtils.isEmpty(childIdList)) {
                childIdList.forEach(child -> addItems(child, foundPosGroupId, null));
            }
        }
        tree.expandTree();
        pathParent = null;
    }

    protected void addItems(@Nonnull UUID posId, @Nullable UUID parentPosId, @Nullable UUID personGroupId) {
        List<MyTeamNew> parentList = teamDs.getItems().stream()
                .filter(myTeamNew -> Objects.equals(myTeamNew.getPositionGroupId(), parentPosId))
                .collect(Collectors.toList());

        List<MyTeamNew> list = myTeamService.getMyTeamInPosition(posId, positionStructureId)
                .stream().map(objects -> myTeamService.parseMyTeamNewObject(objects, vacantPosition))
                .collect(Collectors.toList());

        for (MyTeamNew myTeamNew : list) {
            if (personGroupId == null
                    || personGroupId.equals(myTeamNew.getPersonGroupId())) {
                if (parentList.isEmpty()) {
                    myTeamNew.setParent(null);
                    teamDs.addItem(myTeamNew);
                } else {
                    for (MyTeamNew teamNew : parentList) {
                        MyTeamNew copy = copy(myTeamNew);
                        copy.setParent(teamNew);
                        teamDs.addItem(copy);
                        /*if (personGroupId != null && Boolean.TRUE.equals(copy.getHasChild())) {
                            teamDs.addItem(myTeamService.createFakeChild(copy));
                        }*/
                    }
                }
            }
        }
    }

    protected MyTeamNew copy(MyTeamNew myTeamNew) {
        MyTeamNew copy = new MyTeamNew();
        copy.setFullName(myTeamNew.getFullName());
        copy.setPositionGroupId(myTeamNew.getPositionGroupId());
        copy.setPersonGroupId(myTeamNew.getPersonGroupId());
        copy.setHasChild(myTeamNew.getHasChild());
        return copy;
    }

    protected boolean contain(UUID posId) {
        for (MyTeamNew item : teamDs.getItems()) {
            if (posId.equals(item.getPositionGroupId())) return true;
        }
        return false;
    }

    public void openPersonCard() {
        openEditor("person-card", personExtDs.getItem().getGroup(), WindowManager.OpenType.THIS_TAB);
    }

    protected AssignmentExt getAssignment(UUID personGroupId) {
        return commonService.getEntity(AssignmentExt.class,
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroup.id = :personGroupId",
                ParamsMap.of("personGroupId", personGroupId,
                        "sysDate", CommonUtils.getSystemDate()),
                "assignment.card");
    }


}
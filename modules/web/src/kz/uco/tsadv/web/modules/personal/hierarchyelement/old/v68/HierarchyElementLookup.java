package kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Tree;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.OrganizationStructureConfig;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

//todo
public class HierarchyElementLookup extends AbstractLookup {

    @Inject
    protected CommonService commonService;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected PositionStructureConfig positionStructureConfig;

    @Inject
    protected OrganizationStructureConfig organizationStructureConfig;

    @Inject
    protected TextField<String> searchField;

    @Inject
    protected LookupField hierarchyLookup;

    @Inject
    protected HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;

    @Inject
    protected Tree<HierarchyElementExt> hierarchyElementsTree;

    @WindowParam
    protected Date hierarchyEndDate;

    protected Date oracleMaxDate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Calendar instance = Calendar.getInstance();
        instance.setTime(CommonUtils.getEndOfTime());
        instance.set(Calendar.YEAR, 4712);
        oracleMaxDate = instance.getTime();

        searchField.addEnterPressListener(enterPressEvent -> searchForHierarchyElement());

        if (params.containsKey("hierarchy")) {
            hierarchyLookup.setValue(params.get("hierarchy"));
            hierarchyLookup.setEnabled(false);
        }

        hierarchyLookup.addValueChangeListener(e -> hierarchyElementsDs.refresh());

        hierarchyElementsTree.setIconProvider(this::getIconElement);

        params.putIfAbsent("hierarchyEndDate", CommonUtils.getSystemDate());

        hierarchyEndDate = (Date) params.get("hierarchyEndDate");
        if (hierarchyEndDate.after(oracleMaxDate)) {
            hierarchyEndDate = oracleMaxDate;
        }
        params.put("date", hierarchyEndDate);

    }

    protected String getIconElement(HierarchyElementExt entity) {
        switch (entity.getElementType()) {
            case ORGANIZATION:
                return "font-icon:BANK"; //BUILDING_O
            case POSITION:
                return "font-icon:BRIEFCASE";
            default:
                return "font-icon:USER";
        }
    }

    //todo нужно переделать
    public void searchForHierarchyElement() {
        String searchStr = searchField.getValue();
        if (searchStr != null && !StringUtils.isBlank(searchStr)) {

            String language = userSessionSource.getLocale().getLanguage();

            String hierarchyElement = "";
            String elementLangValue = "";

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(organizationStructureConfig.getOrganizationStructureId())) {
                hierarchyElement = "organizationGroup";
                if (language.equals("ru")) {
                    elementLangValue = "organizationNameLang1";
                }
                if (language.equals("kz")) {
                    elementLangValue = "organizationNameLang2";
                }
                if (language.equals("en")) {
                    elementLangValue = "organizationNameLang3";
                }
            }

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(positionStructureConfig.getPositionStructureId())) {
                hierarchyElement = "positionGroup";
                if (language.equals("ru")) {
                    elementLangValue = "positionFullNameLang1";
                }
                if (language.equals("kz")) {
                    elementLangValue = "positionFullNameLang1";
                }
                if (language.equals("en")) {
                    elementLangValue = "positionFullNameLang1";
                }
            }

            if (!StringUtils.isBlank(hierarchyElement) && !StringUtils.isBlank(elementLangValue)) {
                List<HierarchyElementExt> childrenElements;
//                if (hierarchyStartDate == null && hierarchyEndDate == null) {
                String queryForChildren = "SELECT e\n" +
                        " FROM base$HierarchyElementExt e\n" +
                        " JOIN e." + hierarchyElement + " AS og\n" +
                        " JOIN og.list AS o\n" +
                        " WHERE :systemDate BETWEEN e.startDate AND e.endDate\n" +
                        " AND e.hierarchy.id = :hierarchyLookup\n" +
                        " AND LOWER(o." + elementLangValue + ") LIKE LOWER(CONCAT( '%', :searchStr, '%' ))";
                Map<String, Object> params = new HashMap<>();
                params.put("searchStr", searchStr);
                params.put("systemDate", hierarchyEndDate);
                params.put("hierarchyLookup", ((Hierarchy) hierarchyLookup.getValue()).getId());
                childrenElements = commonService.getEntities(HierarchyElementExt.class, queryForChildren, params, "hierarchyElement.parent");

                if (!childrenElements.isEmpty()) {
                    //создаем копию списка для поиска "внучек"
                    List<UUID> elementsIds = new ArrayList<>();
                    //достаем родителей
                    for (HierarchyElementExt element : childrenElements) {
                        getParents(element, elementsIds);
                    }
                    String query = "SELECT e\n" +
                            " FROM base$HierarchyElementExt e\n" +
                            " WHERE e.id IN :custom$elementsIds\n" +
                            " AND :param$date between e.startDate and e.endDate";
                    Map<String, Object> map = new HashMap<>();
                    map.put("elementsIds", elementsIds);
                    hierarchyElementsDs.setQuery(query);
                    hierarchyElementsDs.refresh(map);
                    hierarchyElementsTree.expandTree();
                } else {
                    hierarchyElementsDs.setQuery("select e\n" +
                            "                           from base$HierarchyElementExt e\n" +
                            "                          where :param$date between e.startDate and e.endDate\n" +
                            "                            and e.hierarchy.id = :component$hierarchyLookup.id");
                    hierarchyElementsDs.refresh();
                    hierarchyElementsTree.collapseTree();
                }
            }
        } else {
            hierarchyElementsDs.setQuery("select e\n" +
                    "                           from base$HierarchyElementExt e\n" +
                    "                          where :param$date between e.startDate and e.endDate\n" +
                    "                            and e.hierarchy.id = :component$hierarchyLookup.id");
            hierarchyElementsDs.refresh();
            hierarchyElementsTree.collapseTree();
        }

        if(hierarchyElementsDs.getItems() != null && !hierarchyElementsDs.getItems().isEmpty()) {
            List<HierarchyElementExt> items = new ArrayList<>();

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(organizationStructureConfig.getOrganizationStructureId())) {
                hierarchyElementsDs.getItems()
                        .stream()
                        .filter(element -> element != null
                            && element.getOrganizationGroup() != null
                            && element.getOrganizationGroup().getOrganization() == null)
                        .collect(Collectors.toList())
                        .forEach(items::add);
                }

            if (((Hierarchy) hierarchyLookup.getValue()).getId().equals(positionStructureConfig.getPositionStructureId())) {
                hierarchyElementsDs.getItems()
                        .stream()
                        .filter(element -> element != null
                                && element.getPositionGroup() != null
                                && element.getPositionGroup().getPosition() == null)
                        .collect(Collectors.toList())
                        .forEach(items::add);
            }

            for (HierarchyElementExt element : items) {
                hierarchyElementsDs.removeItem(hierarchyElementsDs.getItem(element.getId()));
            }
        }
    }

    protected void getParents(HierarchyElementExt element, List<UUID> list) {
        list.add(element.getId());
        if (element.getParent() != null) {
            String queryString = "SELECT e FROM base$HierarchyElementExt e\n" +
                    "WHERE e.id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", element.getParent().getId());
            HierarchyElementExt elementExt = commonService.getEntity(HierarchyElementExt.class, queryString, map, "hierarchyElement.parent");
            getParents(elementExt, list);
        }
    }
}
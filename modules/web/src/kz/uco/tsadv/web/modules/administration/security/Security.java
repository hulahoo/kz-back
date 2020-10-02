package kz.uco.tsadv.web.modules.administration.security;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.Group;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.security.SecurityOrganizationList;
import kz.uco.tsadv.modules.administration.security.SecurityPersonList;
import kz.uco.tsadv.modules.administration.security.SecurityPersonType;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.service.CallStoredFunctionService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.stream.Collectors;
import java.util.*;

public class Security extends AbstractWindow {
    @Inject
    protected CollectionDatasource<SecurityPersonType, UUID> securityPersonTypesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TreeTable<Group> groupsTable;
    @Inject
    protected HierarchicalDatasource<Group, UUID> groupsDs;
    @Named("securityHierarchyNodesTable.create")
    protected CreateAction securityHierarchyNodesTableCreate;
    @Named("securityOrganizationEligibilitiesTable.create")
    protected CreateAction securityOrganizationEligibilitiesTableCreate;
    @Named("securityOrganizationListsTable.create")
    protected CreateAction securityOrganizationListsTableCreate;
    @Named("securityPersonListsTable.create")
    protected CreateAction securityPersonListsTableCreate;
    @Named("securityPersonTypesTable.create")
    protected CreateAction securityPersonTypesTableCreate;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Inject
    protected CollectionDatasource<SecurityOrganizationList, UUID> securityOrganizationListsDs;
    @Inject
    protected CollectionDatasource<SecurityPersonList, UUID> securityPersonListsDs;
    @Named("securityPersonTypesTable.load")
    protected Action securityPersonTypesTableLoad;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Table<SecurityOrganizationList> securityOrganizationListsTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    protected Map<UUID,String> hints;
    @Inject
    protected OrganizationService organizationService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        groupsDs.addItemChangeListener(e -> {
            groupsDsItemChangeListener(e);
        });
        securityHierarchyNodesTableCreate.setInitialValuesSupplier(()->ParamsMap.of("securityGroup",groupsDs.getItem()));
        securityOrganizationEligibilitiesTableCreate.setInitialValuesSupplier(()->ParamsMap.of("securityGroup",groupsDs.getItem()
                , "include", true));
        securityOrganizationListsTableCreate.setInitialValuesSupplier(()->ParamsMap.of("securityGroup",groupsDs.getItem()));
        securityPersonListsTableCreate.setInitialValuesSupplier(()->ParamsMap.of("securityGroup",groupsDs.getItem()));
        securityPersonTypesTableCreate.setInitialValuesSupplier(()->ParamsMap.of("securityGroup",groupsDs.getItem()));
        securityOrganizationListsDs.addCollectionChangeListener(e -> {
            securityOrganizationListsDsCollectionChangeListener(e);
        });

        securityOrganizationListsTable.addGeneratedColumn("organizationGroup.organizationName", new Table.ColumnGenerator<SecurityOrganizationList>() {

            @Override
            public Component generateCell(SecurityOrganizationList entity) {
                return getCell(entity);
            }

            protected HBoxLayout getCell(SecurityOrganizationList entity) {
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                PopupView popupView = componentsFactory.createComponent(PopupView.class);
                VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                popupView.setPopupContent(vBoxLayout);
                popupView.setHideOnMouseOut(false);
                popupView.setHeightFull();
                popupView.setAlignment(Alignment.BOTTOM_RIGHT);
                Label labelInPopup = componentsFactory.createComponent(Label.class);
                labelInPopup.setHtmlEnabled(true);
                labelInPopup.setValue(hints.containsKey(entity.getOrganizationGroup().getId())?hints.get((entity.getOrganizationGroup().getId())):null);
                vBoxLayout.add(labelInPopup);
                hBoxLayout.setWidthFull();
                hBoxLayout.setHeightAuto();
                Label label = componentsFactory.createComponent(Label.class);
                label.setHtmlEnabled(true);
                label.setValue("<pre>"+entity.getOrganizationGroup().getOrganizationName()+"</pre>");
                label.setHeightFull();
                label.setAlignment(Alignment.MIDDLE_LEFT);
                label.setHeight("15px");
                hBoxLayout.add(label);
                hBoxLayout.expand(label);
                hBoxLayout.add(popupView);
                Button button = componentsFactory.createComponent(Button.class);
                button.setAction(new BaseAction(entity.getId().toString()){
                    @Override
                    public void actionPerform(Component component) {
                        super.actionPerform(component);
                        popupView.setPopupVisible(true);
                    }
                });
                button.setIcon("font-icon:SITEMAP");
                button.setCaption("");
                hBoxLayout.add(button);
                return hBoxLayout;
            }
        });
    }

    protected void securityOrganizationListsDsCollectionChangeListener(CollectionDatasource.CollectionChangeEvent<SecurityOrganizationList, UUID> e) {
        hints = organizationService.getOrganizationPathToHintForList(e.getDs().getItems().stream().
                map(securityOrganizationList -> securityOrganizationList.getOrganizationGroup().getId()).
                collect(Collectors.toList()),CommonUtils.getSystemDate());
        securityOrganizationListsTable.repaint();
    }

    protected void groupsDsItemChangeListener(Datasource.ItemChangeEvent<Group> e) {
        securityHierarchyNodesTableCreate.setEnabled(e.getItem()!=null);
        securityOrganizationEligibilitiesTableCreate.setEnabled(e.getItem()!=null);
        securityOrganizationListsTableCreate.setEnabled(e.getItem()!=null);
        securityPersonListsTableCreate.setEnabled(e.getItem()!=null);
        securityPersonTypesTableCreate.setEnabled(e.getItem()!=null);
        securityPersonTypesTableLoad.setEnabled(e.getItem()!=null);
    }

    public void loadOrganizations(Component source) {
        UUID id = groupsTable.getSingleSelected().getId();
        if (id != null) {
            callStoredFunctionService.execSqlCallFunction("select tal.populate_organization_list( '" + id.toString() + "' )");
            securityOrganizationListsDs.refresh();
            showNotification(getMessage("success"));
        } else {
            showNotification(getMessage("not.success"));
        }
    }


    public void loadPersons(Component source) {

        UUID id = groupsTable.getSingleSelected().getId();
        if (id != null) {
            callStoredFunctionService.execSqlCallFunction("select tal.populate_organization_list( '" + id.toString() + "' )");
            callStoredFunctionService.execSqlCallFunction("select tal.populate_person_list('" + id.toString() + "' )");
            securityPersonListsDs.refresh();
            showNotification(getMessage("success"));
        } else {
            showNotification(getMessage("not.success"));
        }

    }

    public void load(Component source) {
        List<DicPersonType> types = commonService.getEntities(DicPersonType.class, "select e from tsadv$DicPersonType e ",
                new HashMap<>(), View.LOCAL);
        Collection<SecurityPersonType> securityPersonTypes = securityPersonTypesDs.getItems();
        for (DicPersonType type : types) {
            SecurityPersonType findPersonType = securityPersonTypes.stream().filter(securityPersonType ->
                    securityPersonType.getPersonType().getId().equals(type.getId())
            ).findFirst().orElse(null);
            if (findPersonType==null){
                SecurityPersonType securityPersonType = metadata.create(SecurityPersonType.class);
                securityPersonType.setPersonType(type);
                securityPersonType.setSecurityGroup(groupsDs.getItem());
                securityPersonTypesDs.addItem(securityPersonType);
            }
        }
        securityPersonTypesDs.commit();
        securityPersonTypesDs.refresh();

    }
}
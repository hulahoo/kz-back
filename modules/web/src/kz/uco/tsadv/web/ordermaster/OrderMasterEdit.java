package kz.uco.tsadv.web.ordermaster;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.MetadataMessageTools;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.model.OrderMaster;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntity;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntityProperty;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class OrderMasterEdit extends AbstractEditor<OrderMaster> {

    private MetadataMessageTools metadataMessageTools = AppBeans.get(MetadataMessageTools.class);

    private OrderMasterDictionary orderMasterDictionary = AppBeans.get(OrderMasterDictionary.class);

    private static List<String> systemProperties = new ArrayList<>();

    static {
        systemProperties.add("id");
        systemProperties.add("createTs");
        systemProperties.add("createdBy");
        systemProperties.add("version");
        systemProperties.add("updateTs");
        systemProperties.add("updatedBy");
        systemProperties.add("deleteTs");
        systemProperties.add("deletedBy");
    }

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CollectionDatasource<DicOrderType, UUID> dicOrderTypeDs;
    @Named("orderMasterEntityTable.create")
    private CreateAction orderMasterEntityTableCreate;
    @Named("orderMasterEntityTable.remove")
    private RemoveAction orderMasterEntityTableRemove;
    @Inject
    private GlobalConfig globalConfig;

    @Inject
    private CollectionDatasource<OrderMasterEntity, UUID> orderMasterEntityDs;

    @Named("fieldGroup.orderType")
    private PickerField orderTypeField;

    @Inject
    private CollectionDatasource<OrderMasterEntityProperty, UUID> entityPropertyDs;

    @Inject
    private Metadata metadata;

    private static Map<String, Locale> locales;

    private static List<String> languages;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        locales = globalConfig.getAvailableLocales();

        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            languages = Arrays.asList(langOrder.split(";"));
        } else {
            languages = new ArrayList<>();
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        orderMasterEntityTableCreate.setBeforeActionPerformedHandler(new Action.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                String excludeItems = orderMasterEntityDs.getItems()
                        .stream().map(OrderMasterEntity::getEntityName)
                        .collect(Collectors.joining(","));

                orderMasterEntityTableCreate.setInitialValues(
                        ParamsMap.of("order", orderMasterEntityDs.size() + 1));

                Map<String, Object> windowParams = new HashMap<>();
                windowParams.put("excludeItems", excludeItems);
                orderMasterEntityTableCreate.setWindowParams(windowParams);
                return true;
            }
        });
        orderMasterEntityTableCreate.setAfterCommitHandler(new CreateAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                OrderMasterEntity masterEntity = (OrderMasterEntity) entity;
                loadProperties(masterEntity);
            }
        });

        orderMasterEntityDs.addItemChangeListener(new Datasource.ItemChangeListener<OrderMasterEntity>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<OrderMasterEntity> e) {
                OrderMasterEntity orderMasterEntity = e.getItem();
                if (orderMasterEntity != null) {
                    if (BooleanUtils.isTrue(orderMasterEntity.getDefaultEntity())) {
                        orderMasterEntityTableRemove.setEnabled(false);
                    } else {
                        orderMasterEntityTableRemove.setEnabled(true);
                    }
                }
            }
        });

//        orderTypeField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                List<OrderMasterEntity> entities = new ArrayList<>(orderMasterEntityDs.getItems());
//                for (OrderMasterEntity entity : entities) {
//                    orderMasterEntityDs.removeItem(entity);
//                }
//
//                Object changeValue = e.getValue();
//                if (changeValue != null) {
//                    DicOrderType dicOrderType = (DicOrderType) changeValue;
//                    String code = dicOrderType.getCode();
//                    if (code != null && code.equalsIgnoreCase("HIRE")) {
//                        /**
//                         * load Person entity
//                         * */
//                        loadDefaultEntity("base$PersonExt", 1);
//
//                        /**
//                         * load Person entity
//                         * */
//                        loadDefaultEntity("base$AssignmentExt", 2);
//                    }
//                }
//            }
//        });

        if (PersistenceHelper.isNew(getItem())) {
            dicOrderTypeDs.setQuery("select e from tsadv$DicOrderType e " +
                    "where e.id not in (select m.orderType.id from tsadv$OrderMaster m)");
        } else {
            dicOrderTypeDs.setQuery("select e from tsadv$DicOrderType e " +
                    "where e.id not in (select m.orderType.id from tsadv$OrderMaster m) or e.id = '" + getItem().getOrderType().getId().toString() + "'");
        }
    }

    private void loadDefaultEntity(String entityName, Integer order) {
        OrderMasterDictionary.MetaClassModel metaClassModel = orderMasterDictionary.find(entityName);
        if (metaClassModel != null) {
            MetaClass metaClass = metaClassModel.getMetaClass();
            OrderMasterEntity masterEntity = metadata.create(OrderMasterEntity.class);
            masterEntity.setEntityName(metaClass.getName());
            masterEntity.setOrder(order);
            masterEntity.setOrderMaster(getItem());
            masterEntity.setDefaultEntity(Boolean.TRUE);
            fillLangValues(masterEntity, metaClass);

            orderMasterEntityDs.addItem(masterEntity);
            orderMasterEntityDs.setItem(masterEntity);

            loadProperties(masterEntity);
        }
    }

    private void loadProperties(OrderMasterEntity masterEntity) {
        Collection<OrderMasterEntityProperty> properties = entityPropertyDs.getItems();

        MetaClass metaClass = metadata.getSession().getClass(masterEntity.getEntityName());
        if (metaClass != null) {
            for (MetaProperty metaProperty : metaClass.getProperties()) {
                if (metaProperty != null && !metaProperty.getAnnotatedElement().isAnnotationPresent(com.haulmont.chile.core.annotations.MetaProperty.class)) {
                    if (metaProperty.getRange().isDatatype() || metaProperty.getRange().isEnum()) {
                        String propertyName = metaProperty.getName();

                        if (!systemProperties.contains(propertyName)) {
                            OrderMasterEntityProperty findProperty = properties.stream()
                                    .filter(p -> p.getName().equalsIgnoreCase(propertyName))
                                    .findFirst()
                                    .orElse(null);

                            if (findProperty == null) {
                                OrderMasterEntityProperty property = metadata.create(OrderMasterEntityProperty.class);
                                property.setName(propertyName);
                                property.setLangName1(propertyName);
                                property.setOrderMasterEntity(masterEntity);

                                fillLangValues(property, metaProperty, metaClass);
                                entityPropertyDs.addItem(property);
                            }
                        }
                    }
                }
            }
        }
    }

    private void fillLangValues(OrderMasterEntityProperty property, MetaProperty metaProperty, MetaClass entityMetaClass) {
        for (Map.Entry<String, Locale> entry : locales.entrySet()) {
            String language = entry.getValue().getLanguage();

            String value = metadataMessageTools.getPropertyCaption(entityMetaClass, metaProperty, entry.getValue());

            switch (languages.indexOf(language)) {
                case 0: {
                    property.setLangName1(value);
                    break;
                }
                case 1: {
                    property.setLangName2(value);
                    break;
                }
                case 2: {
                    property.setLangName3(value);
                    break;
                }
                case 3: {
                    property.setLangName4(value);
                    break;
                }
                case 4: {
                    property.setLangName5(value);
                    break;
                }
                default:
                    property.setLangName1(value);
            }
        }
    }

    private void fillLangValues(OrderMasterEntity masterEntity, MetaClass entityMetaClass) {
        for (Map.Entry<String, Locale> entry : locales.entrySet()) {
            String language = entry.getValue().getLanguage();

            String value = metadataMessageTools.getEntityCaption(entityMetaClass, entry.getValue());

            switch (languages.indexOf(language)) {
                case 0: {
                    masterEntity.setEntityNameLang1(value);
                    break;
                }
                case 1: {
                    masterEntity.setEntityNameLang2(value);
                    break;
                }
                case 2: {
                    masterEntity.setEntityNameLang3(value);
                    break;
                }
                case 3: {
                    masterEntity.setEntityNameLang4(value);
                    break;
                }
                case 4: {
                    masterEntity.setEntityNameLang5(value);
                    break;
                }
                default:
                    masterEntity.setEntityNameLang1(value);
            }
        }
    }
}
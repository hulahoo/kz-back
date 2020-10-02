package kz.uco.tsadv.web.ordermasterentity;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import kz.uco.tsadv.global.common.MetadataMessageTools;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntity;
import kz.uco.tsadv.web.ordermaster.OrderMasterDictionary;

import javax.inject.Inject;
import java.util.*;

public class OrderMasterEntityEdit extends AbstractEditor<OrderMasterEntity> {

    private OrderMasterDictionary orderMasterDictionary = AppBeans.get(OrderMasterDictionary.class);

    private MetadataMessageTools metadataMessageTools = AppBeans.get(MetadataMessageTools.class);

    @Inject
    private LookupField entityNamesLookup;

    @Inject
    private GlobalConfig globalConfig;

    private static Map<String, Locale> locales;

    private static List<String> languages;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Object excludeItemsObject = params.get("excludeItems");
        List<String> excludeEntities = null;
        if (excludeItemsObject != null) {
            excludeEntities = new ArrayList<>(Arrays.asList(excludeItemsObject.toString().split(",")));
        }

        Map<String, Object> entitiesOptionsMap = orderMasterDictionary.getEntitiesAsOptions();

        /**
         * Remove already exists entities in OptionsMap
         * */
        if (excludeEntities != null && !excludeEntities.isEmpty()) {
            for (String excludeEntity : excludeEntities) {
                entitiesOptionsMap.entrySet().removeIf(stringObjectEntry -> {
                    MetaClass metaClass = (MetaClass) stringObjectEntry.getValue();
                    return metaClass.getName().equalsIgnoreCase(excludeEntity);
                });
            }
        }

        entityNamesLookup.setOptionsMap(entitiesOptionsMap);

//        entityNamesLookup.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                MetaClass metaClass = (MetaClass) e.getValue();
//                if (metaClass != null) {
//                    OrderMasterEntity masterEntity = getItem();
//                    masterEntity.setEntityName(metaClass.getName());
//
//                    fillLangValues(masterEntity, metaClass);
//                }
//            }
//        });

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

        entityNamesLookup.setVisible(getItem().getEntityName() == null);
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
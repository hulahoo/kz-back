package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.app.ConfigStorageService;
import com.haulmont.cuba.core.config.AppPropertiesLocator;
import com.haulmont.cuba.core.config.AppPropertyEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;
import kz.uco.tsadv.modules.administration.appproperty.AppPropertyEntityDescription;
import kz.uco.tsadv.modules.administration.appproperty.ExtAppPropertyEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExtAppPropertiesDatasource extends CustomHierarchicalDatasource<ExtAppPropertyEntity, UUID> {
    @Override
    protected Collection<ExtAppPropertyEntity> getEntities(Map<String, Object> params) {
        List<ExtAppPropertyEntity> entities = loadAppPropertyEntities();

        String name = (String) params.get("name");
        if (StringUtils.isNotEmpty(name)) {
            entities = entities.stream()
                    .filter(it -> it.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return createEntitiesTree(entities);
    }

    public List<ExtAppPropertyEntity> loadAppPropertyEntities() {
        ConfigStorageService configStorageService = AppBeans.get(ConfigStorageService.class);
        List<AppPropertyEntity> entities = configStorageService.getAppProperties();

        AppPropertiesLocator appPropertiesLocator = AppBeans.get(AppPropertiesLocator.class);
        entities.addAll(appPropertiesLocator.getAppProperties());

        List<ExtAppPropertyEntity> extEntities = getExtAppPropertyEntities(entities);

        return extEntities;
    }

    List<ExtAppPropertyEntity> createEntitiesTree(List<ExtAppPropertyEntity> entities) {
        List<ExtAppPropertyEntity> resultList = new ArrayList<>();
        for (ExtAppPropertyEntity entity : entities) {
            String[] parts = entity.getName().split("\\.");
            ExtAppPropertyEntity parent = null;
            for (int i = 0; i < parts.length; i++) {
                String[] currParts = Arrays.copyOfRange(parts, 0, i + 1);
                String part = parts[i];
                if (i < parts.length - 1) {
                    Optional<ExtAppPropertyEntity> parentOpt = resultList.stream()
                            .filter(e -> {
                                return e.getCategory() && nameEquals(currParts, e);
                            })
                            .findFirst();
                    if (parentOpt.isPresent()) {
                        parent = parentOpt.get();
                    } else {
                        ExtAppPropertyEntity categoryEntity = new ExtAppPropertyEntity();
                        categoryEntity.setExtParent(parent);
                        categoryEntity.setName(part);
                        resultList.add(categoryEntity);
                        parent = categoryEntity;
                    }

                } else {
                    entity.setExtParent(parent);
                    entity.setCategory(false);
                    resultList.add(entity);
                }
            }
        }
        // remove duplicates from global configs
        for (Iterator<ExtAppPropertyEntity> iter = resultList.iterator(); iter.hasNext(); ) {
            ExtAppPropertyEntity entity = iter.next();
            resultList.stream()
                    .filter(e -> e != entity && nameParts(e).equals(nameParts(entity)))
                    .findFirst()
                    .ifPresent(e -> iter.remove());
        }

        return resultList;
    }

    private List<ExtAppPropertyEntity> getExtAppPropertyEntities(List<AppPropertyEntity> appPropertyEntities) {
        List<ExtAppPropertyEntity> result = new ArrayList<ExtAppPropertyEntity>();
        for (AppPropertyEntity propertyEntity : appPropertyEntities) {
            ExtAppPropertyEntity extAppPropertyEntity = new ExtAppPropertyEntity();
            BeanUtils.copyProperties(propertyEntity, extAppPropertyEntity);
            LoadContext<AppPropertyEntityDescription> loadContext =
                    LoadContext.create(AppPropertyEntityDescription.class).setQuery(
                            LoadContext.createQuery(
                                    "select c from tsadv_AppPropertyEntityDescription c " +
                                            "where c.appPropertyName = :propertyName ")
                                    .setParameter("propertyName", propertyEntity.getName())
                    ).setView("appPropertyEntityDescription-view");
            AppPropertyEntityDescription description = AppBeans.get(DataManager.class)
                    .load(loadContext);
            if (description != null) {
                extAppPropertyEntity.setDescription(description);
            }
            result.add(extAppPropertyEntity);
        }
        return result;
    }

    private boolean nameEquals(String[] nameParts, ExtAppPropertyEntity entity) {
        ExtAppPropertyEntity e = entity;
        for (int i = nameParts.length - 1; i >= 0; i--) {
            String name = nameParts[i];
            if (!e.getName().equals(name))
                return false;
            if (i > 0) {
                e = e.getExtParent();
                if (e == null)
                    return false;
            }
        }
        return true;
    }

    private List<String> nameParts(ExtAppPropertyEntity entity) {
        List<String> list = new ArrayList<>();
        ExtAppPropertyEntity e = entity;
        while (e != null) {
            list.add(e.getName());
            e = e.getExtParent();
        }
        Collections.reverse(list);
        return list;
    }

}

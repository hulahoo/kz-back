package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(DynamicAttributesService.NAME)
public class DynamicAttributesServiceBean implements DynamicAttributesService {

    @Inject
    private CommonService commonService;

    @Override
    public CategoryAttributeValue getCategoryAttributeValue(Category category, BaseUuidEntity entity, String categoryAttributeName) {
        CategoryAttribute categoryAttribute = category.getCategoryAttrs().stream().filter(c -> c.getCode().equals(categoryAttributeName)).findFirst().orElse(null);
        if (categoryAttribute != null) {
            String queryString = "select e from sys$CategoryAttributeValue e" +
                    " where e.categoryAttribute.id = :categoryAttributeId" +
                    "   and e.entity.entityId = :entityId";
            Map<String, Object> params = new HashMap<>();
            params.put("entityId", entity.getId());
            params.put("categoryAttributeId", categoryAttribute.getId());
            return commonService.getEntity(CategoryAttributeValue.class, queryString, params, View.LOCAL);
        }
        return null;
    }

    @Override
    public CategoryAttributeValue getCategoryAttributeValue(BaseUuidEntity entity, CategoryAttribute categoryAttribute) {
        if (categoryAttribute != null) {
            String queryString = "select e from sys$CategoryAttributeValue e" +
                    " where e.categoryAttribute.id = :categoryAttributeId" +
                    "   and e.entity.entityId = :entityId";
            Map<String, Object> params = new HashMap<>();
            params.put("entityId", entity.getId());
            params.put("categoryAttributeId", categoryAttribute.getId());
            return commonService.getEntity(CategoryAttributeValue.class, queryString, params, View.LOCAL);
        }
        return null;
    }

    @Override
    public Category getCategory(String name, String entityType) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("entityType", entityType);
        Category entity = commonService.getEntity(Category.class, "select e" +
                        "   from sys$Category e " +
                        "   where e.entityType =:entityType and e.name = :name",
                map, View.LOCAL);
        return entity;
    }

    @Override
    public List<CategoryAttribute> getCategoryAttributes(Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryId", category.getId());
        List<CategoryAttribute> list = commonService.getEntities(CategoryAttribute.class, "select e" +
                        "   from sys$CategoryAttribute e " +
                        "   where e.category.id =:categoryId",
                map, View.LOCAL);
        return list;
    }

    @Override
    public String getLocaleTranslate(String caption, Category category, String entityType) {
        Map<String, Object> map = new HashMap<>();
        map.put("caption", caption);
        map.put("categoryId", category.getId());
        map.put("entityType", entityType);
        CategoryAttribute categoryAttribute = commonService.getEntity(CategoryAttribute.class, "select e" +
                        "  from sys$CategoryAttribute e" +
                        "  where e.categoryEntityType =:entityType " +
                        "  and e.name =:caption and e.category.id = :categoryId",
                map, View.LOCAL);
        if (categoryAttribute != null) {
            return categoryAttribute.getLocaleName();
        }
        return null;
    }
}
package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;

import java.util.List;

public interface DynamicAttributesService {
    String NAME = "tsadv_DynamicAttributesService";

    CategoryAttributeValue getCategoryAttributeValue(Category category, BaseUuidEntity entity, String categoryAttributeName);

    CategoryAttributeValue getCategoryAttributeValue(BaseUuidEntity entity, CategoryAttribute categoryAttribute);
        
    Category getCategory(String name, String entityType);

    List<CategoryAttribute> getCategoryAttributes(Category category);

    String getLocaleTranslate(String caption, Category category, String entityType);
}
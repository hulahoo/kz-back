package kz.uco.tsadv.web.modules.learning.dictionary.diccategory;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import kz.uco.base.web.abstraction.six.AbstractDictionaryBrowse;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.UUID;

public class DicCategoryBrowse extends AbstractDictionaryBrowse<DicCategory> {

    private static final String IMAGE_CELL_HEIGHT = "40px";

    @Inject
    private HierarchicalDatasource<DicCategory, UUID> dicCategoriesDs;

    public Component generateCategoryImageCell(DicCategory dicCategory) {
        return Utils.getCourseCategoryImageEmbedded(dicCategory, IMAGE_CELL_HEIGHT, null);
    }

    public HierarchicalDatasource<DicCategory, UUID> getDicCategoriesDs() {
        return dicCategoriesDs;
    }
}
package kz.uco.tsadv.web.screens.dicdrivercategory;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicDriverCategory;

@UiController("tsadv_DicDriverCategory.browse")
@UiDescriptor("dic-driver-category-browse.xml")
@LookupComponent("dicDriverCategoriesTable")
@LoadDataBeforeShow
public class DicDriverCategoryBrowse extends StandardLookup<DicDriverCategory> {
}
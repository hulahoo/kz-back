package kz.uco.tsadv.web.screens.dicdrivercategory;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicDriverCategory;

@UiController("tsadv_DicDriverCategory.edit")
@UiDescriptor("dic-driver-category-edit.xml")
@EditedEntityContainer("dicDriverCategoryDc")
@LoadDataBeforeShow
public class DicDriverCategoryEdit extends StandardEditor<DicDriverCategory> {
}
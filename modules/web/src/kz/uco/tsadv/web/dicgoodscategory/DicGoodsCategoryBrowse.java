package kz.uco.tsadv.web.dicgoodscategory;

import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.abstraction.AbstractDictionary68Browse;
import kz.uco.tsadv.modules.recognition.dictionary.DicGoodsCategory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class DicGoodsCategoryBrowse extends AbstractDictionary68Browse<DicGoodsCategory> {

    @Inject
    protected GroupDatasource<DicGoodsCategory, UUID> dicGoodsCategoriesDs;

    @Inject
    protected CommonService commonService;

    @Named("dicGoodsCategoriesTable.remove")
    private RemoveAction dicGoodsCategoriesTableRemove;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        dicGoodsCategoriesTableRemove.setBeforeActionPerformedHandler(() -> {
            Long count = commonService.getCount("select count(*) from TSADV_GOODS e where e.category_id = ?1",
                    Collections.singletonMap(1, dicGoodsCategoriesDs.getItem().getId()));
            if (count >= 1) {
                dicGoodsCategoriesTableRemove.setConfirmationMessage(String.format(getMessage("goodsCategory.remove.confirmation"),
                        count, count > 1 ? getMessage("confirm.goods") : getMessage("confirm.one.good")));
            } else {
                dicGoodsCategoriesTableRemove.setConfirmationMessage(messages.getMessage("com.haulmont.cuba.gui", "dialogs.Confirmation.Remove"));
            }
            return true;
        });

    }
}
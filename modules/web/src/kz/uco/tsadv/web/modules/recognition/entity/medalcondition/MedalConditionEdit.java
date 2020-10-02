package kz.uco.tsadv.web.modules.recognition.entity.medalcondition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.recognition.Medal;
import kz.uco.tsadv.modules.recognition.MedalCondition;

import javax.inject.Named;
import java.util.Map;

public class MedalConditionEdit extends AbstractEditor<MedalCondition> {
    @Named("fieldGroup.medalQuantity")
    protected Field medalQuantityField;
    @Named("fieldGroup.quality")
    protected Field qualityField;
    @Named("fieldGroup.childMedal")
    protected Field childMedalField;
    @Named("fieldGroup.qualityQuantity")
    protected Field qualityQuantityField;
    @Named("fieldGroup.childMedal")
    protected PickerField childMedalPickerField;

    protected Medal medal;
    @Override
    protected void initNewItem(MedalCondition item) {
        super.initNewItem(item);
        item.setMedal(medal);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("inVisibleChildFields")) {
            medal = params.containsKey("medal") ? (Medal) params.get("medal") : null;
            childMedalField.setVisible(false);
            medalQuantityField.setVisible(false);
        }else if (params.containsKey("inVisibleQualityFields")) {
            medal = params.containsKey("medal") ? (Medal) params.get("medal") : null;
            qualityField.setVisible(false);
            qualityQuantityField.setVisible(false);
        }
        childMedalPickerField.getLookupAction().setLookupScreenParams(ParamsMap.of("hideTabs", true));
    }
}
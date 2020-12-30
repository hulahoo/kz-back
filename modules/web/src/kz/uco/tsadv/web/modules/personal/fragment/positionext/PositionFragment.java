package kz.uco.tsadv.web.modules.personal.fragment.positionext;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.GradeRuleValue;
import kz.uco.tsadv.modules.personal.model.PositionExt;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_PositionFragment")
@UiDescriptor("position-fragment.xml")
public class PositionFragment extends ScreenFragment {

    @Inject
    protected InstanceContainer<PositionExt> positionDc;
    @Inject
    protected InstanceContainer<GradeRuleValue> gradeGroupValueDc;
    @Inject
    protected Form form;

    @Inject
    protected CommonService commonService;

    @Subscribe(id = "positionDc", target = Target.DATA_CONTAINER)
    protected void onPositionDcItemChange1(InstanceContainer.ItemChangeEvent<PositionExt> event) {
        refreshGradeRuleValue();

        setLinkButtons();
    }

    protected void setLinkButtons() {
        PositionExt position = positionDc.getItem();
        if (position != null) {
            String componentId = "positionNameLang%dReducted";
            for (int i = 1; i <= 3; i++) {
                String format = String.format(componentId, i);
                LinkButton linkButton = (LinkButton) form.getComponentNN(format);
                linkButton.setCaption(position.getValue(format));
                linkButton.setAction(new BaseAction(format)
                        .withHandler(actionPerformedEvent -> openPosition(position)));
            }
        }
    }

    protected void openPosition(PositionExt position) {
        getHostScreen().getWindow().openEditor(position, WindowManager.OpenType.THIS_TAB);
    }


    private void refreshGradeRuleValue() {
        Map<String, Object> paramsMap = new HashMap<>();
        PositionExt position = positionDc.getItem();
        GradeRuleValue gradeRuleValue = null;
        if (position != null) {
            paramsMap.put("gradeGroupId", position.getGradeGroup() == null ? null : position.getGradeGroup().getId());
            paramsMap.put("gradeRuleId", position.getGradeRule() == null ? null : position.getGradeRule().getId());
            paramsMap.put("systemDate", CommonUtils.getSystemDate());

            List<GradeRuleValue> grv = commonService.getEntities(GradeRuleValue.class,
                    "select e " +
                            "    from tsadv$GradeRuleValue e " +
                            "   where e.gradeGroup.id = :gradeGroupId " +
                            "     and e.gradeRule.id = :gradeRuleId " +
                            "     and :systemDate between e.startDate and e.endDate",
                    paramsMap,
                    "gradeRuleValue.edit");

            if (!grv.isEmpty()) gradeRuleValue = grv.get(0);
        }

        gradeGroupValueDc.setItem(gradeRuleValue);
    }
}
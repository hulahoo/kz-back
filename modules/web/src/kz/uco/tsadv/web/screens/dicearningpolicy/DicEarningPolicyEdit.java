package kz.uco.tsadv.web.screens.dicearningpolicy;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicEarningPolicy;

@UiController("tsadv_DicEarningPolicy.edit")
@UiDescriptor("dic-earning-policy-edit.xml")
@EditedEntityContainer("dicEarningPolicyDc")
@LoadDataBeforeShow
public class DicEarningPolicyEdit extends StandardEditor<DicEarningPolicy> {
}
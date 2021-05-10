package kz.uco.tsadv.web.screens.dicearningpolicy;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicEarningPolicy;

@UiController("tsadv_DicEarningPolicy.browse")
@UiDescriptor("dic-earning-policy-browse.xml")
@LookupComponent("dicEarningPoliciesTable")
@LoadDataBeforeShow
public class DicEarningPolicyBrowse extends StandardLookup<DicEarningPolicy> {
}
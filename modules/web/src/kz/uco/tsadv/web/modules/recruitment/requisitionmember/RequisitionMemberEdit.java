package kz.uco.tsadv.web.modules.recruitment.requisitionmember;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.modules.recruitment.model.RequisitionMember;

import javax.inject.Inject;
import java.util.Map;

public class RequisitionMemberEdit extends AbstractEditor<RequisitionMember> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private Datasource<RequisitionMember> requisitionMemberDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Utils.customizeLookup(fieldGroup.getField("personGroupF").getComponent(), "base$PersonGroup.browse", WindowManager.OpenType.DIALOG, params);
    }

}
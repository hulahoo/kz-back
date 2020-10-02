package kz.uco.tsadv.web.securitypersontype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.security.SecurityPersonType;

import javax.inject.Named;

public class SecurityPersonTypeEdit extends AbstractEditor<SecurityPersonType> {
    @Named("fieldGroup.securityGroup")
    private PickerField securityGroupField;

    @Override
    public void ready() {
        super.ready();
        securityGroupField.setEditable(getItem().getSecurityGroup() == null);
    }
}
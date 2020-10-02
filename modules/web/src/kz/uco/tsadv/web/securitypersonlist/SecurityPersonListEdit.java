package kz.uco.tsadv.web.securitypersonlist;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.security.SecurityPersonList;

import javax.inject.Named;

public class SecurityPersonListEdit extends AbstractEditor<SecurityPersonList> {
    @Named("fieldGroup.securityGroup")
    private PickerField securityGroupField;

    @Override
    public void ready() {
        super.ready();
        securityGroupField.setEditable(getItem().getSecurityGroup() == null);
    }
}
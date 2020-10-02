package kz.uco.tsadv.web.securityorganizationlist;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.security.SecurityOrganizationList;

import javax.inject.Named;

public class SecurityOrganizationListEdit extends AbstractEditor<SecurityOrganizationList> {
    @Named("fieldGroup.securityGroup")
    private PickerField securityGroupField;

    @Override
    public void ready() {
        super.ready();
        securityGroupField.setEditable(getItem().getSecurityGroup() == null);
    }
}
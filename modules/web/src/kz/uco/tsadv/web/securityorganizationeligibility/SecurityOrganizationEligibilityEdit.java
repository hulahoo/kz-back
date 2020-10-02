package kz.uco.tsadv.web.securityorganizationeligibility;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.security.SecurityOrganizationEligibility;

import javax.inject.Named;

public class SecurityOrganizationEligibilityEdit extends AbstractEditor<SecurityOrganizationEligibility> {
    @Named("fieldGroup.securityGroup")
    private PickerField securityGroupField;

    @Override
    public void ready() {
        super.ready();
        securityGroupField.setEditable(getItem().getSecurityGroup() == null);
    }
}
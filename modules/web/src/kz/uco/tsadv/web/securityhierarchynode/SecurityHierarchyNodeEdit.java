package kz.uco.tsadv.web.securityhierarchynode;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.security.SecurityHierarchyNode;

import javax.inject.Named;

public class SecurityHierarchyNodeEdit extends AbstractEditor<SecurityHierarchyNode> {
    @Named("fieldGroup.securityGroup")
    private PickerField securityGroupField;

    @Override
    public void ready() {
        super.ready();
        securityGroupField.setEditable(getItem().getSecurityGroup() == null);
    }
}
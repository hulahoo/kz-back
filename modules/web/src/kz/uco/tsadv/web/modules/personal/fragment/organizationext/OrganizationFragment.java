package kz.uco.tsadv.web.modules.personal.fragment.organizationext;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;

import javax.inject.Inject;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_OrganizationFragment")
@UiDescriptor("organization-fragment.xml")
public class OrganizationFragment extends ScreenFragment {

    @Inject
    protected InstanceContainer<OrganizationExt> organizationDc;
    @Inject
    protected Form form;

    @Subscribe(id = "organizationDc", target = Target.DATA_CONTAINER)
    protected void onOrganizationDcItemChange(InstanceContainer.ItemChangeEvent<OrganizationExt> event) {
        setLinkButtons();
    }

    protected void setLinkButtons() {
        OrganizationExt organization = organizationDc.getItem();
        if (organization != null) {
            String componentId = "organizationNameLang%dReducted";
            for (int i = 1; i <= 3; i++) {
                String format = String.format(componentId, i);
                LinkButton linkButton = (LinkButton) form.getComponentNN(format);
                linkButton.setCaption(organization.getValue(format));
                linkButton.setAction(new BaseAction(format)
                        .withHandler(actionPerformedEvent -> openOrganization(organization)));
            }
        }
    }

    protected void openOrganization(OrganizationExt organization) {
        getHostScreen().getWindow().openEditor(organization, WindowManager.OpenType.THIS_TAB);
    }
}
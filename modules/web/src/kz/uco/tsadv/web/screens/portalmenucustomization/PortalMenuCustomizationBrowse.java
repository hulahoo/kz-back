package kz.uco.tsadv.web.screens.portalmenucustomization;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.security.entity.PermissionVariant;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.administration.PortalMenuCustomization;
import kz.uco.tsadv.modules.administration.enums.PortalAvailability;

import javax.inject.Inject;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_PortalMenuCustomization.browse")
@UiDescriptor("portal-menu-customization-browse.xml")
@LookupComponent("portalMenuCustomizationsTable")
@LoadDataBeforeShow
public class PortalMenuCustomizationBrowse extends StandardLookup<PortalMenuCustomization> {
    @Inject
    protected CollectionContainer<PortalMenuCustomization> portalMenuCustomizationsDc;
    @Inject
    protected Label<String> menuName;
    @Inject
    protected VBoxLayout selectedScreenPanel;
    @Inject
    protected Messages messages;
    @Inject
    protected UiComponents uiComponents;

    @Subscribe("portalMenuCustomizationsTable")
    protected void onPortalMenuCustomizationsTableSelection(Table.SelectionEvent<PortalMenuCustomization> event) {
        selectedScreenPanel.setVisible(!event.getSelected().isEmpty());
    }

    @Subscribe("close")
    protected void onCloseClick(Button.ClickEvent event) {
        this.close(StandardOutcome.CLOSE);
    }

    @Subscribe("saveAndClose")
    protected void onSaveAndCloseClick(Button.ClickEvent event) {
        portalMenuCustomizationsDc.getItems().forEach(getScreenData().getDataContext()::merge);
        getScreenData().getDataContext().commit();
        this.close(StandardOutcome.COMMIT);
    }

    @Install(to = "portalMenuCustomizationsTable.portalAvailability", subject = "formatter")
    protected String portalMenuCustomizationsTablePortalAvailabilityFormatter(PortalAvailability availability) {
        return messages.getMessage(availability);
    }

    @Install(to = "portalMenuCustomizationsTable.active", subject = "columnGenerator")
    protected Component portalMenuCustomizationsTableActiveColumnGenerator(PortalMenuCustomization portalMenuCustomization) {
        Boolean active = portalMenuCustomization.getActive();
        String labelValue = "<span class=\"role-permission-" + (active ? "green" : "red") + "\">" +
                messages.getMessage(active ? PermissionVariant.ALLOWED : PermissionVariant.DISALLOWED) + "</span>";

        @SuppressWarnings("UnstableApiUsage") Label<String> label = uiComponents.create(Label.TYPE_STRING);
        label.setHtmlEnabled(true);
        label.setValue(labelValue);
        return label;
    }
}
package kz.uco.tsadv.web.screens.personpayslip;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Filter;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.config.AccessConfig;
import kz.uco.tsadv.modules.personal.model.PersonPayslip;
import kz.uco.tsadv.service.UserService;

import javax.inject.Inject;

@UiController("tsadv_PersonPayslip.browse")
@UiDescriptor("person-payslip-browse.xml")
@LookupComponent("personPayslipsTable")
@LoadDataBeforeShow
public class PersonPayslipBrowse extends StandardLookup<PersonPayslip> {

    @Inject
    private AccessConfig accessConfig;

    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private UserService userService;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private Filter filter;
    @Inject
    private GroupTable<PersonPayslip> personPayslipsTable;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        checkUserAccess();
    }

    public void generatorName(PersonPayslip item, String columnId) {
        exportDisplay.show(item.getFile(), ExportFormat.OCTET_STREAM);
    }

    protected void checkUserAccess(){
        String delimiter = ";";
        String accessRoles = accessConfig.getPersonPayslipAccessRoles();
        String[] acessRolesArray = accessRoles.split(delimiter);

        if(!userService.hasAnyRole(acessRolesArray)){
            String noAccessMessage = messageBundle.getMessage("noAccess");
            notifications.create(Notifications.NotificationType.WARNING).withCaption(noAccessMessage).show();
            close(StandardOutcome.DISCARD);
        }else{
            filter.setVisible(true);
            personPayslipsTable.setVisible(true);
        }
    }
}
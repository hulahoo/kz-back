package kz.uco.tsadv.web.modules.personal.frames;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68.HierarchyElementBrowse;
import kz.uco.tsadv.web.modules.personal.organizationhruser.OrganizationHrUserEdit;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author Adilbekov Yernar
 */
@Deprecated
public class OrganizationFrame extends AbstractFrame {

    protected HierarchyElementBrowse browse;

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected HBoxLayout actionsPane;
    @Inject
    protected Button editBtn;
    @Inject
    protected ButtonsPanel competenceOrgButtonsPanel;
    @Inject
    protected ButtonsPanel orgHrUsersButtonsPanel;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionDatasource<OrganizationHrUser, UUID> orgHrUsersDs;
    @Inject
    protected Datasource<OrganizationExt> organizationDs;
    @Inject
    protected HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;
    @Inject
    protected Table<OrganizationHrUser> orgHrUsersTable;
    @Inject
    private ComponentsFactory componentsFactory;

    protected OrgAnalytics analytics = null;

    @Named("fieldGroup.organizationNameLang1Reducted")
    protected EntityLinkField organizationNameLang1ReductedField;

    @Named("fieldGroup.organizationNameLang2Reducted")
    protected EntityLinkField organizationNameLang2ReductedField;

    @Named("fieldGroup.organizationNameLang3Reducted")
    protected EntityLinkField organizationNameLang3ReductedField;

    @Named("fieldGroup.organizationNameLang4Reducted")
    protected EntityLinkField organizationNameLang4ReductedField;

    @Named("fieldGroup.organizationNameLang5Reducted")
    protected EntityLinkField organizationNameLang5ReductedField;


    @Override
    public void init(Map<String, Object> params) {
        //this.editable(false);

        organizationNameLang1ReductedField.setScreenCloseListener((window, actionId) -> {
            organizationDs.refresh();
            hierarchyElementsDs.refresh();
        });

        organizationNameLang2ReductedField.setScreenCloseListener((window, actionId) -> {
            organizationDs.refresh();
            hierarchyElementsDs.refresh();
        });

        organizationNameLang3ReductedField.setScreenCloseListener((window, actionId) -> {
            organizationDs.refresh();
            hierarchyElementsDs.refresh();
        });

    }

    public void setTab(String id) {
        tabSheet.setTab(id);
    }

    public void setTabVisible(String id, boolean visible){
        tabSheet.getTab(id).setVisible(visible);
    }

    public void editable(boolean isEdit) {
        isEdit = true;
        fieldGroup.setEditable(isEdit);
        actionsPane.setVisible(isEdit);
        editBtn.setVisible(!isEdit);
        //competenceOrgButtonsPanel.setVisible(isEdit);
        orgHrUsersButtonsPanel.setVisible(isEdit);
    }



    public void save() {
        browse.save(fieldGroup);
    }

    public void edit() {
        browse.edit(true);
        editable(true);
    }

    public void cancel() {
        editable(false);
        browse.cancel();
    }

    public void setHierarchyElementBrowse(HierarchyElementBrowse browse) {
        this.browse = browse;
    }

    public void addHrUser() {
        OrganizationHrUser organizationHrUser = metadata.create(OrganizationHrUser.class);
        organizationHrUser.setOrganizationGroup(organizationDs.getItem().getGroup());
        openCastomizeEditor(organizationHrUser);
    }

    public void editHrUser() {
        openCastomizeEditor(orgHrUsersDs.getItem());
    }

    private void openCastomizeEditor(OrganizationHrUser organizationHrUser) {
        Map<String, Object> map = new HashMap<>();
        List<UserExt> excludedUsersList = new ArrayList<>();

        for (OrganizationHrUser orgHrUser : orgHrUsersDs.getItems()) {
            if (!orgHrUser.getUser().equals(organizationHrUser.getUser())) {
                excludedUsersList.add(orgHrUser.getUser());
            }
        }
        map.put("excludedUsers", excludedUsersList);
        OrganizationHrUserEdit orgHrUserEdit = (OrganizationHrUserEdit) openEditor(
                "tsadv$OrganizationHrUser.edit",
                organizationHrUser,
                WindowManager.OpenType.DIALOG,
                map, orgHrUsersDs);
        orgHrUserEdit.addCloseListener(actionId -> orgHrUsersTable.repaint());
    }

}
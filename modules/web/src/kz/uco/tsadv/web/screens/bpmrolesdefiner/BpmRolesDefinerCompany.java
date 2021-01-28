package kz.uco.tsadv.web.screens.bpmrolesdefiner;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_BpmRolesDefinerCompany")
@UiDescriptor("bpm-roles-definer-company.xml")
@LoadDataBeforeShow
public class BpmRolesDefinerCompany extends Screen {

    @Inject
    protected CollectionContainer<DicCompany> companyDc;
    @Inject
    protected CollectionLoader<BpmRolesDefiner> bpmRolesDefinersDl;
    @Inject
    protected Metadata metadata;
    @Named("bpmRolesDefinersTable.create")
    protected CreateAction<BpmRolesDefiner> bpmRolesDefinersTableCreate;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        bpmRolesDefinersDl.setParameter("companyId", null);
    }

    @Subscribe(id = "companyDc", target = Target.DATA_CONTAINER)
    protected void onCompanyDcItemChange(InstanceContainer.ItemChangeEvent<DicCompany> event) {
        DicCompany company = event.getItem();
        bpmRolesDefinersDl.setParameter("companyId", company != null ? company.getId() : null);
        bpmRolesDefinersDl.load();
    }

    @Install(to = "bpmRolesDefinersTable.create", subject = "newEntitySupplier")
    protected BpmRolesDefiner bpmRolesDefinersTableCreateNewEntitySupplier() {
        BpmRolesDefiner bpmRolesDefiner = metadata.create(BpmRolesDefiner.class);
        bpmRolesDefiner.setCompany(companyDc.getItemOrNull());
        return bpmRolesDefiner;
    }

}
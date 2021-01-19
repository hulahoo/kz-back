package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;

@UiController("tsadv$InsuredPerson.browse")
@UiDescriptor("insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class InsuredPersonBrowse extends StandardLookup<InsuredPerson> {
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;

    @Subscribe
    public void onInit(InitEvent event) {
        insuredPersonsDl.setParameter("myCompany",null);
    }

    public void setParameter(DicCompany company) {
        insuredPersonsDl.setParameter("myCompany", company);
        insuredPersonsDl.load();
    }
}
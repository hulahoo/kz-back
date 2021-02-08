package kz.uco.tsadv.web.screens.persongroupext;

import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;

@UiController("base$PersonGroupExtMIC")
@UiDescriptor("person-group-ext-mic.xml")
@LookupComponent("personGroupExtTable")
@LoadDataBeforeShow
public class PersonGroupExtMIC extends StandardLookup<PersonGroupExt> {

    @Inject
    private CollectionContainer<PersonGroupExt> personGroupExtDc;
    @Inject
    private CollectionLoader<PersonGroupExt> personGroupExtDl;
    @Inject
    private CommonService commonService;

    @Subscribe
    public void onInit(InitEvent event) {
        personGroupExtDl.setParameter("session$systemDate", CommonUtils.getSystemDate());
    }

}
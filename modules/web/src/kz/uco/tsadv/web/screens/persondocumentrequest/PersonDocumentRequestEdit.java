package kz.uco.tsadv.web.screens.persondocumentrequest;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.model.PersonDocumentRequest;

import javax.inject.Inject;

@UiController("tsadv_PersonDocumentRequest.edit")
@UiDescriptor("person-document-request-edit.xml")
@EditedEntityContainer("personDocumentRequestDc")
@LoadDataBeforeShow
public class PersonDocumentRequestEdit extends StandardEditor<PersonDocumentRequest> {
    @Inject
    protected CollectionLoader<DicDocumentType> dicDocumentTypesDl;

    @Inject
    protected CommonService commonService;

    @Subscribe
    protected void onInit(InitEvent event) {
        MapScreenOptions options = (MapScreenOptions) event.getOptions();
        if (options.getParams() != null && options.getParams().containsKey("foreigner")) {
            dicDocumentTypesDl.setQuery("select e from tsadv$DicDocumentType e where e.foreigner = :foreigner");
            dicDocumentTypesDl.setParameter("foreigner", options.getParams().get("fromPersonData"));
        }
    }

    @Subscribe
    protected void onInitEntity(InitEntityEvent<PersonDocumentRequest> event) {
        event.getEntity().setStatus(commonService.getEntity(DicApprovalStatus.class,"PROJECT"));
    }


}
package kz.uco.tsadv.web.screens.scormquestionmapping;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.ScormQuestionMapping;

import javax.inject.Inject;

@UiController("tsadv_ScormQuestionMapping.edit")
@UiDescriptor("scorm-question-mapping-edit.xml")
@EditedEntityContainer("scormQuestionMappingDc")
@LoadDataBeforeShow
public class ScormQuestionMappingEdit extends StandardEditor<ScormQuestionMapping> {
    @Inject
    protected InstanceContainer<ScormQuestionMapping> scormQuestionMappingDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        if (PersistenceHelper.isNew(scormQuestionMappingDc.getItem())) {
            scormQuestionMappingDc.getItem().setSerialNumber(((Integer) getMaxSerialNumber()));
        }
    }

    protected Object getMaxSerialNumber() {
        Object object = commonService.emNativeQuerySingleResult(Object.class,
                "select max(e.serial_number) from TSADV_SCORM_QUESTION_MAPPING e ", null);
        if (object != null) {
            return object;
        }
        return null;
    }

}
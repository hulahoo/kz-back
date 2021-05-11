package kz.uco.tsadv.web.screens.scormquestionmapping;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.LearningObject;
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
            scormQuestionMappingDc.getItem().setSerialNumber(getMaxSerialNumber(scormQuestionMappingDc.getItem().getLearningObject()) != null
                    ? ((Integer) getMaxSerialNumber(scormQuestionMappingDc.getItem().getLearningObject())) + 1
                    : 1);
        }
    }

    protected Object getMaxSerialNumber(LearningObject learningObject) {
        Object object = commonService.emNativeQuerySingleResult(Object.class,
                "select max(e.serial_number) from TSADV_SCORM_QUESTION_MAPPING e " +
                        " where e.learning_object_id = ?1 ",
                ParamsMap.of("1", learningObject.getId()));
        if (object != null) {
            return object;
        }
        return null;
    }

}
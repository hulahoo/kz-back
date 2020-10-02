package kz.uco.tsadv.web.modules.personal.case_;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.base.service.common.CommonService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class Caseframe extends EditableFrame {
    @Inject
    private CommonService commonService;

    @Inject
    private Metadata metadata;

    @Inject
    private ButtonsPanel buttonsPanel;

    @Named("casesTable.create")
    private CreateAction casesTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        casesTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    public void onGenerateClick() {
        CollectionDatasource<Case, UUID> casesDs = (CollectionDatasource<Case, UUID>) getDsContext().get("casesDs");
        for (Case c : new ArrayList<>(casesDs.getItems())) {
            casesDs.removeItem(c);
        }
        for (String lang : CommonUtils.getSystemLangsMap().values()) {
            List<CaseType> caseTypeList = commonService.getEntities(CaseType.class,
                    "select e from tsadv$CaseType e where e.language = :lang",
                    Collections.singletonMap("lang", lang), "_local");
            for (CaseType caseType : caseTypeList) {
                Case case_ = metadata.create(Case.class);
                case_.setCaseType(caseType);
                case_.setLanguage(lang);
                if (getDsContext().get("jobDs") != null) {
                    case_.setJobGroup(((Job) getDsContext().get("jobDs").getItem()).getGroup());
                } else if (getDsContext().get("organizationDs") != null) {
                    case_.setOrganizationGroup(((OrganizationExt) getDsContext().get("organizationDs").getItem()).getGroup());
                } else if (getDsContext().get("personGroupDs") != null) {
                    case_.setPersonGroup((PersonGroupExt) getDsContext().get("personGroupDs").getItem());
                } else if (getDsContext().get("positionDs") != null) {
                    case_.setPositionGroup(((PositionExt) getDsContext().get("positionDs").getItem()).getGroup());
                }
                casesDs.addItem(case_);
            }
        }
        casesDs.commit();
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {

    }
}
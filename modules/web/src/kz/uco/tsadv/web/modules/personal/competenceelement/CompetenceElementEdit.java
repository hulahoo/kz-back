package kz.uco.tsadv.web.modules.personal.competenceelement;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class CompetenceElementEdit extends AbstractEditor<CompetenceElement> {


    @Inject
    private CollectionDatasource<ScaleLevel, UUID> scaleLevelsDs;

    @Inject
    private Datasource<CompetenceElement> competenceElementDs;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    @Named("fieldGroup.competenceGroup")
    private PickerField competenceGroup;
    @Inject
    private CommonService commonService;

    private Map<String,Object> params = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        this.params = params;
        competenceGroup.removeAction(PickerField.OpenAction.NAME);
    }

    @Override
    public void postInit() {
        competenceElementDs.addItemPropertyChangeListener((e) ->
        {
            if ("competenceGroup".equals(e.getProperty()))
                if (competenceElementDs.getItem().getScaleLevel() != null
                        && (competenceElementDs.getItem().getCompetenceGroup().getCompetence() == null
                        || competenceElementDs.getItem().getCompetenceGroup().getCompetence().getScale() == null
                        || !competenceElementDs.getItem().getCompetenceGroup().getCompetence().getScale().getId().equals(competenceElementDs.getItem().getScaleLevel().getScale().getId())))
                    competenceElementDs.getItem().setScaleLevel(null);
            scaleLevelsDs.refresh();
        });


        if (params!=null) {
            Map<String, Object> competenceParams = new HashMap<>();
            if (PersistenceHelper.isNew(getItem())) {
                if (params.containsKey("recruitmentFilter") || params.containsKey("alreadyExistCompetence")) {
                    if (params.containsKey("recruitmentFilter")) {
                        competenceParams.put("rcAvailableFilter", Boolean.TRUE);
                    }
                    if (params.containsKey("alreadyExistCompetence")) {
                        competenceParams.put("alreadyExistCompetence", params.get("alreadyExistCompetence"));
                    }
                    Utils.customizeLookup(fieldGroup.getField("competenceGroup").getComponent(),
                            "tsadv$CompetenceGroup.lookup",
                            WindowManager.OpenType.DIALOG,
                            competenceParams);
                }
            }else
            {
                if (params.containsKey("recruitmentFilter")) {
                    competenceParams.put("rcAvailableFilter", Boolean.TRUE);
                }
                if (params.containsKey("alreadyExistCompetence")) {
                    List<CompetenceGroup> competenceGroupList = (List<CompetenceGroup>) params.get("alreadyExistCompetence");
                    for (Iterator<CompetenceGroup> iterator = competenceGroupList.iterator(); iterator.hasNext();) {
                        CompetenceGroup competenceGroup= iterator.next();
                        if (competenceGroup.getId().equals(competenceElementDs.getItem().getCompetenceGroup().getId())) {
                            // Remove the current element from the iterator and the list.
                            iterator.remove();
                        }
                    }
                    competenceParams.put("alreadyExistCompetence", competenceGroupList);

                }

                Utils.customizeLookup(fieldGroup.getField("competenceGroup").getComponent(),
                        "tsadv$CompetenceGroup.lookup",
                        WindowManager.OpenType.DIALOG,
                        competenceParams);
            }
        }


    }
}
package kz.uco.tsadv.web.modules.personal.competencetemplate;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.performance.model.CompetenceTemplate;
import kz.uco.tsadv.modules.performance.model.CompetenceTemplateDetail;

import javax.inject.Inject;

public class CompetenceTemplateEdit extends AbstractEditor<CompetenceTemplate> {
    @Inject
    private Datasource<CompetenceTemplate> competenceTemplateDs;

    @Override
    protected void postValidate(ValidationErrors errors) {
        Double ctdWeight = 0.0;
        if (competenceTemplateDs.getItem().getCompetenceTemplateDetail() != null) {
            for (CompetenceTemplateDetail ctd : competenceTemplateDs.getItem().getCompetenceTemplateDetail()) {
                ctdWeight += ctd.getWeight();
            }
        }
        if (competenceTemplateDs.getItem().getUsePositionCompetence()) {
            if (competenceTemplateDs.getItem().getPositionCompetenceWeight() + ctdWeight != 100) {
                errors.add(getMessage("CommonWeightWarning"));
            }
        } else {
            if (ctdWeight != 100) {
                errors.add(getMessage("CommonWeightWarning"));
            }
        }
        super.postValidate(errors);
    }
}
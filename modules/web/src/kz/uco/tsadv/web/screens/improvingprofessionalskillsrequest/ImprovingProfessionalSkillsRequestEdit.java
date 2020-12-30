package kz.uco.tsadv.web.screens.improvingprofessionalskillsrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.recruitment.model.ImprovingProfessionalSkillsRequest;

@UiController("tsadv_ImprovingProfessionalSkillsRequest.edit")
@UiDescriptor("improving-professional-skills-request-edit.xml")
@EditedEntityContainer("improvingProfessionalSkillsRequestDc")
@LoadDataBeforeShow
public class ImprovingProfessionalSkillsRequestEdit extends StandardEditor<ImprovingProfessionalSkillsRequest> {
}
package kz.uco.tsadv.web.modules.learning.personLearningContract;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.PersonLearningContract;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;

import javax.inject.Inject;
import java.util.Map;

public class PersonLearningContractBrowse extends AbstractLookup {
    @Inject
    protected GroupTable<? extends PersonLearningContract> personLearningContractsTable;
    @Inject
    protected ComponentsFactory componentsFactory;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personLearningContractsTable.addGeneratedColumn("Unit", entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            AssignmentExt assignmentExt = entity.getPersonGroup().getCurrentAssignment();
            if (assignmentExt != null) {
                OrganizationGroupExt organizationGroupExt = entity.getPersonGroup().getCurrentAssignment().getOrganizationGroup();
                if (organizationGroupExt != null) {
                    label.setValue(entity.getPersonGroup().getCurrentAssignment().getOrganizationGroup().getOrganizationName());
                }
            }
            return label;
        });
        personLearningContractsTable.addGeneratedColumn("staffUnit", entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            AssignmentExt currentAssignment = entity.getPersonGroup().getCurrentAssignment();
            if (currentAssignment != null) {
                PositionGroupExt positionGroup = currentAssignment.getPositionGroup();
                if (positionGroup != null) {
                    label.setValue(positionGroup.getPositionName());
                }
            }
            return label;
        });
    }
}
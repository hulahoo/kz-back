package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import java.util.Date;

@UiController("tsadv$MyVHIInsuredPerson.browse")
@UiDescriptor("my-vhi-insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class MyVhiInsuredPersonBrowse extends StandardLookup<InsuredPerson> {

    @Inject
    private UserSession userSession;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataManager dataManager;
    @Inject
    private CommonService commonService;
    @Inject
    private TimeSource timeSource;

    public void joinVHI() {
        InsuredPerson item = dataManager.create(InsuredPerson.class);
        Date today = timeSource.currentTimestamp();
        item.setAttachDate(today);
        chekType(1, item);
        screenBuilders.editor(InsuredPerson.class, this)
                .editEntity(item)
                .build()
                .show();
    }

    public void joinFamilyMember() {
    }

    public void chekType(int type, InsuredPerson insuredPerson){
        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        insuredPerson.setType(type);

        if (type == 1){
            PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                    "from tsadv$UserExt e " +
                    "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                    .view("personGroupExt-view")
                    .list().stream().findFirst().orElse(null);

            DicCompany company = dataManager.load(DicCompany.class)
                    .query("select s.organizationGroup.company " +
                            "   from base$PersonGroupExt e" +
                            "   join e.assignments s " +
                            " where e.id = :pg")
                    .parameter("pg", personGroupExt.getId())
                    .list().stream().findFirst().orElse(null);

            PersonExt person = personGroupExt.getPerson();
            AssignmentExt assignment = personGroupExt.getCurrentAssignment();

            insuredPerson.setEmployee(personGroupExt);
            insuredPerson.setFirstName(person.getFirstName());
            insuredPerson.setSecondName(person.getLastName());
            insuredPerson.setMiddleName(person.getMiddleName());
            insuredPerson.setIin(person.getNationalIdentifier());
            insuredPerson.setAssignDate(person.getHireDate());
            insuredPerson.setBirthdate(person.getDateOfBirth());
            insuredPerson.setSex(person.getSex());
            insuredPerson.setRelative(relationshipType);
            insuredPerson.setCompany(company);
            insuredPerson.setJob(assignment.getJobGroup());

        }
    }
}
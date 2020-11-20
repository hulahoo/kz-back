package kz.uco.tsadv.beans;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.RecognitionProfileSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Locale;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
public class UserSessionManager extends com.haulmont.cuba.security.sys.UserSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionManager.class);

    @Inject
    private DataManager dataManager;

    private PersonGroupExt personGroup;

    private AssignmentExt assignment;

    private RecognitionProfileSetting recognitionProfileSetting;

    @Override
    public UserSession createSession(UUID sessionId, User user, Locale locale, boolean system) {
        UserSession userSession = super.createSession(sessionId, user, locale, system);
        loadAttributes(userSession);
        return userSession;
    }

    private void loadAttributes(UserSession userSession) {
        UserExt userExt = (UserExt) userSession.getUser();
        PersonGroupExt personGroup = getPersonGroup(userExt);

        if (personGroup != null) {
            PersonExt person = personGroup.getPerson();
            if (person != null) {
                this.personGroup = personGroup;
                userSession.setAttribute("currentLocale", userSession.getLocale().getLanguage());
                userSession.setAttribute(StaticVariable.USER_PERSON, person);
                userSession.setAttribute(StaticVariable.USER_PERSON_GROUP, personGroup);
                userSession.setAttribute(StaticVariable.USER_PERSON_GROUP_ID, personGroup.getId());

                try {
                    LoadContext<AssignmentExt> assignmentLoadContext = LoadContext.create(AssignmentExt.class);
                    assignmentLoadContext.setQuery(
                            LoadContext.createQuery(
                                    "select e from base$AssignmentExt e " +
                                            " join e.personGroup.list p " +
                                            "where e.personGroup.id = :personGroupId " +
                                            "  and e.primaryFlag = true " +
                                            " and :systemDate between e.startDate and e.endDate" +
                                            "   and :systemDate between p.startDate and p.endDate " +
                                            "     and p.type.code = 'EMPLOYEE'")
                                    .setParameter("personGroupId", personGroup.getId())
                                    .setParameter("systemDate", CommonUtils.getSystemDate()))
                            .setView("assignment.card");

                    AssignmentExt assignment = dataManager.load(assignmentLoadContext);
                    if (assignment != null) {
                        PositionGroupExt positionGroupExt = assignment.getPositionGroup();
                        userSession.setAttribute("userPositionGroupId", positionGroupExt != null ? positionGroupExt.getId() : "");
                        userSession.setAttribute("userPositionGroupIdStr", positionGroupExt != null ? positionGroupExt.getId().toString() : "");
                        userSession.setAttribute("userPositionManagerFlag", positionGroupExt != null ? positionGroupExt.getPosition().getManagerFlag() : false);

                        OrganizationGroupExt organizationGroupExt = assignment.getOrganizationGroup();
                        userSession.setAttribute("userOrganizationGroupId", organizationGroupExt != null ? organizationGroupExt.getId() : "");
                        userSession.setAttribute("userOrganizationGroupIdStr", organizationGroupExt != null ? organizationGroupExt.getId().toString() : "");

                        userSession.setAttribute(StaticVariable.ASSIGNMENT_GROUP_ID, assignment.getGroup().getId());
                        userSession.setAttribute("assignment", assignment);

                        this.assignment = assignment;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private PersonGroupExt getPersonGroup(UserExt userExt) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select user.personGroup " +
                        "from tsadv$UserExt user " +
                        "where user.id = :userId ");
        query.setParameter("userId", userExt.getId());

        loadContext.setQuery(query);
        loadContext.setView("personGroupExt.edit");
        return dataManager.load(loadContext);
    }

    public boolean hasPerson() {
        return hasPersonGroup() && personGroup.getPerson() != null;
    }

    public boolean hasPersonGroup() {
        return personGroup != null;
    }

    public boolean hasAssignment() {
        return assignment != null;
    }

    public AssignmentExt getAssignment() {
        return assignment;
    }

    public void setAssignment(AssignmentExt assignment) {
        this.assignment = assignment;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        if (hasAssignment()) return assignment.getGroup();
        return null;
    }

    public PersonExt getPerson() {
        if (hasPersonGroup()) return personGroup.getPerson();
        return null;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public void setRecognitionProfileSetting(RecognitionProfileSetting recognitionProfileSetting) {
        this.recognitionProfileSetting = recognitionProfileSetting;
    }

    public RecognitionProfileSetting getRecognitionProfileSetting() {
        return recognitionProfileSetting;
    }
}

package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.performance.dto.BoardChangedItem;
import kz.uco.tsadv.modules.performance.dto.BoardUpdateType;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EmployeeService {
    String NAME = "tsadv_EmployeeService";

    String generate(String personGroupId, String lang, String systemDate);

    FileDescriptor getImage(String personId);

    FileDescriptor getImageByPersonGroupId(String personGroupId);

    FileDescriptor getImageByEmployeeNumber(String employeeNumber);

    byte[] getMatrixImage(String id);

    String getPersonForScrum(String byType, String orgId, String positionId, String jobId, String calibrationSessionId, String lang);

    String getMatrix(String type, String calibrationSessionId, String orgId, String positionId, String jobId, String lang);

    void updatePerformanceMatrix(BoardChangedItem boardChangedItem, BoardUpdateType boardUpdateType, CalibrationSession calibrationSession);

    List<PersonPercentage> getPersonByPositionCompetence(Map<String, Object> params);

    List<PositionPercentage> getPositionByPersonCompetence(Map<String, Object> params, String language);

    long countActivePerson();

    long countExPerson();

    List<OrganizationHrUser> getHrUsers(UUID organizationGroupId, String roleCodes);

    String getTotalExperience(UUID personGroupId, Date onDate);

    Double getTotalExperienceDouble(UUID personGroupId, Date onDate);

    String getExperienceInCompany(UUID personGroupId, Date onDate);

    AssignmentExt findManagerUserByPosition(UUID positionGroupId);

    UserExt getUserByLogin(String login, @Nullable String view);

    UserExt getUserByLogin(String login);

    UserExt getSystemUser(@Nullable String view);

    UserExt getSystemUser();

    void changePersonType(PersonGroupExt personGroup, String dicPersonTypeCode);

    List<PersonGroupExt> getManagersList();

    PersonGroupExt getPersonGroupByUserId(UUID userId);

    PersonGroupExt getPersonGroupByUserIdExtendedView(UUID userId);

    UserExt getUserExtByPersonGroupId(UUID personGroupId);

    UserExt getUserExtByPersonGroupId(UUID personGroupId, String viewName);

    OrganizationGroupExt getOrganizationGroupExtByPositionGroup(PositionGroupExt positionGroupExt, String viewName);

    PositionGroupExt getPositionGroupByAssignmentGroupId(UUID assignmentGroupId, String view);

    PositionGroupExt getPositionGroupByPersonGroupId(UUID personGroupId, String view);

    UUID getPersonPositionGroup(UUID personGroupId);

    AssignmentGroupExt getAssignmentGroupByPersonGroup(PersonGroupExt personGroupExt);

    PersonGroupExt getPersonGroupByAssignmentGroupId(UUID assignmentGroupId);

    PersonGroupExt getPersonGroupByEmployeeNumber(String employeeNumber);

    Map<UserExt, PersonExt> findManagerByPositionGroup(UUID positionGroupId);

    Map<UserExt, PersonExt> findManagerByPositionGroup(UUID positionGroupId, boolean showAll);

    List<UserExt> recursiveFindManager(UUID positionGroupId);

    List<UserExt> recursiveFindManagerInActiveOne(UUID positionGroupId);

    String getExperienceOnCurrentPosition(PersonGroupExt personGroup);

    boolean employeeIsExEmployee(PersonGroupExt personGroup);

    PersonGroupExt getDirector(PersonGroupExt personGroupExt);


    UserExt findManagerByPositionGroup(UUID positionGroupId, String hierarchyId);

    UserExt findManagerByPersonGroup(UUID personGroupId, String hierarchyId);

    UUID getImmediateSupervisorByPersonGroup(UUID personGroupId);

    UUID getDirectorPositionByPersonGroup(UUID perosnGroupId);

    double getPersonExperience(UUID personGroupId, String type, Date date);

    AssignmentExt getAssignmentExt(UUID personGroupId, Date date, String view);

    Salary getSalary(AssignmentGroupExt assignmentGroupExt, Date date, String view);

    String generateOgrChart(String personGroupId);

    PersonExt getPersonByPersonGroup(UUID personGroupId, Date date, String view);

    int calculateAge(Date birthDate);

    String getYearCases(int years);

    PersonGroupExt getPersonGroupByEmployeeNumber(String employeeNumber, String view);

    boolean isLastAssignment(AssignmentExt assignmentExt);

    DicCostCenter getCostCenterByPersonGroup(@Nullable PersonGroupExt personGroup, Date date, String viewName);

    String getFirstLastName(PersonExt personExt, String language);

    Map<String, String> getDicGoodsCategories();
}
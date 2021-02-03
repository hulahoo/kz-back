package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.performance.dto.BoardChangedItem;
import kz.uco.tsadv.modules.performance.dto.BoardUpdateType;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;

import javax.annotation.Nonnull;
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

    /**
     * @see OrganizationHrUserService#getHrUsers(UUID, String, Integer)
     */
    //todo remove
    List<OrganizationHrUser> getHrUsers(UUID organizationGroupId, String roleCodes);

    String getTotalExperience(UUID personGroupId, Date onDate);

    Double getTotalExperienceDouble(UUID personGroupId, Date onDate);

    String getExperienceInCompany(UUID personGroupId, Date onDate);

    AssignmentExt findManagerUserByPosition(UUID positionGroupId);

    TsadvUser getUserByLogin(String login, @Nullable String view);

    TsadvUser getUserByLogin(String login);

    TsadvUser getSystemUser(@Nullable String view);

    TsadvUser getSystemUser();

    void changePersonType(PersonGroupExt personGroup, String dicPersonTypeCode);

    List<PersonGroupExt> getManagersList();

    PersonGroupExt getPersonGroupByUserId(UUID userId);

    PersonGroupExt getPersonGroupByUserIdExtendedView(UUID userId);

    TsadvUser getUserExtByPersonGroupId(UUID personGroupId);

    TsadvUser getUserExtByPersonGroupId(UUID personGroupId, String viewName);

    OrganizationGroupExt getOrganizationGroupExtByPositionGroup(PositionGroupExt positionGroupExt, String viewName);

    PositionGroupExt getPositionGroupByAssignmentGroupId(UUID assignmentGroupId, String view);

    PositionGroupExt getPositionGroupByPersonGroupId(UUID personGroupId, String view);

    AssignmentGroupExt getAssignmentGroupByPersonGroup(PersonGroupExt personGroupExt);

    PersonGroupExt getPersonGroupByAssignmentGroupId(UUID assignmentGroupId);

    PersonGroupExt getPersonGroupByEmployeeNumber(String employeeNumber);

    Map<TsadvUser, PersonExt> findManagerByPositionGroup(UUID positionGroupId);

    Map<TsadvUser, PersonExt> findManagerByPositionGroup(UUID positionGroupId, boolean showAll);

    List<PersonGroupExt> findManagerListByPositionGroup(UUID positionGroupId, boolean showAll);

    List<TsadvUser> recursiveFindManager(UUID positionGroupId);

    List<TsadvUser> recursiveFindManagerInActiveOne(UUID positionGroupId);

    String getExperienceOnCurrentPosition(PersonGroupExt personGroup);

    boolean employeeIsExEmployee(PersonGroupExt personGroup);

    PersonGroupExt getDirector(PersonGroupExt personGroupExt);

    TsadvUser findManagerByPositionGroup(UUID positionGroupId, String hierarchyId);

    TsadvUser findManagerByPersonGroup(UUID personGroupId, String hierarchyId);

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

    OrganizationGroupExt getOrganizationGroupByPersonGroupId(@Nonnull UUID personGroupId, String viewName);

    DicCompany getCompanyByPersonGroupId(@Nonnull UUID personGroupId);

    List<? extends PersonGroupExt> getPersonGroupByPositionGroupId(UUID positionGroupId, String viewName);
}
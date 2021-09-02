package kz.uco.tsadv.service;

import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.*;

import java.io.Serializable;

public interface IntegrationRestService {
    String NAME = "tsadv_IntegrationRestService";

    BaseResult prepareSuccess(BaseResult baseResult, String methodName, Serializable params);

    BaseResult prepareError(BaseResult baseResult, String methodName, Serializable params, String errorMessage);

    BaseResult createOrUpdateOrganization(OrganizationDataJson organizationData);

    BaseResult deleteOrganization(OrganizationDataJson organizationData);

    BaseResult createOrUpdateJob(JobDataJson jobData);

    BaseResult deleteJob(JobDataJson jobData);

    BaseResult createOrUpdatePosition(PositionDataJson positionData);

    BaseResult deletePosition(PositionDataJson positionData);

    BaseResult createOrUpdatePerson(PersonDataJson personData);

    BaseResult deletePerson(PersonDataJson personData);

    BaseResult createOrUpdateOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData);

    BaseResult deleteOrganizationHierarchyElement(HierarchyElementDataJson hierarchyElementData);

    BaseResult createOrUpdatePositionHierarchyElement(HierarchyElementDataJson hierarchyElementData);

    BaseResult deletePositionHierarchyElement(HierarchyElementDataJson hierarchyElementData);

    BaseResult createOrUpdateAssignment(AssignmentDataJson assignmentDataJson);

    BaseResult deleteAssignment(AssignmentDataJson assignmentDataJson);

    BaseResult createOrUpdateSalary(SalaryDataJson salaryData);

    BaseResult deleteSalary(SalaryDataJson salaryData);

    BaseResult createOrUpdatePersonDocument(PersonDocumentDataJson personDocumentData);

    BaseResult deletePersonDocument(PersonDocumentDataJson personDocumentData);

    BaseResult createOrUpdatePersonQualification(PersonQualificationDataJson personQualificationData);

    BaseResult deletePersonQualification(PersonQualificationDataJson personQualificationData);

    BaseResult createOrUpdatePersonContact(PersonContactDataJson personContactData);

    BaseResult deletePersonContact(PersonContactDataJson personContactData);

    BaseResult createOrUpdatePersonEducation(PersonEducationDataJson personEducationData);

    BaseResult deletePersonEducation(PersonEducationDataJson personEducationData);

    BaseResult createOrUpdateAbsence(AbsenceDataJson absenceData);

    BaseResult deleteAbsence(AbsenceDataJson absenceData);

    BaseResult createOrUpdatePersonExperience(PersonExperienceDataJson personExperienceData);

    BaseResult deletePersonExperience(PersonExperienceDataJson personExperienceData);

    BaseResult createOrUpdateBeneficiary(BeneficiaryDataJson beneficiaryData);

    BaseResult deleteBeneficiary(BeneficiaryDataJson beneficiaryData);

    BaseResult createOrUpdatePersonDismissal(PersonDismissalDataJson personDismissalData);

    BaseResult deletePersonDismissal(PersonDismissalDataJson personDismissalData);

    BaseResult createOrUpdateHarmfulCondition(HarmfulConditionDataJson harmfulConditionData);

    BaseResult deleteHarmfulCondition(HarmfulConditionDataJson harmfulConditionData);

    BaseResult createOrUpdateAssignmentSchedule(AssignmentScheduleJsonData assignmentScheduleData);

    BaseResult deleteAssignmentSchedule(AssignmentScheduleJsonData assignmentScheduleData);

    BaseResult createOrUpdatePersonAddress(PersonAddressDataJson personAddressData);

    BaseResult deletePersonAddress(PersonAddressDataJson personAddressData);

    BaseResult createOrUpdatePersonLanguages(PersonLanguageDataJson personLanguageData);

    BaseResult deletePersonLanguages(PersonLanguageDataJson personLanguageData);

    BaseResult createOrUpdateUser(UserDataJson userData);

    BaseResult createOrUpdatePersonAbsenceBalance(AbsenceBalanceDataJson absenceBalanceData);

    BaseResult createOrUpdatePositionIncentiveFlag(PositionIncentiveFlagDataJson positionIncentiveFlagData);

    BaseResult deletePositionIncentiveFlag(PositionIncentiveFlagDataJson positionIncentiveFlagData);

    BaseResult createOrUpdatePersonPaySlip(PersonPayslipDataJson personPayslipDataJson);

    BaseResult deletePersonPaySlip(PersonPayslipDataJson personPayslipDataJson);
}
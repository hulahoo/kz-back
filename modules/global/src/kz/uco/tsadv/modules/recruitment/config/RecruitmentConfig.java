package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.cuba.core.config.*;
import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultInt;
import com.haulmont.cuba.core.config.defaults.DefaultString;
import com.haulmont.cuba.core.config.type.Stringify;

/**
 * @author Adilbekov Yernar
 */
@Source(type = SourceType.DATABASE)
public interface RecruitmentConfig extends Config {

    @Property("tal.recruitment.current.default.manager")
    @DefaultBoolean(true)
    boolean getCurrentDefaultManager();

    @Property("tal.recruitment.autogenerateRequisitionCode")
    @DefaultBoolean(true)
    boolean getAutogenerateRequisitionCode();

    @Property("tal.recruitment.autofill.requisition.start.date")
    @DefaultBoolean(true)
    boolean getAutofillRequisitionStartDate();

    @Property("tal.recruitment.autofill.requisition.final.date")
    @DefaultBoolean(true)
    boolean getAutofillRequisitionFinalDate();

    @Property("tal.recruitment.check.requisition.final.date")
    @DefaultBoolean(true)
    boolean getCheckRequisitionFinalDate();

    @Property("tal.recruitment.sendEmailWaitTime")
    @DefaultInt(120)
    int getSendEmailWaitTime();

    @Property("tal.recruitment.requisition.sorting.CVDate")
    @DefaultBoolean(true)
    boolean getSortingCVDate();

    @Property("tal.recruitment.requisition.positionDescription")
    @DefaultBoolean(true)
    boolean getPositionDescription();

    @Property("tal.recruitment.interview.defaultAddressCode")
    @Default("CENTER1")
    String getDefaultAddress();

    @Property("tal.recruitment.interview.defaultSourceForFastCreateCode")
    @Default("RECRUITER_SEARCH")
    String getDefaultSourceForFastCreateCode();

    @Property("tal.recruitment.requisition.organization.generationType")
    @EnumStore(EnumStoreMode.ID)
    @Default("FULL")
//    @Factory(factory = OrganizationGenerationTypeFactory.class)
    @Stringify(stringify = OrganizationGenerationTypeStringify.class)
    OrganizationGenerationType getOrganizationGenerationType();

    @EnumStore(EnumStoreMode.ID)
    void setOrganizationGenerationType(OrganizationGenerationType generationType);

    @Property("tal.recruitment.requisition.editWithoutChangeStatus.recruiterManager")
    @DefaultBoolean(true)
    boolean getEditWithoutChangeStatusRm();

    @Property("tal.recruitment.requisition.editWithoutChangeStatus.recruiter")
    @DefaultBoolean(true)
    boolean getEditWithoutChangeStatusR();

    @Property("tal.recruitment.requisition.editWithoutChangeStatus.manager")
    @DefaultBoolean(true)
    boolean getEditWithoutChangeStatusM();

    @Property("tal.recruitment.requisition.jobRequest.excludeDraftStatusFromFilter")
    @DefaultBoolean(true)
    boolean getExcludeDraftStatusFromFilter();

    @Property("tal.recruitment.requisition.jobRequest.filterByCreatedByForRest")
    @DefaultBoolean(true)
    boolean getFilterByCreatedByForRest();

    @Property("tal.recruitment.requisition.candidateForm")
    String getCandidateForm();

    @Property("tal.recruitment.requisition.enableCustomFilter")
    @DefaultBoolean(true)
    boolean getEnabledCustomFilter();

    @Property("tal.recruitment.requisition.enableCubaFilter")
    @DefaultBoolean(false)
    boolean getEnabledCubaFilter();

    @Property("tal.recruitment.requisition.jobRequest.default.draft")
    @DefaultBoolean(false)
    boolean getJobReuestDefaultDraft();

    @Property("tal.recruitment.requisition.jobRequest.disableWhenDraft")
    @DefaultBoolean(false)
    boolean getdDisableWhenDraft();

    @Property("tal.recruitment.requisition.jobRequest.screenForSearchCandidate")
    String getScreenForSearchCandidate();

    @Property("tal.recruitment.requisition.jobRequest.enableCandidateRequirements")
    @DefaultBoolean(false)
    boolean getEnableCandidateRequirements();

    @Property("tal.recruitment.requisition.jobRequest.filterCityEnableForJobRequest")
    @DefaultBoolean(true)
    boolean getFilterCityEnableForJobRequest();

    @Property("tal.recruitment.requisition.jobRequest.default.reason.code")
    @DefaultString("BY_CANDIDATE_INITIATIVE")
    String getDefaultReasonCode();

    @Property("tal.recruitment.requisition.jobRequest.default.reason.lang.value1")
    @DefaultString("По инициативе кандидата")
    String getDefaultReasonLangValue1();

    @Property("tal.recruitment.requisition.jobRequest.default.reason.lang.value2")
    @DefaultString("By candidate initiative")
    String getDefaultReasonLangValue2();

    @Property("tal.recruitment.requisition.jobRequest.default.reason.lang.value3")
    @DefaultString("По инициативе кандидата")
    String getDefaultReasonLangValue3();

    @Property("tal.recruitment.requisition.jobRequest.editEnabled")
    @DefaultBoolean(false)
    boolean getEditEnabled();

    @Property("tal.recruitment.requisition.useNewSelfRequisitionBrowse")
    @DefaultBoolean(true)
    boolean getUseNewSelfRequisitionBrowse();


}

package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface IntegrationConfig extends Config {

    @Property("tsadv.integration.off")
    @DefaultBoolean(false)
    boolean getIsIntegrationOff();

    @Property("tsadv.integration.absenceForRecall.off")
    @DefaultBoolean(false)
    boolean getAbsenceForRecallOff();

    @Property("tsadv.integration.absenceForRecall.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/absence/recall/request")
    String getAbsenceForRecallUrl();

    @Property("tsadv.integration.scheduleOffsetsRequest.off")
    @DefaultBoolean(false)
    boolean getScheduleOffsetsRequestOff();

    @Property("tsadv.integration.scheduleOffsetsRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/schedule/change/request")
    String getScheduleOffsetsRequestUrl();

    @Property("tsadv.integration.changeAbsenceDaysRequest.off")
    @DefaultBoolean(false)
    boolean getChangeAbsenceDaysRequestOff();

    @Property("tsadv.integration.changeAbsenceDaysRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/absence/change/request")
    String getChangeAbsenceDaysRequestUrl();

    @Property("tsadv.integration.absenceRvdRequest.off")
    @DefaultBoolean(false)
    boolean getAbsenceRvdRequestOff();

    @Property("tsadv.integration.absenceRvdRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
    String getAbsenceRvdRequestUrl();

    @Property("tsadv.integration.addressRequest.off")
    @DefaultBoolean(false)
    boolean getAddressRequestOff();

    @Property("tsadv.integration.addressRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/address/request")
    String getAddressRequestUrl();

    @Property("tsadv.integration.leavingVacationRequest.off")
    @DefaultBoolean(false)
    boolean getLeavingVacationRequestOff();

    @Property("tsadv.integration.leavingVacationRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/maternity/recall/request")
    String getLeavingVacationRequestUrl();

    @Property("tsadv.integration.personalDataRequest.off")
    @DefaultBoolean(false)
    boolean getPersonalDataRequestOff();

    @Property("tsadv.integration.personalDataRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/personinfo/request")
    String getPersonalDataRequestUrl();

    @Property("tsadv.integration.absenceRequest.off")
    @DefaultBoolean(false)
    boolean getAbsenceRequestOff();

    @Property("tsadv.integration.absenceRequest.url")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/absence/request")
    String getAbsenceRequestRestUrl();

    @Property("tsadv.integration.activeDirectory.on")
    @DefaultBoolean(true)
    boolean getIsIntegrationActiveDirectory();


    @Property("tsadv.integration.oracle.absenceRequestUrl")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/absence/request")
    String getAbsenceRequestUrl();


    @Property("tsadv.integration.oracle.basicAuth.login")
    @DefaultString("ahruco")
    String getBasicAuthLogin();


    @Property("tsadv.integration.oracle.basicAuth.password")
    @DefaultString("ahruco")
    String getBasicAuthPassword();


}

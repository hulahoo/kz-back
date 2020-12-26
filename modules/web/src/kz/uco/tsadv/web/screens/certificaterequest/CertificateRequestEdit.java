package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.AbsenceRequestStatus;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 11:37
 */

@UiController("tsadv_CertificateRequest.edit")
@UiDescriptor("certificate-request-edit.xml")
@EditedEntityContainer("certificateRequestDc")
@LoadDataBeforeShow
public class CertificateRequestEdit extends StandardEditor<CertificateRequest> {
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UserSession userSession;


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        CertificateRequest item = getEditedEntity();
        if (item.getStatus() == null || "DRAFT".equals(item.getStatus().getCode())) {
            item.setRequestDate(timeSource.currentTimestamp());
        }

        if (item.getRequestNumber() == null) {
            item.setRequestNumber(employeeNumberService.generateNextRequestNumber());
            item.setPersonGroup(employeeService.getPersonGroupByUserId(userSession.getUser().getId()));
            item.setStatus(commonService.getEntity(AbsenceRequestStatus.class, "DRAFT"));
            item.setShowSalary(false);
            item.setNumberOfCopy(1);
        }

    }




}
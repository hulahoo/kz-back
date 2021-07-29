package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class AbsenceJson implements Serializable {
    private String personId;
    private String legacyId;
    private String absenceTypeId;
    private String startDate;
    private String endDate;
    private String absenceDuration;
    private String orderNumber;
    private String orderDate;
    private String absenceHours;
    private String companyCode;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getAbsenceTypeId() {
        return absenceTypeId;
    }

    public void setAbsenceTypeId(String absenceTypeId) {
        this.absenceTypeId = absenceTypeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAbsenceDuration() {
        return absenceDuration;
    }

    public void setAbsenceDuration(String absenceDuration) {
        this.absenceDuration = absenceDuration;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getAbsenceHours() {
        return absenceHours;
    }

    public void setAbsenceHours(String absenceHours) {
        this.absenceHours = absenceHours;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}

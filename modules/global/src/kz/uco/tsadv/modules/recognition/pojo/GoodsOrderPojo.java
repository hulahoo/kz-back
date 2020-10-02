package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$GoodsOrderPojo")
public class GoodsOrderPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -3831704958473327439L;

    @MetaProperty
    protected String orderNumber;

    @MetaProperty
    protected String dateTime;

    @MetaProperty
    protected Long sum;

    @MetaProperty
    protected String status;

    @MetaProperty
    protected String statusCode;

    @MetaProperty
    protected String fullName;

    @MetaProperty
    protected String voucherUsed;

    @MetaProperty
    protected String voucherQRCode;

    @MetaProperty
    protected String personGroupId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Long getSum() {
        return sum;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getVoucherUsed() {
        return voucherUsed;
    }

    public void setVoucherUsed(String voucherUsed) {
        this.voucherUsed = voucherUsed;
    }

    public String getVoucherQRCode() {
        return voucherQRCode;
    }

    public void setVoucherQRCode(String voucherQRCode) {
        this.voucherQRCode = voucherQRCode;
    }

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }
}
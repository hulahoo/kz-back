package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|addressLang1")
@Table(name = "TSADV_RECOGNITION_PROVIDER")
@Entity(name = "tsadv$RecognitionProvider")
public class RecognitionProvider extends StandardEntity {
    private static final long serialVersionUID = -8462826060911019548L;

    @NotNull
    @Column(name = "PROVIDER_NAME_LANG1", nullable = false, length = 350)
    protected String providerNameLang1;

    @Column(name = "PROVIDER_NAME_LANG2", length = 350)
    protected String providerNameLang2;

    @Column(name = "PROVIDER_NAME_LANG3", length = 360)
    protected String providerNameLang3;

    @Column(name = "CONTACT_INFO", length = 350)
    protected String contactInfo;

    @Column(name = "ADDRESS_LANG1", length = 350)
    protected String addressLang1;

    @Column(name = "ADDRESS_LANG2", length = 350)
    protected String addressLang2;

    @Column(name = "ADDRESS_LANG3", length = 350)
    protected String addressLang3;

    public void setProviderNameLang1(String providerNameLang1) {
        this.providerNameLang1 = providerNameLang1;
    }

    public String getProviderNameLang1() {
        return providerNameLang1;
    }

    public void setProviderNameLang2(String providerNameLang2) {
        this.providerNameLang2 = providerNameLang2;
    }

    public String getProviderNameLang2() {
        return providerNameLang2;
    }

    public void setProviderNameLang3(String providerNameLang3) {
        this.providerNameLang3 = providerNameLang3;
    }

    public String getProviderNameLang3() {
        return providerNameLang3;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setAddressLang1(String addressLang1) {
        this.addressLang1 = addressLang1;
    }

    public String getAddressLang1() {
        return addressLang1;
    }

    public void setAddressLang2(String addressLang2) {
        this.addressLang2 = addressLang2;
    }

    public String getAddressLang2() {
        return addressLang2;
    }

    public void setAddressLang3(String addressLang3) {
        this.addressLang3 = addressLang3;
    }

    public String getAddressLang3() {
        return addressLang3;
    }


}
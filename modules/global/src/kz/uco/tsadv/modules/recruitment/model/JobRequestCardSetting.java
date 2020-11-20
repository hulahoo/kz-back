package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.UserExt;

import javax.persistence.*;

@Table(name = "TSADV_JOB_REQUEST_CARD_SETTING")
@Entity(name = "tsadv$JobRequestCardSetting")
public class JobRequestCardSetting extends StandardEntity {
    private static final long serialVersionUID = 6188565139350318231L;

    @Column(name = "PROPERTY")
    protected String property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected UserExt user;

    @Column(name = "PROPERTY_VALUE")
    protected Boolean propertyValue;

    public void setUser(UserExt user) {
        this.user = user;
    }

    public UserExt getUser() {
        return user;
    }


    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setPropertyValue(Boolean propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Boolean getPropertyValue() {
        return propertyValue;
    }


}
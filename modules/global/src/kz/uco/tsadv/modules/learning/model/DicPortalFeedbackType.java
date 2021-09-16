package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.common.MultiLanguageUtils;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "TSADV_DIC_PORTAL_FEEDBACK_TYPE")
@Entity(name = "tsadv_DicPortalFeedbackType")
@NamePattern("%s|langValue,code")
public class DicPortalFeedbackType extends AbstractDictionary {
    private static final long serialVersionUID = 1355148406494897425L;

    @Column(name = "SYSTEM_NOTIFICATION_TEXT1", length = 3000)
    private String systemNotificationText1;

    @Column(name = "SYSTEM_NOTIFICATION_TEXT2", length = 3000)
    private String systemNotificationText2;

    @Column(name = "SYSTEM_NOTIFICATION_TEXT3", length = 3000)
    private String systemNotificationText3;

    @Transient
    @MetaProperty(related = {"systemNotificationText1", "systemNotificationText2", "systemNotificationText3"})
    private String systemNotificationText;

    public String getSystemNotificationText() {
        return MultiLanguageUtils.getCurrentLanguageValue(systemNotificationText1, systemNotificationText2, systemNotificationText3);
    }

    public String getSystemNotificationText3() {
        return systemNotificationText3;
    }

    public void setSystemNotificationText3(String systemNotificationText3) {
        this.systemNotificationText3 = systemNotificationText3;
    }

    public String getSystemNotificationText2() {
        return systemNotificationText2;
    }

    public void setSystemNotificationText2(String systemNotificationText2) {
        this.systemNotificationText2 = systemNotificationText2;
    }

    public String getSystemNotificationText1() {
        return systemNotificationText1;
    }

    public void setSystemNotificationText1(String systemNotificationText1) {
        this.systemNotificationText1 = systemNotificationText1;
    }
}
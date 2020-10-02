package kz.uco.tsadv.modules.timesheet.dictionary;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_DIC_SCHEDULE_ELEMENT_TYPE")
@Entity(name = "tsadv$DicScheduleElementType")
public class DicScheduleElementType extends AbstractDictionary {
    private static final long serialVersionUID = 1073407874121853326L;

    @Column(name = "SHORT_NAME1")
    protected String shortName1;

    @Column(name = "SHORT_NAME2")
    protected String shortName2;

    @Column(name = "SHORT_NAME3")
    protected String shortName3;

    @Column(name = "SHORT_NAME4")
    protected String shortName4;

    @Column(name = "SHORT_NAME5")
    protected String shortName5;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_FROM")
    protected Date timeFrom;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_TO")
    protected Date timeTo;

    @Column(name = "DISPLAY_ON_TIMECARD_EDIT_SCREEN")
    protected Boolean displayOnTimecardEditScreen;

    public void setDisplayOnTimecardEditScreen(Boolean displayOnTimecardEditScreen) {
        this.displayOnTimecardEditScreen = displayOnTimecardEditScreen;
    }

    public Boolean getDisplayOnTimecardEditScreen() {
        return displayOnTimecardEditScreen;
    }


    public void setShortName1(String shortName1) {
        this.shortName1 = shortName1;
    }

    public String getShortName1() {
        return shortName1;
    }

    public void setShortName2(String shortName2) {
        this.shortName2 = shortName2;
    }

    public String getShortName2() {
        return shortName2;
    }

    public void setShortName3(String shortName3) {
        this.shortName3 = shortName3;
    }

    public String getShortName3() {
        return shortName3;
    }

    public void setShortName4(String shortName4) {
        this.shortName4 = shortName4;
    }

    public String getShortName4() {
        return shortName4;
    }

    public void setShortName5(String shortName5) {
        this.shortName5 = shortName5;
    }

    public String getShortName5() {
        return shortName5;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    @MetaProperty(related = {"shortName1", "shortName2", "shortName3", "shortName4", "shortName5"})
    public String getShortName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("tal.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return shortName1;
                }
                case 1: {
                    return shortName2;
                }
                case 2: {
                    return shortName3;
                }
                case 3: {
                    return shortName4;
                }
                case 4: {
                    return shortName5;
                }
                default:
                    return shortName1;
            }
        }

        return shortName1;
    }

}
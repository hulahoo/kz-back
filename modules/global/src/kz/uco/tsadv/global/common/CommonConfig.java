package kz.uco.tsadv.global.common;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * Common business application settings
 *
 * @author Felix Kamalov
 */
@Source(type = SourceType.DATABASE)
public interface CommonConfig extends Config {

    @Property("common.nationalIdentifierCheck.enabled")
    @DefaultBoolean(true)
    boolean getNationalIdentifierCheckEnabled();

    void setNationalIdentifierCheckEnabled(boolean isEnabled);


    @Property("common.nationalIdentifierDuplicateCheck.enabled")
    @DefaultBoolean(true)
    boolean getNationalIdentifierDuplicateCheckEnabled();

    void setNationalIdentifierDuplicateCheckEnabled(boolean isEnabled);

    @Property("common.hr.fullFunctionalityEnabled")
    @DefaultBoolean(false)
    boolean getHrFullFunctionalityEnabled();

    void setHrFullFunctionalityEnabled(boolean isEnabled);

    @Property("common.security.disabledSecurityGroupId")
    String getDisabledSecurityGroupId();

    void setDisabledSecurityGroupId(String groupId);

    @Property("common.mainApp")
    @Default("")
    String getMainApp();

    void setmainApp(String mainAppName);

    @Property("common.videoconverter.converterPath")
    @Default("")
    String getConverterPath();

    void setConverterPath(String converterPath);


    @Property("common.videoconverter.tempDirPath")
    @Default("")
    String getTempDirPath();

    void setTempDirPath(String tempDirPath);


    @Property("common.scorm.scormPackageDirPath")
    @Default("")
    String getScormPackageDirPath();

    void setScormPackageDirPath(String dirPath);


    @Property("common.scorm.scormPackageDomainURL")
    @Default("")
    String getScormPackageDomainURL();

    void setScormPackageDomainURL(String domainURL);

    @Property("common.security.allowedUsersWithoutRoles")
    @Default("")
    String getAllowedUsersWithoutRoles();
    void setAllowedUsersWithoutRoles(String userList);

}

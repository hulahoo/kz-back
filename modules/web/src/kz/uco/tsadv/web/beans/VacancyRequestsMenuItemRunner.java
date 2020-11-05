package kz.uco.tsadv.web.beans;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.tsadv.components.EmployeeMenuItemRunner;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import org.dom4j.Element;

public class VacancyRequestsMenuItemRunner extends EmployeeMenuItemRunner {

    @Override
    protected String getScreenId(Element currentElement) {
        RecruitmentConfig recruitmentConfig = AppBeans.get(Configuration.class)
                .getConfig(RecruitmentConfig.class);
        if (recruitmentConfig.getUseNewSelfRequisitionBrowse()) {
            return "tsadv$Requisition.self.new.browse";
        } else {
            return "requisition-browse-self";
        }
    }

    @Override
    protected OpenMode getScreenOpenType(Element currentElement) {
        return OpenMode.NEW_TAB;
    }

}

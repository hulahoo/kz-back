package kz.uco.tsadv.web.beans;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.config.MenuItem;
import com.haulmont.cuba.gui.screen.FrameOwner;
import kz.uco.tsadv.components.EmployeeMenuItemRunner;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import org.dom4j.Element;

public class VacancyRequestsMenuItemRunner extends EmployeeMenuItemRunner {

    @Override
    public void run(FrameOwner origin, MenuItem menuItem) {
        Element menuItemDescriptor = menuItem.getDescriptor();
        menuItemDescriptor.addElement("param");
        Element newElement = menuItemDescriptor.element("param");
        menuItemDescriptor.element("param").addAttribute("name", "screen");
        newElement.addAttribute("value", getWindowId());
        super.run(origin, menuItem);
    }

    private String getWindowId() {
        RecruitmentConfig recruitmentConfig = AppBeans.get(Configuration.class)
                .getConfig(RecruitmentConfig.class);
        if (recruitmentConfig.getUseNewSelfRequisitionBrowse()) {
            return "tsadv$Requisition.self.new.browse";
        } else {
            return "requisition-browse-self";
        }
    }

}

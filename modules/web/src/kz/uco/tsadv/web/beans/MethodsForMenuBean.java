package kz.uco.tsadv.web.beans;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.web.App;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_MethodsForMenuBean")
public class MethodsForMenuBean {
    @Inject
    protected RecruitmentConfig recruitmentConfig;

    public void openRequisition() {
        Frame window = App.getInstance().getTopLevelWindow();
        if (recruitmentConfig.getUseNewSelfRequisitionBrowse()) {
            window.openWindow("tsadv$Requisition.self.new.browse", WindowManager.OpenType.NEW_TAB);
        } else {
            window.openWindow("requisition-browse-self", WindowManager.OpenType.NEW_TAB);
        }

    }
}

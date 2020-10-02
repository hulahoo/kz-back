package kz.uco.tsadv.web.accideninjured;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.AccidenInjured;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class AccidenInjuredBrowse extends AbstractLookup {
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;

    public Component generateLinkToReport(Entity entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setAction(new BaseAction("report") {
            @Override
            public void actionPerform(Component component) {
                Report report = commonService.emQuerySingleRelult(Report.class, "select e from report$Report e where e.code = 'Form_A'", null);
                ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
                Map<String, Object> map = new HashMap<>();
                map.put("AccidentInjured", entity);
                reportGuiManager.printReport(report, map, null, report.getLocName());
                showNotification("test");
            }
        });
        return linkButton;
    }
}
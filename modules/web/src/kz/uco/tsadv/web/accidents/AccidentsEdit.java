package kz.uco.tsadv.web.accidents;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.Accidents;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class AccidentsEdit extends AbstractEditor<Accidents> {

    @Inject
    private Frame windowActions;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private FieldGroup fieldGroup1;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("readOnly") != null) {
            readOnlyMode();
        }
    }
    private void readOnlyMode() {
        fieldGroup.setEditable(false);
        fieldGroup1.setEditable(false);
    }

    @Inject
    protected Messages messages;

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
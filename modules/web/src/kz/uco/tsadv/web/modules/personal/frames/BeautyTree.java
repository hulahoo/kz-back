package kz.uco.tsadv.web.modules.personal.frames;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.web.gui.components.WebChart;
import kz.uco.tsadv.web.toolkit.ui.chart.ChartServerComponent;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 */

public class BeautyTree extends AbstractWindow {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Inject
    private ScrollBoxLayout scrollBox;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private DataManager dataManager;

    private String personGroupId = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Object personGroupIdObject = params.get("personGroupId");

        if (personGroupIdObject == null) {
            personGroupIdObject = userSessionSource.getUserSession().getAttribute("userPersonGroupId");
        }

        if (personGroupIdObject != null) {
            this.personGroupId = String.valueOf(personGroupIdObject);
        }

        renderChart();
    }

    private void renderChart() {
        scrollBox.removeAll();
        if (personGroupId != null) {
            WebChart webChart = componentsFactory.createComponent(WebChart.class);
            ChartServerComponent chart = (ChartServerComponent) webChart.getComponent();
            chart.setPersonGroupId(personGroupId);
            chart.setLang(userSessionSource.getLocale().getLanguage());
            chart.setAuthorizationToken(CommonUtils.getAuthorizationToken()); //+ for rest api security
            chart.setUrl("./rest/v2/services/tsadv_EmployeeService/generate");

            chart.setSystemDate(dateFormat.format(CommonUtils.getSystemDate()));

            chart.addValueChangeListener((ChartServerComponent.ValueChangeListener) () -> redirect(chart.getUrl(), chart.getChartId()));

            scrollBox.add(webChart);
        }
    }

    public void redirect(String url, String id) {
        if (url.equalsIgnoreCase("team-member")) {
            AssignmentExt assignment = getAssignment(UUID.fromString(id));
            if (assignment != null) {
                AbstractEditor abstractEditor = openEditor("base$TeamMember", assignment.getPersonGroup().getPerson(), WindowManager.OpenType.THIS_TAB);
                abstractEditor.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        renderChart();
                    }
                });
            }
        }
    }

    private AssignmentExt getAssignment(UUID personId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroupId.id = (select p.group.id from base$PersonExt p where p.id = :pId)")
                .setParameter("pId", personId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }
}
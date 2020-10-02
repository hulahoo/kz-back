package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;

import javax.inject.Inject;
import java.util.Map;

public class PcfCalendarOffset extends EditableFrame {
    private Datasource<OrgAnalytics> orgAnalyticsDs;
    @Inject
    private Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (orgAnalyticsDs.getItem() == null) {
            OrgAnalytics analytics = metadata.create(OrgAnalytics.class);
            orgAnalyticsDs.setItem(analytics);
        }
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {
        orgAnalyticsDs = getDsContext().get("orgAnalyticsDs");

    }

    public void commitOrgAnalytics() {
        if (getDsContext().get("assignmentDs") != null) {
            getDsContext().getParent().commit();
        }
    }

    public void cansel() {
        if (getDsContext().get("assignmentDs") != null) {
            getDsContext().get("assignmentDs").refresh();
        }
    }
}
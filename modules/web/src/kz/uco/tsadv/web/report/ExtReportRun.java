package kz.uco.tsadv.web.report;

import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportGroup;
import com.haulmont.reports.gui.report.run.ReportRun;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExtReportRun extends ReportRun {

    @Override
    public void filterReports() {
        List<Report> reports = new ArrayList<>(
                reportGuiManager.getAvailableReports(screenParameter, userSessionSource.getUserSession().getUser(), null)
        );

        CollectionUtils.filter(reports, this::filterData);

        reportDs.clear();
        for (Report report : reports) {
            reportDs.includeItem(report);
        }
    }

    protected boolean filterData(Object object) {
        String nameFilterValue = StringUtils.lowerCase(nameFilter.getValue());
        String codeFilterValue = StringUtils.lowerCase(codeFilter.getValue());
        ReportGroup groupFilterValue = groupFilter.getValue();
        Date dateFilterValue = updatedDateFilter.getValue();

        Report report = (Report) object;

        if (nameFilterValue != null
                && !report.getName().toLowerCase().contains(nameFilterValue)
                && (
                report.getLocName() == null
                        || !report.getLocName().toLowerCase().contains(nameFilterValue))) {
            return false;
        }

        if (codeFilterValue != null) {
            if (report.getCode() == null
                    || (report.getCode() != null
                    && !report.getCode().toLowerCase().contains(codeFilterValue))) {
                return false;
            }
        }

        if (groupFilterValue != null && !Objects.equals(report.getGroup(), groupFilterValue)) {
            return false;
        }

        if (dateFilterValue != null
                && report.getUpdateTs() != null
                && !report.getUpdateTs().after(dateFilterValue)) {
            return false;
        }

        return true;
    }
}
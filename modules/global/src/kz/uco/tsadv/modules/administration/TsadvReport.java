package kz.uco.tsadv.modules.administration;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.reports.entity.Report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tsadv_TsadvReport")
public class TsadvReport extends Report {
    private static final long serialVersionUID = 2576502900033546585L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCREENSHOT_ID")
    protected FileDescriptor screenshot;

    public FileDescriptor getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(FileDescriptor screenshot) {
        this.screenshot = screenshot;
    }
}
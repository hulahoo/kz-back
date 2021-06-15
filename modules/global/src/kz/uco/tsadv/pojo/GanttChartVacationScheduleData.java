package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
public class GanttChartVacationScheduleData implements Serializable {

    private UUID personGroupId;
    private String personFullName;
    private Date startDate;
    private Date endDate;
    private String absenceType;
    private int colorIndex;
    private double brighten;

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public double getBrighten() {
        return brighten;
    }

    public void setBrighten(double brighten) {
        this.brighten = brighten;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
    }

    public UUID getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(UUID personGroupId) {
        this.personGroupId = personGroupId;
    }

    public String getPersonFullName() {
        return personFullName;
    }

    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

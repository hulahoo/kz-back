package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class CalendarMonthPojo implements Serializable {
    String name;
    TimePojo duration;
    String courseId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimePojo getDuration() {
        return duration;
    }

    public void setDuration(TimePojo duration) {
        this.duration = duration;
    }

    public String getCourseId() { return courseId; }

    public void setCourseId(String courseId) { this.courseId = courseId; }
}

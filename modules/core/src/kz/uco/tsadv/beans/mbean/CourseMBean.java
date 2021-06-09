package kz.uco.tsadv.beans.mbean;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "JMX bean for course settings")
public interface CourseMBean {
    String updateCoursesLogo();
}
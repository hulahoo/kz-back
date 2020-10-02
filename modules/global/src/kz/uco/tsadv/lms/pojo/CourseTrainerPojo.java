package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class CourseTrainerPojo implements Serializable {
    String fullName;

    String professionalData;

    String additionalData;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfessionalData() {
        return professionalData;
    }

    public void setProfessionalData(String professionalData) {
        this.professionalData = professionalData;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}

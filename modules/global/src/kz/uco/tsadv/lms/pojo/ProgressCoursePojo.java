package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProgressCoursePojo implements Serializable {
    public static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    protected Integer lineNum;
    protected String date;
    protected String courseName;
    protected String fio;
    protected Integer score;
    protected Integer maxScore;
    protected Integer countFailed;
    protected String certificateUrl;

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getCountFailed() {
        return countFailed;
    }

    public void setCountFailed(Integer countFailed) {
        this.countFailed = countFailed;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }
}

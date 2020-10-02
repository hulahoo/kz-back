package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.UUID;

/**
 * Отклик на Вакансию (Интеграционный объект)
 */
@NamePattern("%s|id")
@MetaClass(name = "tsadv$JobRequestInt")
public class JobRequestInt extends AbstractEntityInt {
    private static final long serialVersionUID = -6315866841720493813L;

    /**
     * Дата отклика
     */
    @MetaProperty
    protected String requestDate;

    /**
     * Статус отклика
     */
    @MetaProperty
    protected String requestStatus;

    /**
     * Id Вакансии
     */
    @MetaProperty
    protected UUID requisition;

    /**
     * Код Вакансии
     */
    @MetaProperty
    protected String requisitionCode;

    /**
     * Должность
     */
    @MetaProperty
    protected String requisitionJob;

    /**
     * Id видеозаписи о себе
     */
    @MetaProperty
    protected UUID videoFile;

    /**
     * Id источника отклика
     */
    @MetaProperty
    protected UUID source;

    /**
     * Прочий источник (отсутствующий в справочнике источников)
     */
    @MetaProperty
    protected String otherSource;



    public void setVideoFile(UUID videoFile) {
        this.videoFile = videoFile;
    }

    public UUID getVideoFile() {
        return videoFile;
    }


    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequisition(UUID requisition) {
        this.requisition = requisition;
    }

    public UUID getRequisition() {
        return requisition;
    }

    public void setRequisitionCode(String requisitionCode) {
        this.requisitionCode = requisitionCode;
    }

    public String getRequisitionCode() {
        return requisitionCode;
    }

    public void setRequisitionJob(String requisitionJob) {
        this.requisitionJob = requisitionJob;
    }

    public String getRequisitionJob() {
        return requisitionJob;
    }

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public String getOtherSource() {
        return otherSource;
    }

    public void setOtherSource(String otherSource) {
        this.otherSource = otherSource;
    }
}
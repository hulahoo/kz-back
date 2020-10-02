package kz.uco.tsadv.lms.pojo;

import com.haulmont.cuba.core.entity.SendingMessage;

import java.io.Serializable;
import java.util.Date;

public class NotificationPojo implements Serializable {
    String id;
    Date date;
    boolean readed;
    String contentText;
    String caption;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

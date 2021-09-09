package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.UUID;

public class AnswerPojo implements Serializable {
    protected UUID id;
    protected String text;
    protected UUID imageId;

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

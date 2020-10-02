package kz.uco.tsadv.global.entity;

import kz.uco.tsadv.modules.recognition.pojo.RecognitionCommentPojo;

import java.io.Serializable;

/**
 * @author adilbekov.yernar
 */
public class CommentRestResult implements Serializable {

    private boolean success;

    private String message;

    private RecognitionCommentPojo commentPojo;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RecognitionCommentPojo getCommentPojo() {
        return commentPojo;
    }

    public void setCommentPojo(RecognitionCommentPojo commentPojo) {
        this.commentPojo = commentPojo;
    }
}

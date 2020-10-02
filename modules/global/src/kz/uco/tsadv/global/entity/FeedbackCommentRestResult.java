package kz.uco.tsadv.global.entity;

import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackCommentPojo;

import java.io.Serializable;

/**
 * @author adilbekov.yernar
 */
public class FeedbackCommentRestResult implements Serializable {

    private boolean success;

    private String message;

    private RcgFeedbackCommentPojo commentPojo;

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

    public RcgFeedbackCommentPojo getCommentPojo() {
        return commentPojo;
    }

    public void setCommentPojo(RcgFeedbackCommentPojo commentPojo) {
        this.commentPojo = commentPojo;
    }
}

package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackAttachmentPojo;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackCommentPojo;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackPojo;

import java.util.List;
import java.util.UUID;

public interface RcgFeedbackService {
    String NAME = "tsadv_RcgFeedbackService";

    String loadFeedback(int page, long lastCount, int type, UUID personGroupId, String filter);

    Long feedbackCount(int type, UUID personGroupId, String filter);

    Long feedbackCommentsCount(String feedbackId);

    String loadFeedbackComments(String feedbackId, int page);

    List<RcgFeedbackCommentPojo> loadFeedbackComments(String feedbackId, int offset, int maxResults, int languageIndex, boolean isAutomaticTranslate);

    List<RcgFeedbackAttachmentPojo> loadFeedbackAttachments(UUID feedbackId);

    List<String> validateRcgFeedback(RcgFeedback rcgFeedback);

    FileDescriptor feedbackTypeImage(UUID id);

    String sendComment(RcgFeedbackCommentPojo commentPojo);

    String loadChartData(String personGroupId, String direction);

    String loadChartCategories();

    RcgFeedback findFeedback(String feedbackId);

    RcgFeedbackPojo parseFeedbackToPojo(RcgFeedback rcgFeedback);

    void sendNotification(RcgFeedback rcgFeedback);
}
package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.tsadv.modules.recognition.exceptions.RcgFeedbackException;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.service.RcgFeedbackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.List;

@Component("tsadv_RcgFeedbackListener")
public class RcgFeedbackListener implements BeforeInsertEntityListener<RcgFeedback>, BeforeUpdateEntityListener<RcgFeedback>, AfterInsertEntityListener<RcgFeedback> {

    @Inject
    protected RcgFeedbackService rcgFeedbackService;

    @Inject
    private GoogleTranslateService googleTranslateService;

    @Override
    public void onBeforeInsert(RcgFeedback rcgFeedback, EntityManager entityManager) {
        validateRcgFeedback(rcgFeedback);
    }

    @Override
    public void onBeforeUpdate(RcgFeedback rcgFeedback, EntityManager entityManager) {
        validateRcgFeedback(rcgFeedback);
    }

    private void validateRcgFeedback(RcgFeedback rcgFeedback) {
        List<String> errors = rcgFeedbackService.validateRcgFeedback(rcgFeedback);

        if (!errors.isEmpty()) {
            StringBuilder errorsBuilder = new StringBuilder();
            errors.forEach(s -> errorsBuilder.append(s).append("\n"));

            throw new RcgFeedbackException(errorsBuilder.toString());
        }

        prepareRcgFeedback(rcgFeedback);
    }

    @Override
    public void onAfterInsert(RcgFeedback rcgFeedback, Connection connection) {
        rcgFeedbackService.sendNotification(rcgFeedback);
    }

    private void prepareRcgFeedback(RcgFeedback rcgFeedback) {
        rcgFeedback.setComment(firstUpperCase(rcgFeedback.getComment()));
        rcgFeedback.setTheme(firstUpperCase(rcgFeedback.getTheme()));

        translateRcgFeedback(rcgFeedback);
    }

    private String firstUpperCase(String text) {
        if (!StringUtils.isBlank(text)) {
            text = text.trim();
            if (text.length() > 1) {
                String firstLetter = text.substring(0, 1).toUpperCase();
                text = firstLetter + text.substring(1, text.length());
            }
        }
        return text;
    }

    private void translateRcgFeedback(RcgFeedback rcgFeedback) {
        String originalComment = rcgFeedback.getComment();
        rcgFeedback.setCommentEn(originalComment);
        rcgFeedback.setCommentRu(originalComment);

        if (StringUtils.isNotBlank(originalComment)) {
            try {
                rcgFeedback.setCommentEn(googleTranslateService.translate(originalComment, "en"));
                rcgFeedback.setCommentRu(googleTranslateService.translate(originalComment, "ru"));
            } catch (Exception ex) {
                rcgFeedback.setCommentEn(originalComment);
                rcgFeedback.setCommentRu(originalComment);
            }
        }

        String originalTheme = rcgFeedback.getTheme();
        rcgFeedback.setThemeEn(originalTheme);
        rcgFeedback.setThemeRu(originalTheme);

        if (StringUtils.isNotBlank(originalTheme)) {
            try {
                rcgFeedback.setThemeEn(googleTranslateService.translate(originalTheme, "en"));
                rcgFeedback.setThemeRu(googleTranslateService.translate(originalTheme, "ru"));
            } catch (Exception ex) {
                rcgFeedback.setThemeEn(originalTheme);
                rcgFeedback.setThemeRu(originalTheme);
            }
        }
    }
}
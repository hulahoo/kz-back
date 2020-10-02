package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackDirection;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackPojo;
import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.web.gui.components.WebRcgWall;
import kz.uco.tsadv.web.modules.recognition.feedback.rcgfeedback.RcgFeedbackDialogEdit;
import kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent.RcgWallComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RcgFeedbackPage extends AbstractRcgPage {


    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private CommonService commonService;

    @Override
    protected void loadPage(Map<String, Object> params) {
        loadFeedback();
    }

    private void loadFeedback() {
        PersonGroupExt currentPersonGroup = currentAssignmentExt.getPersonGroup();
        PersonExt currentPerson = currentPersonGroup.getPerson();

        WebRcgWall rcgWall = componentsFactory.createComponent(WebRcgWall.class);
        rcgWall.setWidthFull();

        RcgWallComponent rcgWallComponent = (RcgWallComponent) rcgWall.getComponent();
        rcgWallComponent.setLanguage(localeName);
        rcgWallComponent.setMessageBundle(messagesJson);
        rcgWallComponent.setChartFeedbackTypes(rcgFeedbackService.loadChartCategories());
        rcgWallComponent.setPageName("feedback");
        rcgWallComponent.setWallType(-1);
        rcgWallComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgWallComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);
        rcgWallComponent.setOpenFaqConsumer(o -> {
            String url;
            if (userSessionSource.getLocale().getLanguage().equalsIgnoreCase("ru")) {
                url = recognitionConfig.getFeedbackFaqUrlRu();
            } else {
                url = recognitionConfig.getFeedbackFaqUrlEn();
            }
            if (StringUtils.isNotBlank(url)) {
                showWebPage(url, ParamsMap.of("target", "_blank"));
            } else {
                showNotification("Error", "FAQ url is blank!", NotificationType.ERROR);
            }
        });
        ProfilePojo currentProfilePojo = createProfilePojo(
                currentPersonGroup.getId(),
                currentPerson.getId(),
                isLatin() ? currentPerson.getFirstNameLatin() : currentPerson.getFirstName(),
                isLatin() ? currentPerson.getLastNameLatin() : currentPerson.getLastName(),
                currentAssignmentExt.getOrganizationGroup().getOrganizationName());

        rcgWallComponent.setCurrentProfilePojo(currentProfilePojo);

        rcgWallComponent.setProfilePojo(currentProfilePojo);

        rcgWallComponent.setFeedbackConsumer(feedbackDirection -> {
            RcgFeedback feedback = metadata.create(RcgFeedback.class);
            feedback.setAuthor(currentPersonGroup);
            feedback.setFeedbackDate(new Date());
            feedback.setDirection(feedbackDirection);

            WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//            extWebWindowManager.setAdditionalClass(null);

//            Window.Editor editor = extWebWindowManager.openEditor(windowInfo,
//                    feedback,
//                    WindowManager.OpenType.DIALOG);

//            editor.addCloseWithCommitListener(() -> {
//                rcgWallComponent.callFunction("refreshFeedback");
//
//                if (feedbackDirection.equals(RcgFeedbackDirection.REQUEST)) {
//                    rcgFeedbackService.sendNotification((RcgFeedback) editor.getItem());
//                }
//            });
        });

        rcgWallComponent.setEditFeedbackConsumer(feedbackId -> {
            try {
                RcgFeedback rcgFeedback = rcgFeedbackService.findFeedback(feedbackId);
                if (rcgFeedback == null) {
                    throw new NullPointerException(String.format("Feedback by ID: [%s] not found!", feedbackId));
                }

                if (!rcgFeedback.getAuthor().getId().equals(currentPersonGroupId)) {
                    throw new NullPointerException("Access denied!");
                }

                WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//                extWebWindowManager.setAdditionalClass(null);

//                RcgFeedbackDialogEdit feedbackEditor = (RcgFeedbackDialogEdit) extWebWindowManager.openEditor(windowInfo,
//                        rcgFeedback,
//                        WindowManager.OpenType.DIALOG);

//                feedbackEditor.addCloseWithCommitListener(() -> {
//                    RcgFeedbackPojo feedbackPojo = rcgFeedbackService.parseFeedbackToPojo(feedbackEditor.getItem());
//                    feedbackPojo.setAttachmentChanged(feedbackEditor.isAttachmentChanged());

//                    rcgWallComponent.callFunction("reloadFeedback", gson.toJson(feedbackPojo));
//                });
            } catch (Exception ex) {
                showNotification("Error", ex.getMessage(), NotificationType.TRAY);
            }
        });

        rcgWallComponent.setSendFeedbackToAuthor(objects -> {
            try {
                String feedbackId = objects[0].toString();
                String authorId = objects[1].toString();
                PersonGroupExt author = findPersonGroup(authorId);
                Assert.notNull(author, "Author is null!");

                RcgFeedback requiredFeedBack = rcgFeedbackService.findFeedback(feedbackId);
                RcgFeedback rcgFeedback = metadata.create(RcgFeedback.class);
                rcgFeedback.setReceiver(author);
                rcgFeedback.setDirection(RcgFeedbackDirection.SEND);
                rcgFeedback.setTheme(requiredFeedBack.getTheme());

                WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//                extWebWindowManager.setAdditionalClass(null);

//                RcgFeedbackDialogEdit feedbackEditor = (RcgFeedbackDialogEdit) extWebWindowManager.openEditor(windowInfo,
//                        rcgFeedback,
//                        WindowManager.OpenType.DIALOG,
//                        ParamsMap.of("type", "answer", "requiredFeedBack", requiredFeedBack));

//                feedbackEditor.addCloseWithCommitListener(() -> {
//                    RcgFeedbackPojo feedbackPojo = rcgFeedbackService.parseFeedbackToPojo(feedbackEditor.getItem());
//                    feedbackPojo.setAttachmentChanged(feedbackEditor.isAttachmentChanged());

//                    rcgWallComponent.callFunction("refreshFeedback", gson.toJson(feedbackPojo));
//                });
            } catch (Exception ex) {
                showNotification("Error", ex.getMessage(), NotificationType.TRAY);
            }
        });

        /**
         * set person link consumer
         * */
        rcgWallComponent.setPersonLinkConsumer(personGroupId -> {
            openFrame(getParent(), "rcg-profile",
                    ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
        });

        add(rcgWall);
    }

    private PersonGroupExt findPersonGroup(String pgId) {
        return dataManager.load(LoadContext
                .create(PersonGroupExt.class)
                .setId(UUID.fromString(pgId))
                .setView("personGroupExt.rcg.feedback"));
    }
}
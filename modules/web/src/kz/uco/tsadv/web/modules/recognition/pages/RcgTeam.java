package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.Recognition;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackDirection;
import kz.uco.tsadv.web.gui.components.WebRcgTeam;
import kz.uco.tsadv.web.toolkit.ui.rcgteamcomponent.RcgTeamComponent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RcgTeam extends AbstractRcgPage {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    protected void loadPage(Map<String, Object> params) {
        WebRcgTeam rcgTeam = componentsFactory.createComponent(WebRcgTeam.class);
        rcgTeam.setWidthFull();
        RcgTeamComponent rcgTeamComponent = (RcgTeamComponent) rcgTeam.getComponent();
        rcgTeamComponent.setLanguage(localeName);
        rcgTeamComponent.setMessageBundle(messagesJson);
        rcgTeamComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgTeamComponent.setPersonLinkConsumer(personGroupId -> {
            Component component = getParent();
            openFrame(component, "rcg-profile",
                    ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
        });

        rcgTeamComponent.setGiveThanksConsumer(receiverPgId -> {
            boolean selfProfile = currentPersonGroupId.toString().equals(receiverPgId);

            if (!selfProfile) {
                PersonGroupExt personGroupExt = findPersonGroup(receiverPgId);
                if (personGroupExt == null) {
                    throw new NullPointerException(String.format("Person group by ID:[%s] not found!", receiverPgId));
                }

                Recognition recognition = metadata.create(Recognition.class);
                recognition.setReceiver(personGroupExt);

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RecognitionDialog.edit");

//                Window.Editor editor = extWebWindowManager.openEditor(windowInfo, recognition, WindowManager.OpenType.DIALOG);
//                editor.addCloseWithCommitListener(() -> {
//                    recognitionService.addBadges(recognition);
//                    loadPage(params);
//                });
            } else {
                showNotification("Warning", "This is current person!", NotificationType.TRAY);
            }
        });

        rcgTeamComponent.setShowAnalyticsConsumer(receiverPgId -> {
            PersonGroupExt personGroupExt = findPersonGroup(receiverPgId);
            if (personGroupExt == null) {
                throw new NullPointerException(String.format("Person group by ID:[%s] not found!", receiverPgId));
            }

            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-analytics");

//            extWebWindowManager.openWindow(windowInfo,
//                    WindowManager.OpenType.DIALOG,
//                    ParamsMap.of("analyticsPersonGroup", personGroupExt));
        });

        rcgTeamComponent.setRequestFeedbackConsumer(receiverPgId -> {
            PersonGroupExt personGroupExt = findPersonGroup(receiverPgId);
            if (personGroupExt == null) {
                throw new NullPointerException(String.format("Person group by ID:[%s] not found!", receiverPgId));
            }

            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);

            RcgFeedback feedback = metadata.create(RcgFeedback.class);
            feedback.setAuthor(currentAssignmentExt.getPersonGroup());
            feedback.setReceiver(personGroupExt);
            feedback.setFeedbackDate(new Date());
            feedback.setDirection(RcgFeedbackDirection.SEND);

            WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//            extWebWindowManager.setAdditionalClass(null);
//            extWebWindowManager.openEditor(windowInfo,
//                    feedback,
//                    WindowManager.OpenType.DIALOG);
        });

        add(rcgTeam);
    }

    private PersonGroupExt findPersonGroup(String pgId) {
        return dataManager.load(LoadContext
                .create(PersonGroupExt.class)
                .setId(UUID.fromString(pgId))
                .setView("personGroup.search"));
    }
}
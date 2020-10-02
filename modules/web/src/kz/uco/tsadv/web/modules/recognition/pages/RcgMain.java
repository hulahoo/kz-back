package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.PersonAward;
import kz.uco.tsadv.modules.recognition.Recognition;
import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.web.gui.components.WebRcgWall;
import kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent.RcgWallComponent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgMain extends AbstractRcgPage {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void loadPage(Map<String, Object> params) {
        PersonGroupExt currentPersonGroup = currentAssignmentExt.getPersonGroup();
        PersonExt currentPerson = currentPersonGroup.getPerson();

        WebRcgWall rcgWall = componentsFactory.createComponent(WebRcgWall.class);
        rcgWall.setWidthFull();

        RcgWallComponent rcgWallComponent = (RcgWallComponent) rcgWall.getComponent();
        rcgWallComponent.setLanguage(localeName);
        rcgWallComponent.setMessageBundle(messagesJson);
        rcgWallComponent.setPageName("main");
        rcgWallComponent.setWallType(-1);
        rcgWallComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgWallComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);
        rcgWallComponent.setOpenFaqConsumer(new Consumer() {
            @Override
            public void accept(Object o) {
                openFrame(getParent(), "rcg-faq");
            }
        });
        rcgWallComponent.setOpenLikesDialog(new Consumer<String>() {
            @Override
            public void accept(String recognitionId) {
                Consumer<String> openProfileConsumer = personGroupId -> {
                    Component component = getParent();
                    openFrame(component, "rcg-profile",
                            ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
                };

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-likes");

//                extWebWindowManager.setAdditionalClass(null);

//                extWebWindowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG,
//                        ParamsMap.of(RcgLikes.RECOGNITION_ID, recognitionId,
//                                RcgLikes.OPEN_PROFILE_CONSUMER, openProfileConsumer));
            }
        });

        rcgWallComponent.setPersonLinkConsumer(personGroupId -> {
            Component component = getParent();
            openFrame(component, "rcg-profile",
                    ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
        });

        rcgWallComponent.setGiveThanksConsumer(new Consumer<String>() {
            @Override
            public void accept(String personGroupId) {
                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RecognitionDialog.edit");

//                extWebWindowManager.setAdditionalClass(null);
                Recognition item = metadata.create(Recognition.class);
//                Window.Editor editor = extWebWindowManager.openEditor(windowInfo, item, WindowManager.OpenType.DIALOG);
//                editor.addCloseWithCommitListener(new Window.CloseWithCommitListener() {
//                    @Override
//                    public void windowClosedWithCommitAction() {
//                        recognitionService.addBadges(item);
//                        rcgWallComponent.setReloadAttributes("recognitions");
//                    }
//                });
            }
        });

        rcgWallComponent.setNominateConsumer(receiverPgId -> {
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$PersonAwardDialog.edit");

//            extWebWindowManager.setAdditionalClass("rcg-blue-head");
//            extWebWindowManager.openEditor(windowInfo, metadata.create(PersonAward.class), WindowManager.OpenType.DIALOG);
        });

        ProfilePojo currentProfilePojo = createProfilePojo(
                currentPersonGroup.getId(),
                currentPerson.getId(),
                isLatin() ? currentPerson.getFirstNameLatin() : currentPerson.getFirstName(),
                isLatin() ? currentPerson.getLastNameLatin() : currentPerson.getLastName(),
                currentAssignmentExt.getOrganizationGroup().getOrganizationName());

        rcgWallComponent.setCurrentProfilePojo(currentProfilePojo);

        rcgWallComponent.setProfilePojo(currentProfilePojo);

        add(rcgWall);
    }
}
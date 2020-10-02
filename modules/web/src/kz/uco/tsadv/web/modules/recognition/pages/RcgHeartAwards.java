package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.recognition.PersonAward;
import kz.uco.tsadv.modules.recognition.RcgFaq;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import kz.uco.tsadv.modules.recognition.pojo.NomineePojo;
import kz.uco.tsadv.web.gui.components.WebRcgHeartAward;
import kz.uco.tsadv.web.modules.recognition.entity.personaward.PersonAwardDetailEdit;
import kz.uco.tsadv.web.modules.recognition.entity.personaward.PersonAwardDialogEdit;
import kz.uco.tsadv.web.toolkit.ui.rcgheartawardcomponent.RcgHeartAwardComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgHeartAwards extends AbstractRcgPage {

    @Override
    protected void loadPage(Map<String, Object> params) {
        removeAll();

        WebRcgHeartAward rcgHeartAward = componentsFactory.createComponent(WebRcgHeartAward.class);
        rcgHeartAward.setWidthFull();

        RcgHeartAwardComponent rcgHeartAwardComponent = (RcgHeartAwardComponent) rcgHeartAward.getComponent();
        rcgHeartAwardComponent.setLanguage(localeName);
        rcgHeartAwardComponent.setMessageBundle(messagesJson);
        rcgHeartAwardComponent.setPageName("heartAwards");
        rcgHeartAwardComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
        rcgHeartAwardComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);
        rcgHeartAwardComponent.setAboutHaUrl(localeName.equalsIgnoreCase("en") ? recognitionConfig.getAboutHeartAwardUrlEn() : recognitionConfig.getAboutHeartAwardUrlRu());
        rcgHeartAwardComponent.setReadStoryFullConsumer(nomineeJson -> {
            try {
                NomineePojo nomineePojo = gson.fromJson(nomineeJson, NomineePojo.class);

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-nominee-detail");

//                extWebWindowManager.setAdditionalClass("rcg-ha-head");

                Consumer<String> openProfileConsumer = personGroupId -> {
                    if (StringUtils.isNotBlank(personGroupId)) {
                        openFrame(getParent(),
                                "rcg-profile",
                                ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
                    } else {
                        showNotification("PersonGroup ID is null or empty!");
                    }
                };

                Map<String, Object> windowParams = new HashMap<>();
                windowParams.put(RcgNomineeDetail.NOMINEE_POJO, nomineePojo);
                windowParams.put(RcgNomineeDetail.OPEN_PROFILE_CONSUMER, openProfileConsumer);

//                extWebWindowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, windowParams);
            } catch (Exception ex) {
                showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
            }
        });

        rcgHeartAwardComponent.setReadPersonAwardConsumer(personAwardId -> openPersonAwardEditor(personAwardId, true));

        rcgHeartAwardComponent.setEditPersonAwardConsumer(personAwardId -> openPersonAwardEditor(personAwardId, false));

        rcgHeartAwardComponent.setNominateConsumer(tabId -> {
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$PersonAwardDialog.edit");

//            extWebWindowManager.setAdditionalClass("rcg-blue-head");
//            PersonAwardDialogEdit personAwardDialogEdit = (PersonAwardDialogEdit) extWebWindowManager.openEditor(windowInfo, metadata.create(PersonAward.class), WindowManager.OpenType.DIALOG);
//            personAwardDialogEdit.addCloseWithCommitListener(() -> {
//                if (tabId == 2) {
//                    AwardStatus awardStatus = personAwardDialogEdit.getItem().getStatus();
//                    if (awardStatus != null && awardStatus.equals(AwardStatus.NOMINATED)) {
//                        rcgHeartAwardComponent.callFunction("refreshActiveTab");
//                    }
//                }
//            });
        });

        rcgHeartAwardComponent.setAboutHeartAwardsConsumer(new Consumer() {
            @Override
            public void accept(Object nullableObject) {
                String faqCode = "ABOUT_HEART_AWARDS";
                RcgFaq faqAboutHeartAwards = loadRcgFaq(faqCode);

                if (faqAboutHeartAwards == null) {
                    showNotification(getMessage("msg.warning.title"),
                            String.format(getMessage("rcg.faq.by.code.nf"), faqCode),
                            NotificationType.TRAY);
                    return;
                }

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-about-heart-awards");

//                extWebWindowManager.setAdditionalClass("rcg-ha-head");
//                extWebWindowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG,
//                        ParamsMap.of(RcgAboutHeartAwards.ABOUT_HEART_AWARDS, faqAboutHeartAwards));
            }
        });

        rcgHeartAwardComponent.setPersonLinkConsumer(personGroupId -> {
            Component component = getParent();
            openFrame(component, "rcg-profile",
                    ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId)));
        });

        add(rcgHeartAward);
    }

    private kz.uco.tsadv.modules.recognition.RcgFaq loadRcgFaq(String faqCode) {
        LoadContext<kz.uco.tsadv.modules.recognition.RcgFaq> loadContext = LoadContext.create(RcgFaq.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RcgFaq e " +
                        "where e.code = :code");
        query.setParameter("code", faqCode);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    private void openPersonAwardEditor(String personAwardId, boolean readOnly) {
        try {
            PersonAward personAward = getCheckedPersonAward(personAwardId);

            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$PersonAwardDetail.edit");

//            extWebWindowManager.setAdditionalClass("rcg-blue-head");
//            extWebWindowManager.openEditor(windowInfo,
//                    personAward,
//                    WindowManager.OpenType.DIALOG,
//                    ParamsMap.of(PersonAwardDetailEdit.READ_ONLY, readOnly));
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    private PersonAward getCheckedPersonAward(String personAwardId) {
        PersonAward personAward = loadPersonAward(personAwardId);
        if (personAward != null) {
            if (personAward.getAuthor().getId().equals(currentPersonGroupId)) {
                return personAward;
            }
            throw new RuntimeException(getMessage("rcg.person.award.na"));
        }
        throw new NullPointerException(getMessage("rcg.person.award.nf"));
    }

    private PersonAward loadPersonAward(String personAwardId) {
        LoadContext<PersonAward> loadContext = LoadContext.create(PersonAward.class)
                .setId(UUID.fromString(personAwardId))
                .setView("personAward.edit");
        return dataManager.load(loadContext);
    }
}
package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.vaadin.ui.JavaScript;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.PersonAward;
import kz.uco.tsadv.modules.recognition.PersonCoin;
import kz.uco.tsadv.modules.recognition.PersonPoint;
import kz.uco.tsadv.modules.recognition.Recognition;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedback;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackDirection;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackPojo;
import kz.uco.tsadv.modules.recognition.pojo.PreferencePojo;
import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.modules.recognition.pojo.RcgQuestionPojo;
import kz.uco.tsadv.modules.recognition.pojo.RecognitionTypePojo;
import kz.uco.tsadv.web.gui.components.WebRcgWall;
import kz.uco.tsadv.web.modules.recognition.entity.goods.GoodsCard;
import kz.uco.tsadv.web.modules.recognition.entity.personpreference.PersonPreferenceDialogEdit;
import kz.uco.tsadv.web.modules.recognition.feedback.rcgfeedback.RcgFeedbackDialogEdit;
import kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent.RcgWallComponent;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgProfile extends AbstractRcgPage {

    public static final String PROFILE_PERSON_GROUP_ID = "PROFILE_PERSON_GROUP_ID";
    protected UUID fromTeamPersonGroupId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void loadPage(Map<String, Object> params) {
        if (params.containsKey(PROFILE_PERSON_GROUP_ID)) {
            fromTeamPersonGroupId = (UUID) params.get(PROFILE_PERSON_GROUP_ID);
            loadProfilePage(fromTeamPersonGroupId);
        } else {
            loadProfilePage(currentPersonGroupId);
        }
    }

    private void loadProfilePage(UUID personGroupId) {
        removeAll();

        try {
            PersonGroupExt currentPersonGroup = currentAssignmentExt.getPersonGroup();
            AssignmentExt assignmentExt;

            boolean selfProfile = false;
            if (currentPersonGroup.getId().equals(personGroupId)) {
                selfProfile = true;
                assignmentExt = currentAssignmentExt;
            } else {
                assignmentExt = getAssignmentExt(personGroupId);
            }

            if (assignmentExt == null) {
                throw new Exception(getMessage("user.person.assignment.null"));
            }

            PersonGroupExt profilePersonGroup = assignmentExt.getPersonGroup();
            boolean isLatin = isLatin();

            PersonExt currentPerson = currentPersonGroup.getPerson();

            WebRcgWall rcgWall = componentsFactory.createComponent(WebRcgWall.class);
            rcgWall.setWidthFull();
            RcgWallComponent rcgWallComponent = (RcgWallComponent) rcgWall.getComponent();
            rcgWallComponent.setLanguage(localeName);
            rcgWallComponent.setMessageBundle(messagesJson);
            rcgWallComponent.setChartFeedbackTypes(rcgFeedbackService.loadChartCategories());
            rcgWallComponent.setAutomaticTranslate(isAutomaticTranslate() ? 1 : 0);
            rcgWallComponent.setAccessNominee(recognitionService.checkAccessNominees(profilePersonGroup.getId()) ? 1 : 0);
            rcgWallComponent.setShowAnalyticsConsumer(personGroupId13 -> {
                PersonGroupExt personGroupExt = findPersonGroup(personGroupId13);
                if (personGroupExt == null) {
                    throw new NullPointerException(String.format("Person group by ID:[%s] not found!", personGroupId13));
                }

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-analytics");

//                extWebWindowManager.openWindow(windowInfo,
//                        WindowManager.OpenType.DIALOG,
//                        ParamsMap.of("analyticsPersonGroup", personGroupExt));
            });
            rcgWallComponent.setShowGoodsCardConsumer(new Consumer<String>() {
                @Override
                public void accept(String goodsJson) {
                    WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                    WindowInfo windowInfo = windowConfig.getWindowInfo("goods-card");

//                    extWebWindowManager.setAdditionalClass(null);
//                    extWebWindowManager.openWindow(windowInfo,
//                            WindowManager.OpenType.DIALOG,
//                            ParamsMap.of(GoodsCard.GOODS_JSON, goodsJson,
//                                    GoodsCard.PARENT_COMPONENT, getParent()));
                }
            });
            rcgWallComponent.setOpenLikesDialog(recognitionId -> {
                Consumer<String> openProfileConsumer = personGroupId12 -> {
                    Component component = getParent();
                    openFrame(component, "rcg-profile",
                            ParamsMap.of(RcgProfile.PROFILE_PERSON_GROUP_ID, UUID.fromString(personGroupId12)));
                };

                WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-likes");

//                extWebWindowManager.setAdditionalClass(null);
//
//                extWebWindowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG,
//                        ParamsMap.of(RcgLikes.RECOGNITION_ID, recognitionId,
//                                RcgLikes.OPEN_PROFILE_CONSUMER, openProfileConsumer));
            });

            /**
             * set reload profile image consumer
             * */
            rcgWallComponent.setReloadProfileImageConsumer(nullableObject -> showNotification(getMessage("msg.info.title"),
                    getMessage("changed.profile.image"),
                    NotificationType.TRAY));

            /**
             * set open FAQ  consumer
             * */
            rcgWallComponent.setOpenFaqDetailConsumer(rcgFaqCode -> {
                WindowInfo windowInfo = windowConfig.getWindowInfo("rcg-faq-detail");
//                extWebWindowManager.openWindow(windowInfo,
//                        WindowManager.OpenType.DIALOG,
//                        ParamsMap.of(RcgFaqDetail.RCG_FAQ_CODE, rcgFaqCode));
            });

            /**
             * set person link consumer
             * */
            rcgWallComponent.setPersonLinkConsumer(personGroupId1 -> {
                try {
                    loadProfilePage(UUID.fromString(personGroupId1));
                } catch (Exception ex) {
                    showNotification(ex.getMessage());
                    ex.printStackTrace();
                }
            });

            /**
             * set give thanks consumer
             * */
            rcgWallComponent.setGiveThanksConsumer(receiverPgId -> {
                boolean selfProfile1 = currentPersonGroup.equals(profilePersonGroup);

                if (!selfProfile1) {
                    Recognition recognition = metadata.create(Recognition.class);
                    recognition.setReceiver(profilePersonGroup);

                    WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RecognitionDialog.edit");

//                    extWebWindowManager.setAdditionalClass(null);
//
//                    Window.Editor editor = extWebWindowManager.openEditor(windowInfo, recognition, WindowManager.OpenType.DIALOG);
//                    editor.addCloseWithCommitListener(new Window.CloseWithCommitListener() {
//                        @Override
//                        public void windowClosedWithCommitAction() {
//                            rcgWallComponent.setReloadAttributes("recognitions");
//                            recognitionService.addBadges(recognition);
//                            loadProfilePage(personGroupId);
//                        }
//                    });
                }
            });

            /**
             * set nominate consumer
             * */
            rcgWallComponent.setNominateConsumer(receiverPgId -> {
                boolean selfProfile12 = currentPersonGroup.equals(profilePersonGroup);

                if (!selfProfile12) {
                    PersonAward personAward = recognitionService.findDraftPersonAward(profilePersonGroup.getId());
                    if (personAward == null) {
                        personAward = metadata.create(PersonAward.class);
                    }
                    personAward.setAuthor(currentPersonGroup);
                    personAward.setReceiver(profilePersonGroup);

                    WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$PersonAwardDialog.edit");

//                    extWebWindowManager.setAdditionalClass("rcg-blue-head");
//                    extWebWindowManager.openEditor(windowInfo, personAward, WindowManager.OpenType.DIALOG);
                }
            });

            /**
             * set send HEART Coins consumer
             * */
            rcgWallComponent.setSendHeartCoinsConsumer(heartCoinPojo -> {
                try {
                    recognitionService.sendHeartCoins(heartCoinPojo);

                    JavaScript.eval("$('.rcg-pr-shc-input').val(0);$('.rcg-pr-shc-comment').val('');");

                    recognitionHelper.showNotificationWindow(
                            String.format("Heart Coins пользователю %s отправлен!", profilePersonGroup.getFullName()),
                            "images/recognition/gt-success.png");
                } catch (Exception ex) {
                    showNotification("Error", ex.getMessage(), NotificationType.TRAY);
                }
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

//                    extWebWindowManager.setAdditionalClass(null);
//
//                    RcgFeedbackDialogEdit feedbackEditor = (RcgFeedbackDialogEdit) extWebWindowManager.openEditor(windowInfo,
//                            rcgFeedback,
//                            WindowManager.OpenType.DIALOG);
//
//                    feedbackEditor.addCloseWithCommitListener(() -> {
//                        RcgFeedbackPojo feedbackPojo = rcgFeedbackService.parseFeedbackToPojo(feedbackEditor.getItem());
//                        feedbackPojo.setAttachmentChanged(feedbackEditor.isAttachmentChanged());

//                        rcgWallComponent.callFunction("reloadFeedback", gson.toJson(feedbackPojo));
//                    });
                } catch (Exception ex) {
                    showNotification("Error", ex.getMessage(), NotificationType.TRAY);
                }
            });

            rcgWallComponent.setPageName("profile");
            rcgWallComponent.setWallType(profilePersonGroup.equals(currentPersonGroup) ? 0 : 1);
            rcgWallComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());

            /**
             * set currentPersonGroup
             * */
            ProfilePojo currentProfile = createProfilePojo(
                    currentPersonGroup.getId(),
                    currentPerson.getId(),
                    isLatin ? currentPerson.getFirstNameLatin() : currentPerson.getFirstName(),
                    isLatin ? currentPerson.getLastNameLatin() : currentPerson.getLastName(),
                    currentAssignmentExt.getOrganizationGroup().getOrganizationName());

            currentProfile.setCoins(getPersonCoin(currentPersonGroup.getId()) + "");
            currentProfile.setPoints(getPersonPoint(currentPersonGroup.getId()) + "");

            /**
             * TODO: uncomment by customer's command
             *
             * currentProfile.setMedalCount(recognitionService.personMedalCount(currentPersonGroup.getId()));
             * */
            if (selfProfile) {
                currentProfile.setMedalCount(recognitionService.personMedalCount(currentPersonGroup.getId()));
            } else {
                currentProfile.setMedalCount(recognitionService.personMedalCount(personGroupId));
            }
            rcgWallComponent.setCurrentProfilePojo(currentProfile);

            /**
             * set profile PersonGroup
             * */
            PersonExt profilePerson = profilePersonGroup.getPerson();

            ProfilePojo profilePojo = createProfilePojo(
                    profilePersonGroup.getId(),
                    profilePerson.getId(),
                    isLatin ? profilePerson.getFirstNameLatin() : profilePerson.getFirstName(),
                    isLatin ? profilePerson.getLastNameLatin() : profilePerson.getLastName(),
                    assignmentExt.getOrganizationGroup().getOrganizationName(),
                    assignmentExt.getPositionGroup().getPositionName());
            profilePojo.setShowImageTooltip(recognitionService.giveCoinForPhoto(profilePersonGroup.getId()));
            profilePojo.setInTeam(recognitionService.checkProfileInTeam(profilePersonGroup.getId()) ? 1 : 0);

            /**
             * set profile recognition types
             * */
            profilePojo.setRecognitionTypes(loadRecognitionTypes(profilePersonGroup.getId()));

            /**
             * set profile preferences
             * */
            List<PreferencePojo> preferences = recognitionService.loadPersonPreferences(profilePersonGroup.getId());
            profilePojo.setPreferences(preferences);

            /**
             * check heard award
             * */
            profilePojo.setHeartAward(recognitionService.loadPersonHeartAward(profilePersonGroup.getId()));

            rcgWallComponent.setProfilePojo(profilePojo);

            if (selfProfile) {
                rcgWallComponent.setChangePreferenceConsumer(nullableObject -> {
                    WindowInfo windowInfo = windowConfig.getWindowInfo("person-preference-dialog-edit");

//                    PersonPreferenceDialogEdit preferenceDialogEdit = (PersonPreferenceDialogEdit) extWebWindowManager.openWindow(windowInfo,
//                            WindowManager.OpenType.DIALOG,
//                            ParamsMap.of(PersonPreferenceDialogEdit.PREFERENCES, preferences));
//
//                    preferenceDialogEdit.addCloseListener(actionId -> {
//                        if (actionId.equals("commit")) {
//                            recognitionHelper.showNotificationWindow(
//                                    getMessage("profile.preferences.success.saved"),
//                                    "images/recognition/gt-success.png");

                            /**
                             * @param editedPreferences is a reference to preferences which passed as editor parameter
                             */
//                            List<PreferencePojo> editedPreferences = preferenceDialogEdit.getPreferences();
//                            if (editedPreferences != null) {
//                                rcgWallComponent.setReloadAttributes("preferences");
//                            }
//                        }
//                    });
                });

                rcgWallComponent.setFeedbackConsumer(feedbackDirection -> {
                    RcgFeedback feedback = metadata.create(RcgFeedback.class);
                    feedback.setAuthor(currentPersonGroup);
                    feedback.setFeedbackDate(new Date());
                    feedback.setDirection(feedbackDirection);

                    WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//                    extWebWindowManager.setAdditionalClass(null);
//
//                    Window.Editor editor = extWebWindowManager.openEditor(windowInfo,
//                            feedback,
//                            WindowManager.OpenType.DIALOG);

//                    editor.addCloseWithCommitListener(() -> {
//                        rcgWallComponent.callFunction("refreshFeedback");
//
//                        if (feedbackDirection.equals(RcgFeedbackDirection.REQUEST)) {
//                            rcgFeedbackService.sendNotification((RcgFeedback) editor.getItem());
//                        }
//                    });
                });
            } else {
                rcgWallComponent.setSendPersonFeedback(personGroupId14 -> {
                    RcgFeedback feedback = metadata.create(RcgFeedback.class);
                    feedback.setAuthor(currentPersonGroup);
                    feedback.setFeedbackDate(new Date());
                    feedback.setReceiver(profilePersonGroup);
                    feedback.setDirection(RcgFeedbackDirection.SEND);

                    WindowInfo windowInfo = windowConfig.getWindowInfo("tsadv$RcgFeedbackDialog.edit");

//                    extWebWindowManager.setAdditionalClass(null);
//
//                    Window.Editor editor = extWebWindowManager.openEditor(windowInfo,
//                            feedback,
//                            WindowManager.OpenType.DIALOG);
//
//                    editor.addCloseWithCommitListener(() -> rcgWallComponent.callFunction("refreshFeedback"));
                });
            }

            /**
             * set question pojo
             * */
            RcgQuestionPojo questionPojo = recognitionService.loadActiveQuestion(currentPersonGroupId);
            rcgWallComponent.setQuestionPojo(questionPojo);

            if (questionPojo != null) {
                rcgWallComponent.setAnswerConsumer(new Consumer<String>() {
                    @Override
                    public void accept(String answerId) {
                        try {
                            recognitionService.sendQuestionAnswer(currentPersonGroup,
                                    questionPojo.getId(),
                                    UUID.fromString(answerId),
                                    questionPojo.getCoins());

                            recognitionHelper.showNotificationWindow(
                                    getMessage("thanks.for.answer"),
                                    "images/recognition/gt-success.png");

                            JavaScript.eval("$('.rcg-question-w').slideUp(200, function(){$(this).remove();})");
                        } catch (Exception ex) {
                            showNotification("Error", ex.getMessage(), NotificationType.TRAY);
                        }
                    }
                });
            }

            add(rcgWall);
        } catch (Exception ex) {
            addErrorLabel(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private List<RecognitionTypePojo> loadRecognitionTypes(UUID personGroupId) {
        return recognitionService.loadRecognitionTypes(personGroupId);
    }

    private Long getPersonCoin(UUID personGroupId) {
        PersonCoin personCoin = recognitionService.loadPersonCoin(personGroupId);
        if (personCoin != null) {
            return personCoin.getCoins();
        }
        return 0L;
    }

    private Long getPersonPoint(UUID personGroupId) {
        PersonPoint personPoint = recognitionService.loadPersonPoint(personGroupId);
        if (personPoint != null) {
            return personPoint.getPoints();
        }
        return 0L;
    }

    private AssignmentExt getAssignmentExt(UUID personGroupId) throws Exception {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where e.personGroup.id = :pgId " +
                        "  and e.primaryFlag = true " +
                        "and :systemDate between e.startDate and e.endDate");
        loadContextQuery.setParameter("pgId", personGroupId);
        loadContextQuery.setParameter("systemDate", CommonUtils.getSystemDate());
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("assignment.rcg.profile");
        return dataManager.load(loadContext);
    }

    private PersonGroupExt findPersonGroup(String pgId) {
        return dataManager.load(LoadContext
                .create(PersonGroupExt.class)
                .setId(UUID.fromString(pgId))
                .setView("personGroup.search"));
    }
}
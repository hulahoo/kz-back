package kz.uco.tsadv.web.toolkit.ui.rcgwallcomponent;

import com.haulmont.cuba.gui.components.Frame;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import kz.uco.base.web.init.App;
import kz.uco.tsadv.modules.recognition.feedback.RcgFeedbackDirection;
import kz.uco.tsadv.modules.recognition.pojo.HeartCoinPojo;
import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.modules.recognition.pojo.RcgQuestionPojo;
import kz.uco.tsadv.web.toolkit.ui.RcgJavaScriptComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @param wallType: 0 - self, 1 - other profile, -1 - all
 * @author adilbekov.yernar
 */

@SuppressWarnings("all")
@JavaScript({"rcgwallcomponent-connector-v3.4.js",
        "VAADIN/webjars/amcharts/amcharts.js",
        "VAADIN/webjars/amcharts/pie.js",
        "VAADIN/webjars/amcharts/plugins/export/export.min.js",
        "video.js"})
@StyleSheet({"video-js.css", "VAADIN/webjars/amcharts/plugins/export/export.css"})
public class RcgWallComponent extends RcgJavaScriptComponent {

    private Consumer openFaqConsumer;
    private Consumer<String> openFaqDetailConsumer;
    private Consumer<String> personLinkConsumer;
    private Consumer<String> giveThanksConsumer;
    private Consumer<String> nominateConsumer;
    private Consumer<HeartCoinPojo> sendHeartCoinsConsumer;
    private Consumer<String> answerConsumer;
    private Consumer<String> openLikesDialog;
    private Consumer changePreferenceConsumer;
    private Consumer reloadProfileImageConsumer;
    private Consumer<String> showGoodsCardConsumer;
    private Consumer<String> showAnalyticsConsumer;

    /**
     * отправить / запросить обратную связь [шапка]
     */
    private Consumer<RcgFeedbackDirection> feedbackConsumer;

    /**
     * отправить обратную связь [профиль]
     */
    private Consumer<String> sendPersonFeedback;
    private Consumer<String> editFeedbackConsumer;
    private Consumer<Object[]> sendFeedbackToAuthor;

    public RcgWallComponent() {
        addStyleName("rcg-wall-widget");

        addFunction("showAnalytics", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String personGroupId = arguments.getString(0);
                if (personGroupId == null || StringUtils.isBlank(personGroupId)) {
                    throw new NullPointerException("Person group ID is null!");
                }

                if (showAnalyticsConsumer != null) {
                    showAnalyticsConsumer.accept(personGroupId);
                }
            }
        });

        addFunction("showGoodsCard", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String goodsJson = arguments.getString(0);
                if (goodsJson != null && !goodsJson.equals("")) {
                    if (showGoodsCardConsumer != null) {
                        showGoodsCardConsumer.accept(goodsJson);
                    }
                }
            }
        });

        addFunction("openLikesDialog", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String recognitionId = arguments.getString(0);
                if (recognitionId != null && !recognitionId.equals("")) {
                    if (openLikesDialog != null) {
                        openLikesDialog.accept(recognitionId);
                    }
                }
            }
        });

        addFunction("openProfilePage", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String personGroupId = arguments.getString(0);
                if (personGroupId != null && !personGroupId.equals("")) {
                    if (personLinkConsumer != null) {
                        personLinkConsumer.accept(personGroupId);
                    }
                }
            }
        });

        addFunction("giveThanks", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String receiverPgId = arguments.getString(0);

                if (getWallType() != -1) {
                    if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                        throw new NullPointerException("Receiver person group ID is null!");
                    }
                }

                if (giveThanksConsumer != null) {
                    giveThanksConsumer.accept(null);
                }
            }
        });

        addFunction("nominate", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String receiverPgId = arguments.getString(0);

                if (getWallType() != -1) {
                    if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                        throw new NullPointerException("Receiver person group ID is null!");
                    }
                }

                if (nominateConsumer != null) {
                    nominateConsumer.accept(null);
                }
            }
        });

        addFunction("changePreference", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (changePreferenceConsumer != null) {
                    changePreferenceConsumer.accept(null);
                }
            }
        });

        addFunction("sendHeartCoins", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                try {
                    String receiverPgId = arguments.getString(0);
                    String coinsObject = arguments.getString(1);
                    String comment = arguments.getString(2);

                    if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                        throw new NullPointerException("Receiver person group ID is null!");
                    }

                    if (coinsObject == null || StringUtils.isBlank(receiverPgId)) {
                        throw new NullPointerException("Coins is null or empty!");
                    }

                    long coins = Long.parseLong(coinsObject);

                    if (sendHeartCoinsConsumer != null) {
                        sendHeartCoinsConsumer.accept(new HeartCoinPojo(receiverPgId, coins, comment));
                    }
                } catch (Exception ex) {
                    showErrorNotification(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        addFunction("sendQuestionAnswer", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                try {
                    String answerId = arguments.getString(0);

                    if (answerId == null || StringUtils.isBlank(answerId)) {
                        throw new NullPointerException("Answer ID is null!");
                    }

                    try {
                        UUID.fromString(answerId);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Incorrect format answer ID!");
                    }

                    if (answerConsumer != null) {
                        answerConsumer.accept(answerId);
                    }
                } catch (Exception ex) {
                    showErrorNotification(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        addFunction("openFaq", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (openFaqConsumer != null) {
                    openFaqConsumer.accept(null);
                }
            }
        });

        addFunction("openFaqDetail", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (openFaqDetailConsumer != null) {
                    String rcgFaqCode = arguments.getString(0);

                    if (rcgFaqCode == null || StringUtils.isBlank(rcgFaqCode)) {
                        throw new NullPointerException("Faq code is null!");
                    }

                    openFaqDetailConsumer.accept(rcgFaqCode);
                }
            }
        });

        addFunction("reloadProfileImage", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (reloadProfileImageConsumer != null) {
                    reloadProfileImageConsumer.accept(null);
                }
            }
        });

        addFunction("openFeedbackEditor", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String direction = arguments.getString(0);
                if (direction == null || StringUtils.isBlank(direction)) {
                    throw new NullPointerException("Type is null or empty!");
                }

                RcgFeedbackDirection feedbackDirection = RcgFeedbackDirection.fromId(direction.toUpperCase());
                if (feedbackDirection == null) {
                    throw new NullPointerException(String.format("Direction by ID: %s not found!", direction));
                }

                if (feedbackConsumer != null) {
                    feedbackConsumer.accept(feedbackDirection);
                }
            }
        });

        addFunction("sendFeedbackToAuthor", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String requestedFeedBackId = arguments.getString(0);
                String authorId = arguments.getString(1);
                if (StringUtils.isBlank(authorId)) {
                    throw new NullPointerException("Type is null or empty!");
                }
                Object[] objects = {requestedFeedBackId != null ? UUID.fromString(requestedFeedBackId) : null,
                        UUID.fromString(authorId)};
                sendFeedbackToAuthor.accept(objects);
            }
        });

        addFunction("sendPersonFeedback", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String personGroupId = arguments.getString(0);
                if (personGroupId == null || StringUtils.isBlank(personGroupId)) {
                    throw new NullPointerException("Person ID is null or empty!");
                }

                if (sendPersonFeedback != null) {
                    sendPersonFeedback.accept(personGroupId);
                }
            }
        });

        addFunction("editFeedback", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String feedbackId = arguments.getString(0);
                if (feedbackId == null || StringUtils.isBlank(feedbackId)) {
                    throw new NullPointerException("Feedback ID is null or empty!");
                }

                if (editFeedbackConsumer != null) {
                    editFeedbackConsumer.accept(feedbackId);
                }
            }
        });
    }

    public void setEditFeedbackConsumer(Consumer<String> editFeedbackConsumer) {
        this.editFeedbackConsumer = editFeedbackConsumer;
    }

    public void setFeedbackConsumer(Consumer<RcgFeedbackDirection> feedbackConsumer) {
        this.feedbackConsumer = feedbackConsumer;
    }

    public void setSendPersonFeedback(Consumer<String> sendPersonFeedback) {
        this.sendPersonFeedback = sendPersonFeedback;
    }

    public void setShowAnalyticsConsumer(Consumer<String> showAnalyticsConsumer) {
        this.showAnalyticsConsumer = showAnalyticsConsumer;
    }

    public void setOpenLikesDialog(Consumer<String> openLikesDialog) {
        this.openLikesDialog = openLikesDialog;
    }

    public void setReloadProfileImageConsumer(Consumer reloadProfileImageConsumer) {
        this.reloadProfileImageConsumer = reloadProfileImageConsumer;
    }

    private void showErrorNotification(String errorMessage) {
        App.getInstance()
                .getWindowManager()
                .showNotification("Warning", errorMessage, Frame.NotificationType.TRAY);
    }

    public void setOpenFaqDetailConsumer(Consumer<String> openFaqDetailConsumer) {
        this.openFaqDetailConsumer = openFaqDetailConsumer;
    }

    public void setOpenFaqConsumer(Consumer openFaqConsumer) {
        this.openFaqConsumer = openFaqConsumer;
    }

    public void setPersonLinkConsumer(Consumer<String> personLinkConsumer) {
        this.personLinkConsumer = personLinkConsumer;
    }

    public void setGiveThanksConsumer(Consumer<String> giveThanksConsumer) {
        this.giveThanksConsumer = giveThanksConsumer;
    }

    public void setChangePreferenceConsumer(Consumer changePreferenceConsumer) {
        this.changePreferenceConsumer = changePreferenceConsumer;
    }

    public void setShowGoodsCardConsumer(Consumer<String> showGoodsCardConsumer) {
        this.showGoodsCardConsumer = showGoodsCardConsumer;
    }

    public void setNominateConsumer(Consumer<String> nominateConsumer) {
        this.nominateConsumer = nominateConsumer;
    }

    public void setSendHeartCoinsConsumer(Consumer<HeartCoinPojo> sendHeartCoinsConsumer) {
        this.sendHeartCoinsConsumer = sendHeartCoinsConsumer;
    }

    public void setAnswerConsumer(Consumer<String> answerConsumer) {
        this.answerConsumer = answerConsumer;
    }

    @Override
    protected RcgWallComponentState getState() {
        return (RcgWallComponentState) super.getState();
    }

    public ProfilePojo getCurrentProfilePojo() {
        return getState().currentProfilePojo;
    }

    public void setCurrentProfilePojo(ProfilePojo currentProfilePojo) {
        getState().currentProfilePojo = currentProfilePojo;
    }

    public int getWallType() {
        return getState().wallType;
    }

    public void setWallType(int wallType) {
        getState().wallType = wallType;
    }

    public int getAccessNominee() {
        return getState().accessNominee;
    }

    public void setAccessNominee(int accessNominee) {
        getState().accessNominee = accessNominee;
    }

    public String getChartFeedbackTypes() {
        return getState().chartFeedbackTypes;
    }

    public void setChartFeedbackTypes(String chartFeedbackTypes) {
        getState().chartFeedbackTypes = chartFeedbackTypes;
    }

    public ProfilePojo getProfilePojo() {
        return getState().profilePojo;
    }

    public void setProfilePojo(ProfilePojo profilePojo) {
        getState().profilePojo = profilePojo;
    }

    public RcgQuestionPojo getQuestionPojo() {
        return getState().questionPojo;
    }

    public void setQuestionPojo(RcgQuestionPojo questionPojo) {
        getState().questionPojo = questionPojo;
    }

    public Consumer<Object[]> getSendFeedbackToAuthor() {
        return sendFeedbackToAuthor;
    }

    public void setSendFeedbackToAuthor(Consumer<Object[]> sendFeedbackToAuthor) {
        this.sendFeedbackToAuthor = sendFeedbackToAuthor;
    }
}
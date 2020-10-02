package kz.uco.tsadv.web.toolkit.ui.rcgteamcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import kz.uco.tsadv.web.toolkit.ui.RcgJavaScriptComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@JavaScript({"rcgteamcomponent-connector-v1.4.js"})
public class RcgTeamComponent extends RcgJavaScriptComponent {

    private Consumer<String> personLinkConsumer;
    private Consumer<String> giveThanksConsumer;
    private Consumer<String> showAnalyticsConsumer;
    private Consumer<String> requestFeedbackConsumer;

    public RcgTeamComponent() {
        addFunction("openProfilePage", (JavaScriptFunction) arguments -> {
            String personGroupId = arguments.getString(0);
            if (personGroupId != null && !personGroupId.equals("")) {
                if (personLinkConsumer != null) {
                    personLinkConsumer.accept(personGroupId);
                }
            }
        });

        addFunction("giveThanks", (JavaScriptFunction) arguments -> {
            String receiverPgId = arguments.getString(0);
            if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                throw new NullPointerException("Receiver person group ID is null!");
            }

            if (giveThanksConsumer != null) {
                giveThanksConsumer.accept(receiverPgId);
            }
        });

        addFunction("showAnalytics", (JavaScriptFunction) arguments -> {
            String receiverPgId = arguments.getString(0);
            if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                throw new NullPointerException("Receiver person group ID is null!");
            }

            if (showAnalyticsConsumer != null) {
                showAnalyticsConsumer.accept(receiverPgId);
            }
        });

        addFunction("requestFeedback", (JavaScriptFunction) arguments -> {
            String receiverPgId = arguments.getString(0);
            if (receiverPgId == null || StringUtils.isBlank(receiverPgId)) {
                throw new NullPointerException("Receiver person group ID is null!");
            }

            if (requestFeedbackConsumer != null) {
                requestFeedbackConsumer.accept(receiverPgId);
            }
        });
    }

    public void setRequestFeedbackConsumer(Consumer<String> requestFeedbackConsumer) {
        this.requestFeedbackConsumer = requestFeedbackConsumer;
    }

    public void setShowAnalyticsConsumer(Consumer<String> showAnalyticsConsumer) {
        this.showAnalyticsConsumer = showAnalyticsConsumer;
    }

    public void setPersonLinkConsumer(Consumer<String> personLinkConsumer) {
        this.personLinkConsumer = personLinkConsumer;
    }

    public void setGiveThanksConsumer(Consumer<String> giveThanksConsumer) {
        this.giveThanksConsumer = giveThanksConsumer;
    }

    @Override
    protected RcgTeamComponentState getState() {
        return (RcgTeamComponentState) super.getState();
    }
}
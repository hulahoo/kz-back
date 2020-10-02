package kz.uco.tsadv.web.toolkit.ui.rcgheartawardcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import kz.uco.tsadv.web.toolkit.ui.RcgJavaScriptComponent;

import java.util.Calendar;
import java.util.function.Consumer;

@JavaScript({"rcgheartawardcomponent-connector-v1.4.js"})
public class RcgHeartAwardComponent extends RcgJavaScriptComponent {

    private Consumer<String> readStoryFullConsumer;
    private Consumer<String> readPersonAwardConsumer;
    private Consumer<String> editPersonAwardConsumer;
    private Consumer<Integer> nominateConsumer;
    private Consumer aboutHeartAwardsConsumer;
    private Consumer<String> personLinkConsumer;

    public RcgHeartAwardComponent() {
        addStyleName("rcg-heart-award-widget");

        setCurrentYear(Calendar.getInstance().get(Calendar.YEAR));

        setLastYear(getCurrentYear() - 1);

        addFunction("readStoryFull", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String nomineeJson = arguments.getString(0);
                if (nomineeJson != null && !nomineeJson.equals("")) {
                    if (readStoryFullConsumer != null) {
                        readStoryFullConsumer.accept(nomineeJson);
                    }
                }
            }
        });

        addFunction("readPersonAward", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String personAwardId = arguments.getString(0);
                if (personAwardId != null && !personAwardId.equals("")) {
                    if (readPersonAwardConsumer != null) {
                        readPersonAwardConsumer.accept(personAwardId);
                    }
                }
            }
        });

        addFunction("editPersonAward", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                String personAwardId = arguments.getString(0);
                if (personAwardId != null && !personAwardId.equals("")) {
                    if (editPersonAwardConsumer != null) {
                        editPersonAwardConsumer.accept(personAwardId);
                    }
                }
            }
        });

        addFunction("nominate", (JavaScriptFunction) arguments -> {
            Double tabId = arguments.getNumber(0);
            if (nominateConsumer != null) {
                nominateConsumer.accept(tabId.intValue());
            }
        });

        addFunction("aboutHeartAwards", (JavaScriptFunction) arguments -> {
            if (aboutHeartAwardsConsumer != null) {
                aboutHeartAwardsConsumer.accept(null);
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
    }

    public void setPersonLinkConsumer(Consumer<String> personLinkConsumer) {
        this.personLinkConsumer = personLinkConsumer;
    }

    public void setAboutHeartAwardsConsumer(Consumer aboutHeartAwardsConsumer) {
        this.aboutHeartAwardsConsumer = aboutHeartAwardsConsumer;
    }

    public void setNominateConsumer(Consumer<Integer> nominateConsumer) {
        this.nominateConsumer = nominateConsumer;
    }

    public void setReadPersonAwardConsumer(Consumer<String> readPersonAwardConsumer) {
        this.readPersonAwardConsumer = readPersonAwardConsumer;
    }

    public void setEditPersonAwardConsumer(Consumer<String> editPersonAwardConsumer) {
        this.editPersonAwardConsumer = editPersonAwardConsumer;
    }

    public void setReadStoryFullConsumer(Consumer<String> readStoryFullConsumer) {
        this.readStoryFullConsumer = readStoryFullConsumer;
    }

    public int getCurrentYear() {
        return getState().currentYear;
    }

    public void setCurrentYear(int currentYear) {
        getState().currentYear = currentYear;
    }

    public String getAboutHaUrl() {
        return getState().aboutHaUrl;
    }

    public void setAboutHaUrl(String aboutHaUrl) {
        getState().aboutHaUrl = aboutHaUrl;
    }

    public int getLastYear() {
        return getState().lastYear;
    }

    public void setLastYear(int lastYear) {
        getState().lastYear = lastYear;
    }

    @Override
    protected RcgHeartAwardComponentState getState() {
        return (RcgHeartAwardComponentState) super.getState();
    }
}
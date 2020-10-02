package kz.uco.tsadv.web.modules.recognition.entity.personpreference;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.pojo.PreferencePojo;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class PersonPreferenceDialogEdit extends AbstractWindow {

    public static final String PREFERENCES = "PPDE_PREFERENCES";

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private ScrollBoxLayout scrollBox;

    @Inject
    private UserSession userSession;

    private List<PreferencePojo> preferences = null;

    private PersonGroupExt personGroup = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(PREFERENCES)) {
            personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            if (personGroup == null) {
                throw new NullPointerException(getMessage("user.person.null"));
            }

            preferences = (List<PreferencePojo>) params.get(PREFERENCES);

            initForm();
        }
    }

    private void initForm() {
        if (preferences != null && !preferences.isEmpty()) {
            preferences.forEach(new Consumer<PreferencePojo>() {
                @Override
                public void accept(PreferencePojo preferencePojo) {
                    scrollBox.add(createBlock(preferencePojo));
                }
            });
        }
    }

    private Component createBlock(PreferencePojo preference) {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setSpacing(true);
        wrapper.setStyleName("rcg-ch-preference-w");

        /**
         * preference type name
         * */
        HBoxLayout titleWrapper = componentsFactory.createComponent(HBoxLayout.class);
        titleWrapper.setWidthFull();

        Label titleLabel = componentsFactory.createComponent(Label.class);
        titleLabel.setValue(preference.getTypeName());
        titleLabel.setStyleName("rcg-ch-preference-t");
        titleWrapper.add(titleLabel);
        titleWrapper.expand(titleLabel);

        if (BooleanUtils.isTrue(preference.getShowCoinsDescription())) {
            Label infoLabel = componentsFactory.createComponent(Label.class);
            infoLabel.setStyleName("rcg-ch-preference-t-i rcg-tooltip");
            infoLabel.setAlignment(Alignment.MIDDLE_RIGHT);
            infoLabel.setValue("+" + preference.getCoins() + " HEART Coins");
            titleWrapper.add(infoLabel);
        }
        wrapper.add(titleWrapper);

        /**
         * preference description
         * */
        TextArea textArea = componentsFactory.createComponent(TextArea.class);
        textArea.setStyleName("rcg-ch-preference-d");
        textArea.setHeight("80px");
        textArea.setWidthFull();
        textArea.setValue(preference.getDescription());
        textArea.addValueChangeListener(stringValueChangeEvent -> {
            Object value = stringValueChangeEvent;
            preference.setDescription(value != null ? value.toString() : null);
        });
        wrapper.add(textArea);
        return wrapper;
    }

    public void save() {
        try {
            if (preferences != null && !preferences.isEmpty() && personGroup != null) {
                List<PreferencePojo> persistPreferences = recognitionService.savePersonPreference(personGroup, preferences);

                preferences.clear();
                preferences.addAll(persistPreferences);

                close("commit");
            }
        } catch (Exception ex) {
            showNotification(getMessage("msg.error.title"), ex.getMessage(), NotificationType.TRAY);
        }
    }

    public List<PreferencePojo> getPreferences() {
        return preferences;
    }

    public void cancel() {
        close("close", true);
    }
}
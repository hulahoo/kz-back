package kz.uco.tsadv.web.modules.recognition.pages;

import com.google.gson.Gson;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.components.ExtWebWindowManager;
import kz.uco.tsadv.config.RecognitionConfig;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.recognition.RecognitionProfileSetting;
import kz.uco.tsadv.modules.recognition.pojo.ProfilePojo;
import kz.uco.tsadv.service.RcgFeedbackService;
import kz.uco.tsadv.service.RecognitionService;
import kz.uco.tsadv.web.modules.recognition.RecognitionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

/**
 * @author adilbekov.yernar
 */
public abstract class AbstractRcgPage extends AbstractFrame {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    protected Gson gson = new Gson();

    protected RecognitionHelper recognitionHelper = new RecognitionHelper();

    @Inject
    protected RecognitionConfig recognitionConfig;

    @Inject
    protected Events events;

    @Inject
    protected RecognitionService recognitionService;

    @Inject
    protected RcgFeedbackService rcgFeedbackService;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected UserSession userSession;

    @Inject
    protected Metadata metadata;

    @Inject
    protected ExtWebWindowManager extWebWindowManager;

    @Inject
    protected ComponentsFactory componentsFactory;

    protected WindowConfig windowConfig = AppBeans.get(WindowConfig.class);

    protected AssignmentExt currentAssignmentExt;

    protected UUID currentPersonGroupId;

    protected ResourceBundle resourceBundle;

    protected String messagesJson;

    protected String localeName = "ru";

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Locale currentLocale = userSession.getLocale();
        localeName = currentLocale.getLanguage();

        resourceBundle = ResourceBundle.getBundle("kz.uco.tsadv.web.modules.recognition.pages.messages", currentLocale, new UTF8Control());
        resourceBundleToJson(resourceBundle);

//        extWebWindowManager.setRecognition(true); TODO переход на 7 версия

        currentAssignmentExt = userSession.getAttribute("assignment");

        currentPersonGroupId = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID);

        try {
            if (currentPersonGroupId == null) {
                throw new NullPointerException(getMessage("user.person.null"));
            }

            if (currentAssignmentExt == null) {
                throw new NullPointerException(getMessage("user.person.assignment.null"));
            }

            loadPage(params);
        } catch (Exception ex) {
            addErrorLabel(ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected boolean isAutomaticTranslate() {
        RecognitionProfileSetting profileSetting = recognitionService.loadProfileSettings();
        if (profileSetting == null) {
            return true;
        }
        return profileSetting.getAutomaticTranslate();
    }

    protected boolean isLatin() {
        return localeName.equalsIgnoreCase("en");
    }

    protected void resourceBundleToJson(final ResourceBundle bundle) {
        final Map<String, String> bundleMap = new HashMap<>();
        for (String key : bundle.keySet()) {
            String value = bundle.getString(key);
            bundleMap.put(key, value);
        }
        messagesJson = gson.toJson(bundleMap);
    }

    protected abstract void loadPage(Map<String, Object> params);

    protected ProfilePojo createProfilePojo(UUID pgId, UUID pId, String firstName, String lastName, String organization) {
        ProfilePojo profilePojo = metadata.create(ProfilePojo.class);
        profilePojo.setId(pgId);
        profilePojo.setPId(pId.toString());
        profilePojo.setFirstName(firstName);
        profilePojo.setLastName(lastName);
        profilePojo.setOrganization(organization);
        return profilePojo;
    }

    protected ProfilePojo createProfilePojo(UUID pgId, UUID pId, String firstName, String lastName, String organization, String position) {
        ProfilePojo profilePojo = createProfilePojo(pgId, pId, firstName, lastName, organization);
        profilePojo.setPosition(position);
        return profilePojo;
    }

    protected void addErrorLabel(String message) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(message);
        label.setAlignment(Alignment.MIDDLE_CENTER);
        label.setSizeFull();
        add(label);
    }
}

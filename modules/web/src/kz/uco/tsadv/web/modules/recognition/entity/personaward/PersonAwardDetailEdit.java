package kz.uco.tsadv.web.modules.recognition.entity.personaward;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.components.actions.BaseAction;

import java.util.Map;

public class PersonAwardDetailEdit extends PersonAwardDialogEdit {

    public static final String READ_ONLY = "PADE_READ_ONLY";

    private boolean readOnly = true;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey(READ_ONLY)) {
            readOnly = (boolean) params.get(READ_ONLY);
        }

        super.init(params);

        instruction.setVisible(!readOnly);
//        suggestionPickerField.setVisible(false);
        nomineeNameLabel.setVisible(true);
    }

    @Override
    protected void initButtonsAction() {
        saveDraft.setVisible(false);
        windowCommit.setVisible(false);
        save.setAction(new BaseAction("save") {
            @Override
            public void actionPerform(Component component) {
                if (validateAll()) {
                    commitAndClose();
                }
            }
        });

        close.setAction(new BaseAction("close") {
            @Override
            public void actionPerform(Component component) {
                close("close", true);
            }
        });

        save.setVisible(!readOnly);
        close.setVisible(readOnly);
    }

    @Override
    public void ready() {
        super.ready();

        receiverBlock.setVisible(true);
        nomineeNameLabel.setValue(getItem().getReceiver().getFirstLastName());
        historyTextArea.setEditable(!readOnly);
        whyTextArea.setEditable(!readOnly);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            recognitionHelper.showNotificationWindow(
                    getMessage("award.save.success"),
                    "images/recognition/n-success.png");
        }
        return true;
    }

    /**
     * Don't remove this. Method is overrided
     */
    @Override
    protected void postValidate(ValidationErrors errors) {
    }

    /**
     * Don't remove this. Method is overrided
     */
    @Override
    protected void postInit() {
    }

    /**
     * Don't remove this. Method is overrided
     */
    @Override
    protected void initSuggestionField() {
    }

    /**
     * Don't remove this. Method is overrided
     */
    @Override
    protected void initSuggestionPickerListener() {
    }
}
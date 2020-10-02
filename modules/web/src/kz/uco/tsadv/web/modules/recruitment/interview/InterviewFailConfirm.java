package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.ResizableTextArea;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;

import javax.inject.Inject;
import java.util.Map;

public class InterviewFailConfirm extends AbstractWindow {

    @Inject
    private LookupField<Object> interviewReason;
    @Inject
    private ResizableTextArea reason;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

//        interviewReason.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                changeRequiredFields(e.getValue());
//            }
//        });

        addAction(new BaseAction("yes") {
            @Override
            public void actionPerform(Component component) {
                if (validateAll()) {
                    close(this.id);
                }
            }
        });

        addAction(new BaseAction("no") {
            @Override
            public void actionPerform(Component component) {
                close(this.id, true);
            }
        });
    }

    public DicInterviewReason getDicInterviewReason() {
        return (DicInterviewReason) interviewReason.getValue();
    }

    public String getReason() {
        return (String) reason.getValue();
    }

    private void changeRequiredFields(Object object) {
        reason.setRequired(false);

        if (object != null) {
            DicInterviewReason dicInterviewReason = (DicInterviewReason) object;
            String code = dicInterviewReason.getCode();
            if (code != null && code.equalsIgnoreCase("other")) {
                reason.setRequired(true);
            }
        }
    }

}
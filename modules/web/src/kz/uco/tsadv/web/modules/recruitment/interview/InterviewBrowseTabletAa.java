package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;

import java.util.HashMap;

public class InterviewBrowseTabletAa extends InterviewBrowseTablet {

    @Override
    public void startInterview() {
        AbstractWindow abstractWindow = openWindow("interview-aa-questionnaire",
                WindowManager.OpenType.THIS_TAB,
                new HashMap<String, Object>() {{
                    put("interview", interviewsDs.getItem());
                    put("person", reloadPerson("person.minimal"));
                }});

        abstractWindow.addCloseListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                interviewsDs.refresh();
            }
        });
    }
}
package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Label;
import kz.uco.tsadv.modules.recognition.RcgFaq;

import javax.inject.Inject;
import java.util.Map;

public class RcgAboutHeartAwards extends AbstractWindow {

    public static final String ABOUT_HEART_AWARDS = "RAHA_ABOUT_HEART_AWARDS";

    @Inject
    private Label content;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(ABOUT_HEART_AWARDS)) {
            loadPage((RcgFaq) params.get(ABOUT_HEART_AWARDS));
        }
    }

    protected void loadPage(RcgFaq rcgFaq) {
        setCaption(rcgFaq.getTitle());
        content.setValue(rcgFaq.getContent());
    }
}
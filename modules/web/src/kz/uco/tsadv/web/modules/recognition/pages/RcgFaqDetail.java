package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.RcgFaq;

import javax.inject.Inject;
import java.util.Map;

public class RcgFaqDetail extends AbstractWindow {

    public static String RCG_FAQ_CODE = "RCG_FAQ_CODE";

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(RCG_FAQ_CODE)) {
            initFaq(String.valueOf(params.get(RCG_FAQ_CODE)));
        }
    }

    private void initFaq(String code) {
        RcgFaq rcgFaq = loadRcgFaq(code);
        if (rcgFaq != null) {
            setCaption(rcgFaq.getTitle());

            Label contentLabel = componentsFactory.createComponent(Label.class);
            contentLabel.setHtmlEnabled(true);
            contentLabel.setWidthFull();
            contentLabel.setAlignment(Alignment.MIDDLE_CENTER);
            contentLabel.setValue(rcgFaq.getContent());
            add(contentLabel);
            expand(contentLabel);
        }
    }

    private RcgFaq loadRcgFaq(String code) {
        LoadContext<RcgFaq> loadContext = LoadContext.create(RcgFaq.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RcgFaq e " +
                        "where e.code = :code");
        query.setParameter("code", code);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }
}
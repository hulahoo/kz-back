package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.Layout;
import kz.uco.tsadv.web.toolkit.ui.jplayer.JPlayerServerComponent;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;

import javax.inject.Inject;
import java.util.Map;

public class VideoScreen extends AbstractWindow {
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;
    private JobRequest jobRequest;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        jobRequest = (JobRequest) params.get("jobRequest");
    }

    @Override
    public void ready() {
        super.ready();
        Component box = componentsFactory.createComponent(VBoxLayout.class);
        Layout vBox = (Layout) WebComponentsHelper.unwrap(box);
        JPlayerServerComponent player = new JPlayerServerComponent();
        String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
        player.setUrl(webAppUrl + "/dispatch/video/" + jobRequest.getId().toString());
        player.setSourceType("video/mp4");
        vBox.addComponent(player);
        this.add(box);
    }
}
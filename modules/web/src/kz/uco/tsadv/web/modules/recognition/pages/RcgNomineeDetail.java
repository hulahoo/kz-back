package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recognition.pojo.NomineePojo;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Consumer;

public class RcgNomineeDetail extends AbstractWindow {

    public static final String NOMINEE_POJO = "RND_NOMINEE_POJO";

    public static final String OPEN_PROFILE_CONSUMER = "OPEN_PROFILE_CONSUMER";

    @Inject
    private Label imageLabel, descriptionLabel;
    @Inject
    private HtmlBoxLayout htmlBoxLayout;
    @Inject
    private ComponentsFactory componentsFactory;
    private Consumer<String> openProfileConsumer = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(NOMINEE_POJO)) {
            if (params.containsKey(OPEN_PROFILE_CONSUMER)) {
                openProfileConsumer = (Consumer<String>) params.get(OPEN_PROFILE_CONSUMER);
            }

            loadPage((NomineePojo) params.get(NOMINEE_POJO));
        }
    }

    private void loadPage(NomineePojo nomineePojo) {
        setCaption(getMessage("nominee.detail.winner") + " " + nomineePojo.getYear());
        imageLabel.setValue(String.format(
                "<div class=\"rcg-nominee-detail-image-w\"><img src=\"./dispatch/person_image/%s\"/><i/></div>",
                nomineePojo.getPId()));

        htmlBoxLayout.setTemplateContents(String.format("<div class=\"rcg-nominee-detail-info-w\">" +
                        "<div location=\"profileLink\" class=\"\"></div>" +
                        "<div class=\"rcg-nominee-detail-info-pos\">%s</div>" +
                        "<div class=\"rcg-nominee-detail-info-org\">%s</div>" +
                        "</div>",
                nomineePojo.getPosition(),
                nomineePojo.getOrganization()));

        LinkButton profileLink = componentsFactory.createComponent(LinkButton.class);
        profileLink.setId("profileLink");
        profileLink.setStyleName("rcg-nominee-detail-info-fn");
        profileLink.setCaption(nomineePojo.getFullName());

        if (openProfileConsumer != null) {
            profileLink.setAction(new BaseAction("profile") {
                @Override
                public void actionPerform(Component component) {
                    close("close", true);
                    openProfileConsumer.accept(nomineePojo.getPgId());
                }
            });
        }
        htmlBoxLayout.add(profileLink);

        descriptionLabel.setValue(nomineePojo.getDescription());
    }
}
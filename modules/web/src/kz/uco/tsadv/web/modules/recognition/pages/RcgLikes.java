package kz.uco.tsadv.web.modules.recognition.pages;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RcgLikes extends AbstractWindow {

    public static final String RECOGNITION_ID = "RL_RECOGNITION_ID";
    public static final String OPEN_PROFILE_CONSUMER = "RL_OPEN_PROFILE_CONSUMER";

    @Inject
    private CollectionDatasource<PersonGroupExt, UUID> likesDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Table<PersonGroupExt> likesTable;

    private Consumer<String> openProfileConsumer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(RECOGNITION_ID) && params.containsKey(OPEN_PROFILE_CONSUMER)) {
            this.openProfileConsumer = (Consumer<String>) params.get(OPEN_PROFILE_CONSUMER);
            loadTable((String) params.get(RECOGNITION_ID));
        }
    }

    private void loadTable(String recognitionId) {
        likesDs.refresh(ParamsMap.of("recognitionId", UUID.fromString(recognitionId)));
    }

    public Component generateImage(PersonGroupExt personGroupExt) {
        Label imageLabel = componentsFactory.createComponent(Label.class);
        imageLabel.setHtmlEnabled(true);

        String imageUrl = String.format("./dispatch/person_image/%s", personGroupExt.getPerson().getId().toString());
        imageLabel.setValue(String.format("<img src=\"%s\" style=\"%s\"/>",
                imageUrl,
                "width:50px;height:50px;border-radius:50%;margin:5px 0"));
        return imageLabel;
    }

    public void openProfile(PersonGroupExt personGroupExt, String name) {
        try {
            if (openProfileConsumer != null) {
                close("", true);
                openProfileConsumer.accept(personGroupExt.getId().toString());
            } else {
                showNotification("msg.warning.title",
                        getMessage("rcg.likes.open.profile.null"),
                        NotificationType.TRAY);
            }
        } catch (Exception ex) {
            showNotification("msg.warning.title",
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    @Override
    public void ready() {
        super.ready();

        likesTable.getColumn("image").setWidth(70);
    }


}
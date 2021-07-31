package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import java.util.Objects;

@UiController("base$PersonExt.browse")
@UiDescriptor("person-ext-browse.xml")
@LookupComponent("personTable")
@LoadDataBeforeShow
public class PersonExtBrowse extends StandardLookup<PersonExt> {
    private static final String IMAGE_CELL_HEIGHT = "40px";
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private CollectionLoader<PersonExt> personDl;
    @Inject
    private Notifications notifications;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private GroupTable<PersonExt> personTable;

    @Subscribe("personTable.create")
    public void onPersonExtsTableCreate(Action.ActionPerformedEvent event) {
        DicPersonType dicPersonType = dataManager.load(DicPersonType.class)
                .query("select e from tsadv$DicPersonType e " +
                        " where e.code = 'GRANT'")
                .view(View.LOCAL)
                .list().stream().findFirst().orElse(null);
        if (dicPersonType != null) {
            PersonGroupExt personGroupExt = metadata.create(PersonGroupExt.class);
            screenBuilders.editor(PersonExt.class, this)
                    .withScreenId("base$PersonExt.edit")
                    .newEntity()
                    .withInitializer(personExt -> {
                        personExt.setGroup(personGroupExt);
                        personExt.setType(dicPersonType);
                    }).build().show()
                    .addAfterCloseListener(afterCloseEvent -> personDl.load());
        } else {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("noTypeGrant")).show();
        }
    }

    @Subscribe("personTable.edit")
    public void onPersonExtsTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(PersonExt.class, this)
                .editEntity(Objects.requireNonNull(personTable.getSingleSelected()))
                .withScreenId("base$PersonExt.edit").build().show()
                .addAfterCloseListener(afterCloseEvent -> personDl.load());
    }

    public void personCard(PersonExt item, String columnId) {
        getWindow().openEditor("person-card-no-assignment",
                item.getGroup(), WindowManager.OpenType.THIS_TAB);
    }

    public Component generateUserImageCell(PersonExt entity) {
        Image image = WebCommonUtils.setImage(entity.getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }
}
package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.LookupComponent;
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
        AbstractEditor editor = getWindow().openEditor("person-card-no-assignment",
                item.getGroup(), WindowManager.OpenType.THIS_TAB);
        editor.addAfterCloseListener(e -> personDl.load());
    }

    public Component generateUserImageCell(PersonExt entity) {
        FileDescriptor personImageFileDescriptor = getPersonImageFileDescriptor(entity);
        Image image = WebCommonUtils.setImage(personImageFileDescriptor, null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }

    protected FileDescriptor getPersonImageFileDescriptor(PersonExt person){
        FileDescriptor actualFileDesctiptor = null;
        if(person.getGroup() != null && person.getGroup().getImage() != null){
            actualFileDesctiptor = person.getGroup().getImage();
        }else{
            actualFileDesctiptor = person.getImage();
        }

        return actualFileDesctiptor;
    }
}
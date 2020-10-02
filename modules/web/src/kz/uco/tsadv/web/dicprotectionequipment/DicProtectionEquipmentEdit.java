package kz.uco.tsadv.web.dicprotectionequipment;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipment;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentPhoto;

import javax.inject.Inject;
import java.util.Map;

public class DicProtectionEquipmentEdit extends AbstractEditor<DicProtectionEquipment> {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Table<DicProtectionEquipmentPhoto> dicProtectionEquipmentPhotoTable;

    @Override
    protected void initNewItem(DicProtectionEquipment item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    @Override
    public void init(Map<String, Object> params) {
        dicProtectionEquipmentPhotoTable.addGeneratedColumn("name", entity -> {
            Image image = componentsFactory.createComponent(Image.class);
            image.setScaleMode(Image.ScaleMode.CONTAIN);
            image.setHeight("40");
            image.setWidth("40");
            if (entity.getAttachment() != null) {
                FileDescriptor userImageFile = entity.getAttachment();
                image.setSource(FileDescriptorResource.class).setFileDescriptor(userImageFile);
            }

            Label userLogin = componentsFactory.createComponent(Label.class);
            userLogin.setValue(entity.getAttachment() != null ? entity.getAttachment().getName() : null);
            userLogin.setAlignment(Alignment.MIDDLE_LEFT);

            HBoxLayout hBox = componentsFactory.createComponent(HBoxLayout.class);
            hBox.setSpacing(true);
            hBox.add(image);
            hBox.add(userLogin);

            return hBox;
        });
    }
}
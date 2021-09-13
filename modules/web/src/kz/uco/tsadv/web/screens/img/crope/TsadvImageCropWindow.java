package kz.uco.tsadv.web.screens.img.crope;

import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.vaadin.ui.Layout;
import cubacn.cmp.crop.web.screens.imgcrop.ImageCropWindow;
import cubacn.cmp.crop.web.screens.imgcrop.ImageCropWindowOptions;
import cubacn.cmp.crop.web.toolkit.ui.imgcrop.ImgCropServerComponent;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.Field;

@UiController("cubacn_ImageCropWindow")
@UiDescriptor("tsadv-image-crop-window.xml")
public class TsadvImageCropWindow extends ImageCropWindow {

    @Inject
    protected VBoxLayout cropCmpCtn;
    @Inject
    protected VBoxLayout rootCtn;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {

        Field optionsField = ReflectionUtils.findField(this.getClass(), "options");
        optionsField.setAccessible(true);
        ImageCropWindowOptions options = (ImageCropWindowOptions) ReflectionUtils.getField(optionsField, this);

        Field imgCropField = ReflectionUtils.findField(this.getClass(), "imgCrop");
        imgCropField.setAccessible(true);

        ImgCropServerComponent imgCropServerComponent = new ImgCropServerComponent(
                options.getImageFile(),
                "",
                true,
                true,
                true,
                true,
                true,
                true,
                options.getViewPort(),
                options.getCropQuality());

        ReflectionUtils.setField(imgCropField, this, imgCropServerComponent);

        cropCmpCtn.unwrap(Layout.class).addComponent(imgCropServerComponent);

        rootCtn.getComponents().stream().filter(component -> component instanceof GroupBoxLayout).forEach(component -> component.setVisible(false));
    }
}
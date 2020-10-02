package kz.uco.tsadv.components;

import com.haulmont.cuba.web.gui.icons.IconResolverImpl;
import com.vaadin.server.Resource;

/**
 * @author adilbekov.yernar
 */

public class ImageIconResolver extends IconResolverImpl {

    @Override
    protected Resource getResource(String iconPath) {
        if (iconPath.startsWith("image-icon:")) {
            return new ImageIconResource(iconPath.split(":")[1]);
        }

        return super.getResource(iconPath);
    }
}

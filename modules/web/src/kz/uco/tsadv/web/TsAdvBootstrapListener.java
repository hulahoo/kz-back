package kz.uco.tsadv.web;

import com.vaadin.server.BootstrapPageResponse;
import kz.uco.base.web.init.BootstrapListener;
import org.jsoup.nodes.Element;

/**
 * @author adilbekov.yernar
 */
public class TsAdvBootstrapListener extends BootstrapListener {

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        super.modifyBootstrapPage(response);

        Element head = response.getDocument().getElementsByTag("head").get(0);
        includeCss("./VAADIN/themes/base/lib/sumoselect/sumoselect.min.css", response, head);
        includeCss("./VAADIN/themes/base/lib/tippy/tippy.css", response, head);
        includeCss("./VAADIN/themes/base/lib/tippy/themes/blue.css", response, head);
        includeCss("./VAADIN/themes/base/lib/tippy/themes/light.css", response, head);
        includeCss("./VAADIN/themes/base/lib/lightGallery/css/lightgallery.min.css", response, head);

        includeScript("./VAADIN/themes/base/lib/sumoselect/jquery.sumoselect.min.js", response, head);
        includeScript("./VAADIN/themes/base/lib/tippy/tippy.min.js", response, head);
        includeScript("./VAADIN/themes/base/lib/recognition.js", response, head);
        includeScript("./VAADIN/themes/base/lib/lightGallery/js/lightgallery-all.min.js", response, head);
    }
}

package kz.uco.tsadv.web.banner;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import kz.uco.tsadv.modules.recognition.Banner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class BannerEdit extends AbstractEditor<Banner> {

    @Inject
    private LookupField pageLookup;

    @Override
    protected void postInit() {
        super.postInit();

        Map<String, String> pages = new HashMap<>();
        pages.put(getMessage("page.main"), "main");
        pages.put(getMessage("page.profile"), "profile");
        pages.put(getMessage("page.feedback"), "feedback");
        pageLookup.setOptionsMap(pages);

        if (!PersistenceHelper.isNew(getItem())) {
            pageLookup.setValue(getItem().getPage());
        }
    }
}
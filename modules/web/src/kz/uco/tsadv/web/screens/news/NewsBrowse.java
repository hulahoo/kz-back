package kz.uco.tsadv.web.screens.news;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.information.News;

@UiController("tsadv_News.browse")
@UiDescriptor("news-browse.xml")
@LookupComponent("newsTable")
@LoadDataBeforeShow
public class NewsBrowse extends StandardLookup<News> {
}
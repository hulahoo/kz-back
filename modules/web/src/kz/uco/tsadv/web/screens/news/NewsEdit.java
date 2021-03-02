package kz.uco.tsadv.web.screens.news;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.information.News;

@UiController("tsadv_News.edit")
@UiDescriptor("news-edit.xml")
@EditedEntityContainer("newsDc")
@LoadDataBeforeShow
public class NewsEdit extends StandardEditor<News> {
}
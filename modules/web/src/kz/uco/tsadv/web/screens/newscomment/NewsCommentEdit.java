package kz.uco.tsadv.web.screens.newscomment;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.information.NewsComment;

@UiController("tsadv_NewsComment.edit")
@UiDescriptor("news-comment-edit.xml")
@DialogMode(forceDialog = true)
@EditedEntityContainer("newsCommentDc")
@LoadDataBeforeShow
public class NewsCommentEdit extends StandardEditor<NewsComment> {
}
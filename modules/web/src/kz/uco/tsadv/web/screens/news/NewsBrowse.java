package kz.uco.tsadv.web.screens.news;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.cuba.actions.EditActionExt;
import kz.uco.tsadv.modules.information.News;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv_News.browse")
@UiDescriptor("news-browse.xml")
@LookupComponent("newsTable")
@LoadDataBeforeShow
public class NewsBrowse extends StandardLookup<News> {
    @Named("newsTable.edit")
    private EditActionExt newsTableEdit;
    @Inject
    private GroupTable<News> newsTable;
    @Inject
    private CollectionContainer<News> newsCollectionDc;
    @Inject
    private DataManager dataManager;
    @Inject
    private CollectionLoader<News> newsCollectionDl;

    @Subscribe
    public void onInit(InitEvent event) {
        newsTableEdit.addEnabledRule(() -> {
            News news = newsTable.getSingleSelected();
            return !news.getIsPublished();
        });
    }

    @Subscribe("newsTable.edit")
    public void onNewsTableEdit(Action.ActionPerformedEvent event) {
        News news = newsTable.getSingleSelected();
        if (news != null) {
            news.setIsPublished(true);
            newsCollectionDc.replaceItem(dataManager.commit(news));
        }
    }
}
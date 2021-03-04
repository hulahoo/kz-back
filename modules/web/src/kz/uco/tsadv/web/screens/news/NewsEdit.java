package kz.uco.tsadv.web.screens.news;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FlowBoxLayout;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.information.News;
import kz.uco.tsadv.modules.information.NewsComment;
import kz.uco.tsadv.modules.information.NewsLike;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.screens.newscomment.NewsCommentEdit;

import javax.inject.Inject;

@UiController("tsadv_News.edit")
@UiDescriptor("news-edit.xml")
@EditedEntityContainer("newsDc")
@LoadDataBeforeShow
public class NewsEdit extends StandardEditor<News> {
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Notifications notifications;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private DataManager dataManager;
    @Inject
    private Messages messages;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private CollectionLoader<NewsLike> newsLikesDl;
    @Inject
    private InstanceContainer<News> newsDc;
    @Inject
    private CollectionContainer<NewsLike> newsLikesDc;
    @Inject
    private DataContext dataContext;
    @Inject
    private CollectionLoader<NewsLike> newsCommentDl;
    @Inject
    private CollectionContainer<NewsComment> newsCommentsDc;
    @Inject
    private Screens screens;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        newsLikesDl.setParameter("currentNew",getEditedEntity());
        newsLikesDl.load();
        newsCommentDl.setParameter("currentNew",getEditedEntity());
        newsCommentDl.load();
    }

    public Component generateRemoveLikeColumn(Entity entity) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaptionAsHtml(true);
        linkButton.setCaption(messageBundle.getMessage("delete"));
        linkButton.setAction(new BaseAction(entity.getId() + " - " + linkButton.getClass().getName()) {
            @Override
            public void actionPerform(Component component) {
                dataContext.remove(entity);
                newsLikesDc.getMutableItems().remove(entity);
            }
        });
        return linkButton;
    }

    public Component generateActionsColumn(Entity entity) {
        HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);
        LinkButton editLinkButton = uiComponents.create(LinkButton.class);
        editLinkButton.setCaptionAsHtml(true);
        editLinkButton.setCaption(messageBundle.getMessage("edit"));
        editLinkButton.setAction(new BaseAction(entity.getId() + " - " + editLinkButton.getClass().getName()) {
            @Override
            public void actionPerform(Component component) {
                NewsCommentEdit commentEditScreen = screens.create(NewsCommentEdit.class);
                commentEditScreen.setEntityToEdit((NewsComment) entity);
                commentEditScreen.show();
                commentEditScreen.addAfterCloseListener(afterCloseEvent -> newsCommentsDc.replaceItem(commentEditScreen.getEditedEntity()));
            }
        });
        hBoxLayout.add(editLinkButton);
        LinkButton removeLinkButton = uiComponents.create(LinkButton.class);
        removeLinkButton.setCaptionAsHtml(true);
        removeLinkButton.setCaption(messageBundle.getMessage("delete"));
        removeLinkButton.setAction(new BaseAction(entity.getId() + " - " + removeLinkButton.getClass().getName()) {
            @Override
            public void actionPerform(Component component) {
                dataContext.remove(entity);
                newsCommentsDc.getMutableItems().remove(entity);
            }
        });
        hBoxLayout.add(removeLinkButton);
        return hBoxLayout;
    }
}

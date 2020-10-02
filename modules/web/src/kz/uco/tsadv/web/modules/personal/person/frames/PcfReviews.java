package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PersonReview;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class PcfReviews extends AbstractWindow {
    private PersonGroupExt authorPersonGroup = null;
    private PersonReview editedReview = null;
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    @Inject
    private UserSession userSession;
    @Inject
    private Button addDislike;
    @Inject
    private Button addLike;
    @Inject
    private Button save;
    @Inject
    private RichTextArea richText;
    @Inject
    private CollectionDatasource<PersonReview, UUID> personReviewsDs;
    @Inject
    private Metadata metadata;
    @Inject
    private Datasource<PersonGroupExt> personGroupDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private GroupBoxLayout reviewForm;
    @Inject
    private Label authorImage;
    private boolean readOnly;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey("readOnly")) {
            readOnly = true;
        }
        super.init(params);
        authorPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
        initReviewCreateForm();
    }

    private void initReviewCreateForm() {
        if (authorPersonGroup != null) {
            reviewForm.setVisible(!readOnly);
            PersonExt person = authorPersonGroup.getPerson();
            String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
            /*authorImage.setValue(String.format("<img src=\"/tal/image_api?userId=%s\" class=\"pr-author-img\"/>", person.getId()));*/
            authorImage.setValue(String.format("<img src=\"" + webAppUrl + "/dispatch/person_image/%s\" class=\"pr-author-img\"/>", person.getId()));

        }
    }

    public Component generateGeneratedCell(Entity entity) {
        PersonReview personReview = (PersonReview) entity;
        ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        if (authorPersonGroup != null) {
            hBoxLayout.setSpacing(true);

            PersonGroupExt localAuthorPersonGroup = personReview.getAuthor();

            Label authorImage = componentsFactory.createComponent(Label.class);
            authorImage.setHtmlEnabled(true);
            String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
            /*authorImage.setValue(String.format("<img src=\"/tal/image_api?userId=%s\" class=\"pr-author-img\"/>", personReview.getAuthor().getPerson().getId()));*/
            authorImage.setValue(String.format("<img src=\"" + webAppUrl + "/dispatch/person_image/%s\" class=\"pr-author-img\"/>", personReview.getAuthor().getPerson().getId()));
            hBoxLayout.add(authorImage);

            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);

            HBoxLayout userInfo = componentsFactory.createComponent(HBoxLayout.class);
            userInfo.setSpacing(true);
            Label label = componentsFactory.createComponent(Label.class);
            label.setIcon("1".equals(personReview.getLiking()) ? "font-icon:THUMBS_UP" : "font-icon:THUMBS_DOWN");
            label.setStyleName("1".equals(personReview.getLiking()) ? "font-color-green" : "font-color-red");
            userInfo.add(label);
            userInfo.add(createLabel(localAuthorPersonGroup.getPersonFioWithEmployeeNumber(), "pr-author-name"));
            userInfo.add(createLabel(dateTimeFormat.format(personReview.getDateTime()), "pr-date-time"));

            if (localAuthorPersonGroup.getId().equals(authorPersonGroup.getId()) && !readOnly) {
                HBoxLayout actions = componentsFactory.createComponent(HBoxLayout.class);
                LinkButton editLink = componentsFactory.createComponent(LinkButton.class);
                editLink.setAlignment(Alignment.MIDDLE_CENTER);
                editLink.setCaption("");
                editLink.setIcon("icons/edit.png");
                editLink.setAction(new BaseAction("edit-review") {
                    @Override
                    public void actionPerform(Component component) {
                        setVisibleComponent(false);
                        editedReview = personReview;
                        richText.setValue(personReview.getText());
                    }
                });

                LinkButton deleteLink = componentsFactory.createComponent(LinkButton.class);
                deleteLink.setAlignment(Alignment.TOP_CENTER);
                deleteLink.setCaption("");
                deleteLink.setIcon("font-icon:REMOVE");
                deleteLink.setAction(new BaseAction("delete-review") {
                    @Override
                    public void actionPerform(Component component) {
                        showOptionDialog(getMessage("PersonReview.delete.confirm.title"),
                                String.format(getMessage("PersonReview.delete.confirm"), authorPersonGroup.getPerson().getFullName()),
                                MessageType.CONFIRMATION_HTML,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                            public void actionPerform(Component component) {
                                                deleteReview(personReview);
                                            }
                                        },
                                        new DialogAction(DialogAction.Type.NO, Status.NORMAL)
                                });
                    }
                });

                actions.add(editLink);
                actions.add(deleteLink);
                userInfo.add(actions);
            }

            vBoxLayout.add(userInfo);
            Label body = createLabel(personReview.getText(), "pr-body");
            body.setHtmlEnabled(true);
            body.setAlignment(Alignment.MIDDLE_LEFT);
            vBoxLayout.add(body);
            hBoxLayout.add(vBoxLayout);
        }

        return hBoxLayout;
    }

    private Label createLabel(String value, String cssClass) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setAlignment(Alignment.MIDDLE_CENTER);
        label.setStyleName(cssClass);
        label.setValue(value);
        return label;
    }

    private void setVisibleComponent(boolean isCreate) {
        addDislike.setVisible(isCreate);
        addLike.setVisible(isCreate);
        save.setVisible(!isCreate);
        if (isCreate) {
            editedReview = null;
        }
    }

    public void saveReview() {
        if (validateAll()) {

            editedReview.setText(richText.getValue());
            personReviewsDs.modifyItem(editedReview);
            personReviewsDs.commit();
            personReviewsDs.refresh();
            reset();
        }
    }

    public void addLikeReview() {
        addReview("1");
    }

    public void addDislikeReview() {
        addReview("-1");
    }

    public void addReview(String likeOrDislike) {
        if (authorPersonGroup != null && validateAll()) {
            PersonReview personReview = metadata.create(PersonReview.class);
            personReview.setAuthor(authorPersonGroup);
            personReview.setText(richText.getValue());
            personReview.setDateTime(new Date());
            personReview.setLiking(likeOrDislike);
            personReview.setPerson(personGroupDs.getItem());
            personReviewsDs.addItem(personReview);
            personReviewsDs.commit();
            personReviewsDs.refresh();
            reset();
        }
    }

    private void deleteReview(PersonReview personReview) {
        personReviewsDs.removeItem(personReview);
        personReviewsDs.commit();
        personReviewsDs.refresh();
        reset();
    }

    public void reset() {
        richText.setValue("");
        setVisibleComponent(true);
    }
}
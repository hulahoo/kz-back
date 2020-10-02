package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebProgressBar;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PersonReview;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class PersonReviews extends AbstractWindow {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Inject
    private CollectionDatasource<PersonReview, UUID> likeReviewDs;

    @Inject
    private CollectionDatasource<PersonReview, UUID> dislikeReviewDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private HtmlBoxLayout personCardLeftMenu;

    @Inject
    private RichTextArea richText;

    @Inject
    private Label authorImage;

    @Inject
    private Metadata metadata;

    @Inject
    private TabSheet tabSheet;

    @Inject
    private UserSession userSession;
    @Inject
    private GroupBoxLayout reviewForm;

    @WindowParam
    private AssignmentExt assignment;

    @Inject
    private Button add;

    @Inject
    private Button save;

    private int filledRowCount;
    private PersonGroupExt authorPersonGroup = null;
    private PersonReview editedReview = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setVisibleComponent(true);

        PersonExt person = assignment.getPersonGroup().getPerson();

        setCaption(getMessage("PersonReview.page.caption") + " " + person.getFullName());
        authorPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        String query = "select e from tsadv$PersonReview e " +
                "where e.liking = '%s' and e.person.id = '%s'" +
                "order by e.dateTime desc";

        likeReviewDs.setQuery(String.format(query, "1", assignment.getPersonGroup().getId()));
        dislikeReviewDs.setQuery(String.format(query, "-1", assignment.getPersonGroup().getId()));

        initPersonLeftMenu(person);
        initReviewCreateForm();
    }

    private void setVisibleComponent(boolean isCreate) {
        add.setVisible(isCreate);
        save.setVisible(!isCreate);
        if (isCreate) {
            editedReview = null;
        }
    }

    public void saveReview() {
        if (validateAll()) {
            boolean isLike = tabSheet.getTab().getName().equals("likeTab");
            CollectionDatasource<PersonReview, UUID> reviewDs = isLike ? likeReviewDs : dislikeReviewDs;

            editedReview.setText(richText.getValue());
            reviewDs.modifyItem(editedReview);
            reviewDs.commit();
            reviewDs.refresh();
            reset();
        }
    }

    public void addReview() {
        if (authorPersonGroup != null && validateAll()) {
            boolean isLike = tabSheet.getTab().getName().equals("likeTab");
            CollectionDatasource<PersonReview, UUID> reviewDs = isLike ? likeReviewDs : dislikeReviewDs;

            PersonReview personReview = metadata.create(PersonReview.class);
            personReview.setAuthor(authorPersonGroup);
            personReview.setText(richText.getValue());
            personReview.setDateTime(new Date());
            personReview.setLiking(isLike ? "1" : "-1");
            personReview.setPerson(assignment.getPersonGroup());
            reviewDs.addItem(personReview);
            reviewDs.commit();
            reviewDs.refresh();
            reset();
        }
    }

    private void deleteReview(PersonReview personReview) {
        boolean isLike = tabSheet.getTab().getName().equals("likeTab");
        CollectionDatasource<PersonReview, UUID> reviewDs = isLike ? likeReviewDs : dislikeReviewDs;
        reviewDs.removeItem(personReview);
        reviewDs.commit();
        reviewDs.refresh();
        reset();
    }

    public void reset() {
        richText.setValue("");
        setVisibleComponent(true);
    }

    private void initReviewCreateForm() {
        if (authorPersonGroup != null) {
            String webAppUrl = AppContext.getProperty("cuba.webAppUrl");

            reviewForm.setVisible(true);
            PersonExt person = authorPersonGroup.getPerson();
            /*authorImage.setValue(String.format("<img src=\"/tal/image_api?userId=%s\" class=\"pr-author-img\"/>", person.getId()));*/
            authorImage.setValue(String.format("<img src=\"" + webAppUrl + "/dispatch/person_image/%s\" class=\"pr-author-img\"/>", person.getId()));
        }
    }

    public Component generateRow(PersonReview personReview) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        if (authorPersonGroup != null) {
            hBoxLayout.setSpacing(true);

            PersonGroupExt authorPersonGroup = personReview.getAuthor();

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
            userInfo.add(createLabel(authorPersonGroup.getPersonFioWithEmployeeNumber(), "pr-author-name"));
            userInfo.add(createLabel(dateTimeFormat.format(personReview.getDateTime()), "pr-date-time"));

            if (authorPersonGroup.getId().equals(this.authorPersonGroup.getId())) {
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

    private void initPersonLeftMenu(PersonExt person) {
        personCardLeftMenu.add(createLabelWithId("fullName", person.getFullName()));
        personCardLeftMenu.add(createLabelWithId("employeeNumber", person.getEmployeeNumber()));

        filledRowCount = 0;

        StringBuilder userInfoSb = new StringBuilder("<table>");
        userInfoSb.append(addUserInfoTr(getMessage("Person.hireDate"), person.getHireDate() == null ? "" : dateFormat.format(person.getHireDate())));
        userInfoSb.append(addUserInfoTr(getMessage("Person.nationalIdentifier"), String.valueOf(person.getNationalIdentifier())));
        userInfoSb.append(addUserInfoTr(getMessage("Person.dateOfBirth"), dateFormat.format(person.getDateOfBirth())));
        userInfoSb.append(addUserInfoTr(getMessage("Person.sex"), person.getSex().getLangValue()));

        if (person.getMaritalStatus() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.maritalStatus"), person.getMaritalStatus().getLangValue()));
        if (person.getType() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.type"), person.getType().getLangValue()));
        if (person.getNationality() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.nationality"), person.getNationality().getLangValue()));
        if (person.getCitizenship() != null)
            userInfoSb.append(addUserInfoTr(getMessage("Person.citizenship"), person.getCitizenship().getLangValue()));

        userInfoSb.append("</table>");

        Label userInfo = createLabelWithId("userInfo", userInfoSb.toString());
        userInfo.setHtmlEnabled(true);
        personCardLeftMenu.add(userInfo);

        Embedded personImage = Utils.getPersonImageEmbedded(person, "70px", null);
        personImage.setId("personImage");
        personCardLeftMenu.add(personImage);

        double fieldPercent = 100 / 8 * filledRowCount;

        WebProgressBar progressBar = componentsFactory.createComponent(WebProgressBar.class);
        progressBar.setId("progressBar");
        progressBar.setWidth("100%");
        progressBar.setValue(fieldPercent / 100);
        personCardLeftMenu.add(progressBar);

        personCardLeftMenu.add(createH4("progressBarLabel", String.format("%s %d%s", getMessage("person.card.progress.label"), (int) fieldPercent, "%"), true));
        personCardLeftMenu.add(createH4("userInfoLabel", getMessage("person.card.main.info"), false));
    }

    private Label createLabelWithId(String id, String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setId(id);
        label.setValue(value);
        return label;
    }

    private Label createLabel(String value, String cssClass) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setAlignment(Alignment.MIDDLE_CENTER);
        label.setStyleName(cssClass);
        label.setValue(value);
        return label;
    }

    private Label createH4(String id, String message, boolean isBordered) {
        Label redirectLabel = createLabelWithId(id,
                String.format("<h4 class=\"%s\">%s</h4>", isBordered ? " border-top" : "", message));
        redirectLabel.setHtmlEnabled(true);
        redirectLabel.setWidth("100%");
        return redirectLabel;
    }

    private LinkButton createLinkButton(String caption, BaseAction baseAction) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(caption);
        linkButton.setAction(baseAction);
        return linkButton;
    }

    private String addUserInfoTr(String columnName, String columnValue) {
        filledRowCount++;
        return String.format("<tr><td>%s</td><td>:</td><td>%s</td></tr>", columnName, columnValue);
    }

}
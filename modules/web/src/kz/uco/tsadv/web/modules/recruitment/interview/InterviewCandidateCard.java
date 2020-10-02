package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.PersonAttachment;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class InterviewCandidateCard extends AbstractHrEditor<PersonExt> {

    @Inject
    private Embedded userImage;
    @Inject
    private VBoxLayout studentGrants;
    @Inject
    private Label cardMenu;
    @Inject
    private ScrollBoxLayout scrollBox;
    @Inject
    private FieldGroup fieldGroup1;
    @Inject
    private FieldGroup fieldGroup2;
    @Inject
    private FieldGroup fieldGroup3;
    @Inject
    private FieldGroup fieldGroup4;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private Frame windowActions;
    @Inject
    private VBoxLayout reviewsVbox;

    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        Utils.getPersonImageEmbedded(getItem(), "60px", userImage);

        boolean isStudent = getItem().getType().getCode().equalsIgnoreCase("student");

        initVisibleBlock(isStudent);

        generateLinks(isStudent);

        initFieldGroup(fieldGroup1, fieldGroup2, fieldGroup3, fieldGroup4);

        Map<String, Object> map = new HashMap<>();
        map.put("readOnly", null);
        Frame reviewsFrame = openFrame(null, "pcf-reviews", map);
        reviewsVbox.add(reviewsFrame);
        reviewsVbox.expand(reviewsFrame);

        windowActions.setVisible(false);
    }

    private void initFieldGroup(FieldGroup... fieldGroups) {
        for (FieldGroup fieldGroup : fieldGroups) {
            fieldGroup.setFieldCaptionWidth(150);
            fieldGroup.getFields().forEach(fieldConfig -> fieldConfig.setEditable(false));
        }
    }

    public Component generateInterview(JobRequest entity) {
        Interview interview = entity.getInterview();

        if (interview != null) {
            Label label = componentsFactory.createComponent(Label.class);
            label.setDescription(
                    interview.getRequisitionHiringStep().getHiringStep().getStepName() +
                            " (" + messages.getMessage(interview.getInterviewStatus()) + ")");
            return label;
        }
        return null;
    }

    public Component generatePassedInterviews(JobRequest entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getPassedInterviews() != null && entity.getTotalInterviews() != null)
            label.setValue(String.format(messages.getMainMessage("JobRequest.interviews"),
                    entity.getPassedInterviews(),
                    entity.getTotalInterviews()));

        return label;
    }

    private void generateLinks(boolean isStudent) {
        StringBuilder sb = new StringBuilder("<ul class=\"b-list-group\">");
        sb.append(createLi("user-secret", "mainData", 1));
        sb.append(createLi("address-card", "candidateIdentification", 2));
        if (isStudent) sb.append(createLi("graduation-cap", "Student.grant.browseCaption", 3));
        sb.append(createLi("book", "PersonGroup.education", 4));
        sb.append(createLi("briefcase", "PersonGroup.experience", 5));
        sb.append(createLi("paperclip", "PersonGroup.attachments", 6));
        sb.append(createLi("address-book-o", "PersonCard.adressess", 7));
        sb.append(createLi("phone-square", "Contacts", 8));
        sb.append(createLi("file-text-o", "Documents", 9));
        sb.append(createLi("indent", "Competences", 10));
        sb.append(createLi("newspaper-o", "interview.candidate.card.jr", 11));
        sb.append(createLi("thumbs-up", "reviewsTable", 12));
        sb.append("</ul>");
        cardMenu.setValue(sb.toString());
    }

    private String createLi(String icon, String messageKey, int section) {
        String liTemplate = "<li class=\"b-list-group-item\">" +
                "<a class=\"lg-scroll-link\" data-scroll=\"section-" + section + "\"><i class=\"fa fa-" + icon + "\"></i>" + getMessage(messageKey) + "<span class=\"fa fa-chevron-right\"></span></a></li>";
        return liTemplate;
    }

    private void initVisibleBlock(boolean isStudent) {
        studentGrants.setVisible(isStudent);
    }

    public Component getPersonAttachmentDownloadBtn(PersonAttachment entity) {
        return Utils.getFileDownload(entity.getAttachment(), InterviewCandidateCard.this);
    }
}
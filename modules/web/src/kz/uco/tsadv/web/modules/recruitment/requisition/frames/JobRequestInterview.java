package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.FormatStrings;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestion;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestionnaire;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author veronika.buksha
 */
public class JobRequestInterview extends AbstractFrame {

    private static final String percentFormatPattern = "###.##'%'";
    protected DecimalFormat percentFormat;

    @Named("interviewTable.edit")
    protected EditAction interviewTableEdit;
    @Inject
    private DataManager dataManager;
    @Inject
    protected Table<Interview> interviewTable;
    @Inject
    protected UserSessionSource userSessionSource;

    public CollectionDatasource<Interview, UUID> interviewsDs;
    protected ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        /*interviewTableEdit.setAfterCommitHandler(new EditAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                interviewsDs.modifyItem((Interview) entity);
            }
        });*/

        FormatStrings formatStrings = Datatypes.getFormatStrings(userSessionSource.getLocale());
        percentFormat = new DecimalFormat(percentFormatPattern, formatStrings.getFormatSymbols());

        interviewsDs = (CollectionDatasource<Interview, UUID>) getDsContext().get("interviewsDs");

        interviewsDs.addCollectionChangeListener(e -> {
            interviewTable.getColumn("cause").setCollapsed(true);
            interviewTable.getColumn("interviewStatus").setWidth(130);
            interviewsDs.getItems().forEach(x -> {
                if (x.getInterviewStatus().equals(InterviewStatus.FAILED)) {
                    interviewTable.getColumn("cause").setCollapsed(false);
                    interviewTable.getColumn("cause").setWidth(280);
                    interviewTable.getColumn("interviewStatus").setWidth(175);
                }
            });
        });
    }

    public Component generateScore(Interview interview) {
        Map<String, Object> map = new HashMap<>();
        map.put("intId", interview.getId());

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        Double passingScore = null;
        InterviewQuestionnaire interviewQuestionnaire = commonService.emQueryFirstResult(InterviewQuestionnaire.class,
                "select e from tsadv$InterviewQuestionnaire e " +
                        "join e.interview i " +
                        "join e.questionnaire rq " +
                        "join rq.category c " +
                        "where c.code = 'PRE_SCREEN_TEST' and i.id = :intId " +
                        "order by e.createTs desc",
                map,
                "interviewQuestionnaire.weight");

        if (interviewQuestionnaire != null) {

            RcQuestionnaire rcQuestionnaire = interviewQuestionnaire.getQuestionnaire();
            if (rcQuestionnaire != null) {
                passingScore = rcQuestionnaire.getPassingScore();
            }

            calculateTotalScore(interviewQuestionnaire);

            Double totalScore = interviewQuestionnaire.getTotalScore();

            linkButton.setCaption(String.format("%s / %s", totalScore, interviewQuestionnaire.getTotalMaxScore()));
            linkButton.setAction(new BaseAction("open-pre-screen") {
                @Override
                public void actionPerform(Component component) {
                    openWindow("interview-pre-screen",
                            WindowManager.OpenType.DIALOG,
                            ParamsMap.of(StaticVariable.INTERVIEW_ID, interview.getId()));
                }
            });

            if (passingScore != null) {
                String cssClass = totalScore >= passingScore ? "pre-screen-passed" : "pre-screen-fail";
                linkButton.setStyleName(cssClass);
            }
        }
        return linkButton;
    }

    private void calculateTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;
        Double score = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }

        interviewQuestionnaire.setTotalScore(score);
        interviewQuestionnaire.setTotalMaxScore(maxScore);
    }

    public Component generateInterviewLink(Interview interview) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(interview.getRequisitionHiringStep().getHiringStep().getStepName());
        linkButton.setAction(new BaseAction("linkToInterview") {
            @Override
            public void actionPerform(Component component) {
                interviewTable.setSelected(interview);
                interviewTable.getActionNN(EditAction.ACTION_ID).actionPerform(component);
            }
        });
        return linkButton;
    }

    public Component generateQuestionnairesLink(Interview interview) {
        if (interview.getQuestionnaireMatchPercent() != null) {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

            linkButton.setCaption(percentFormat.format(new BigDecimal(interview.getQuestionnaireMatchPercent())));
            linkButton.setAction(new BaseAction("linkToQuestionnaires") {
                @Override
                public void actionPerform(Component component) {
                    AbstractEditor<Interview> interviewEditor = openEditor("tsadv$Interview.edit", interview, WindowManager.OpenType.THIS_TAB, Collections.singletonMap("selectTab", "questionnaires"));
                    interviewEditor.addCloseWithCommitListener(() -> interviewsDs.refresh());
                }
            });
            return linkButton;
        } else return null;
    }

    public Component generateCause(Interview interview) {
        if (interview.getInterviewStatus().equals(InterviewStatus.FAILED)) {
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(getMessage("didNotPass") + " " + interview.getRequisitionHiringStep().getHiringStep().getStepName().toLowerCase());
            return label;
        }
        return null;
    }

}

package kz.uco.tsadv.web.talentprogrampersonstep;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.SendingMessage;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.core.notification.NotificationTemplate;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.SmsService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.DicTalentProgramRequestStatus;
import kz.uco.tsadv.entity.TalentProgramPersonStep;
import kz.uco.tsadv.entity.TalentProgramRequest;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

public class TalentProgramPersonStepInvite extends AbstractEditor<TalentProgramPersonStep> {
    @Inject
    protected TimeField<Date> timeFrom, timeTo;
    @Inject
    protected DateField<Date> dateFieldFrom, dateFieldTo;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected RichTextArea textAreaTemplateEn, textAreaTemplateKz, textAreaTemplateRu;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    private Datasource<NotificationTemplate> notificationTemplateDs;
    @Inject
    private UserSessionSource userSessionSource;

    protected SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    protected Set<TalentProgramRequest> inviteList;
    protected String fromAddress;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        inviteList = (Set<TalentProgramRequest>) params.getOrDefault("inviteList", new ArrayList<>());
        fromAddress = AppBeans.get(SmsService.class).getFromAddress();

        ((HasValue<Entity>) fieldGroup.getFieldNN("talentProgramStepField").getComponentNN()).addValueChangeListener(e -> {
            String code = e.getValue() != null ? ((DicTalentProgramStep) e.getValue()).getCode() : "default";
            NotificationTemplate notificationTemplate = commonService.getEntity(NotificationTemplate.class, "invite.candidate.template." + code.toLowerCase());
            if (notificationTemplate == null) {
                notificationTemplate = metadata.create(NotificationTemplate.class);
            }
            notificationTemplateDs.setItem(notificationTemplate);
        });
    }

    @Override
    protected void initNewItem(TalentProgramPersonStep item) {
        super.initNewItem(item);
        item.setStatus(commonService.getEntity(DicTalentProgramRequestStatus.class, "INVITATION_SENT"));
    }

    @Override
    public void ready() {
        super.ready();

        dateFieldFrom.setValue(BaseCommonUtils.truncDate(Optional.ofNullable(getItem().getDateFrom()).orElse(CommonUtils.getSystemDate())));
        dateFieldTo.setValue(BaseCommonUtils.truncDate(Optional.ofNullable(getItem().getDateTo()).orElse(CommonUtils.getSystemDate())));

        timeFrom.addValueChangeListener(e -> {
            if (timeFrom.getValue() != null) {
                timeTo.setValue(DateUtils.addHours(timeFrom.getValue(), 1));
            }
            dateChangeListener(dateFieldFrom.getValue(), (Date) e.getValue(), "dateFrom");
        });
        timeTo.addValueChangeListener(e -> dateChangeListener(dateFieldTo.getValue(), (Date) e.getValue(), "dateTo"));
        dateFieldTo.addValueChangeListener(e -> dateChangeListener((Date) e.getValue(), timeTo.getValue(), "dateTo"));
        dateFieldFrom.addValueChangeListener(e -> dateChangeListener((Date) e.getValue(), timeFrom.getValue(), "dateFrom"));
    }

    protected void dateChangeListener(Date date, Date time, String property) {
        getItem().setValue(property, getDate(date, time));
    }

    protected Date getDate(Date date, Date time) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar cTime = null;
        if (time != null) {
            cTime = Calendar.getInstance();
            cTime.setTime(time);
        }
        cal.set(Calendar.HOUR_OF_DAY, cTime != null ? cTime.get(Calendar.HOUR_OF_DAY) : 0);
        cal.set(Calendar.MINUTE, cTime != null ? cTime.get(Calendar.MINUTE) : 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void onInvite() {
        if (!validateAll()) return;
        showOptionDialog(getMessage("Attention"),
                getMessage("invite.candidates"),
                MessageType.WARNING,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                            @Override
                            public void actionPerform(Component component) {
                                inviteCandidates();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL) {
                            public void actionPerform(Component component) {
                            }
                        }
                });
    }

    protected void inviteCandidates() {

        Map<String, Object> param = new HashMap<>(ParamsMap.of("dateFrom", getItem().getDateFrom() != null ? format.format(getItem().getDateFrom()) : "",
                "dateTo", getItem().getDateTo() != null ? format.format(getItem().getDateTo()) : "",
                "addressRu", Optional.ofNullable(getItem().getAddressRu()).orElse(""),
                "addressEn", Optional.ofNullable(getItem().getAddressEn()).orElse(""),
                "stepRu", getItem().getDicTalentProgramStep().getLangValue1(),
                "stepEn", getItem().getDicTalentProgramStep().getLangValue3()));

        CommitContext context = new CommitContext();
        inviteList.forEach(talentProgramRequest -> inviteCandidate(talentProgramRequest, context, param));
        dataManager.commit(context);
//        entities.forEach(entity -> notifyCandidates(entity, param));
        close(COMMIT_ACTION_ID, true);
    }

/*    protected void notifyCandidates(Entity entity, Map<String, Object> param) {
        if (entity instanceof TalentProgramPersonStep) {
            PersonGroupExt candidate = ((TalentProgramPersonStep) entity).getPersonGroup();
            param.put("candidateRu", candidate != null ? candidate.getPersonLatinFioWithEmployeeNumber("ru") : "");
            param.put("candidateEn", candidate != null ? candidate.getPersonLatinFioWithEmployeeNumber("en") : "");
            TalentProgramPersonStep talentProgramRequest = ((TalentProgramPersonStep) entity);
            UserExt userExt = employeeService.getUserExtByPersonGroupId(talentProgramRequest.getPersonGroup().getId(), View.LOCAL);
            if (userExt != null && userExt.getEmail() != null && userExt.getLanguage() != null) {
                notificationService.sendNotification(userExt.getEmail(),
                        notificationTemplateDs.getItem().getValue("emailCaptionLang" + getIndexLang(userExt.getLanguage())),
                        getTextAreaTemplate(userExt.getLanguage()).getValue(),
                        param);
            }
        }
    }*/

    private String getIndexLang(String language) {
        switch (language) {
            case "kz":
                return "2";
            case "en":
                return "3";
            default:
                return "1";
        }
    }

    protected void inviteCandidate(TalentProgramRequest talentProgramRequest, CommitContext context, Map<String, Object> param) {
        PersonGroupExt candidate = talentProgramRequest.getPersonGroup();

        param.put("candidateRu", Optional.ofNullable(candidate.getPersonLatinFioWithEmployeeNumber("ru")).orElse(""));
        param.put("candidateEn", Optional.ofNullable(candidate.getPersonLatinFioWithEmployeeNumber("en")).orElse(""));

        UserExt userExt = employeeService.getUserExtByPersonGroupId(candidate.getId(), View.LOCAL);

        TalentProgramPersonStep personStep = metadata.getTools().deepCopy(getItem());
        personStep.setId(UUID.randomUUID());
        personStep.setTalentProgramRequest(talentProgramRequest);
        personStep.setPersonGroup(candidate);
        if (alreadyExistProgramStep(personStep)) {
            throw new ItemNotFoundException(String.format(getMessage("already.has.step"), candidate.getFullName()));
        }

        talentProgramRequest.setCurrentStepStatus(personStep.getStatus());
        talentProgramRequest.setCurrentStep(personStep.getDicTalentProgramStep());

        if (userExt == null || StringUtils.isBlank(userExt.getEmail())) {
            throw new ItemNotFoundException(formatMessage("no.email", candidate.getPersonLatinFioWithEmployeeNumber(userSessionSource.getLocale().getLanguage())));
        }

        String language = Optional.ofNullable(userExt.getLanguage()).orElse("ru");

        String header = notificationTemplateDs.getItem().getValue("emailCaptionLang" + getIndexLang(language));
        String content = getTextAreaTemplate(language).getValue();

        SendingMessage sendingMessage = metadata.create(SendingMessage.class);
        sendingMessage.setCaption("Приглашение на интервью");
        sendingMessage.setStatus(SendingStatus.QUEUE);
        sendingMessage.setHeaders(TemplateHelper.processTemplate(header != null ? header : "", param));
        sendingMessage.setContentText("<html>" + TemplateHelper.processTemplate(content != null ? content : "", param) + "</html>");
        sendingMessage.setFrom(fromAddress);
        sendingMessage.setAttemptsMade(1);
        sendingMessage.setAddress(userExt.getEmail());

        context.addInstanceToCommit(sendingMessage);
        context.addInstanceToCommit(personStep);
        context.addInstanceToCommit(talentProgramRequest);
    }

    protected boolean alreadyExistProgramStep(TalentProgramPersonStep item) {
        return !commonService.getEntities(TalentProgramPersonStep.class,
                "select e from tsadv$TalentProgramPersonStep e " +
                        "   where e.dicTalentProgramStep.id = :dicTalentProgramStep " +
                        "   and e.talentProgramRequest.id = :talentProgramRequest ",
                ParamsMap.of("dicTalentProgramStep", item.getDicTalentProgramStep().getId(),
                        "talentProgramRequest", item.getTalentProgramRequest().getId()),
                View.MINIMAL).isEmpty();
    }

    public void cancel() {
        close(CLOSE_ACTION_ID, true);
    }

    protected RichTextArea getTextAreaTemplate(String lang) {
        switch (lang) {
            case "ru":
                return textAreaTemplateRu;
            case "kz":
                return textAreaTemplateKz;
            case "en":
                return textAreaTemplateEn;
            default:
                throw new IllegalArgumentException();
        }
    }
}
package kz.uco.tsadv.web.modules.selfservice.Requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.service.RecruitmentService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class RequisitionSelfNewEdit<T extends Requisition> extends AbstractEditor<T> {

    @Inject
    protected RecruitmentConfig recruitmentConfig;
    @Inject
    protected RecruitmentService recruitmentService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UniqueNumbersService uniqueNumbersService;
    protected PersonGroupExt currentPersonGroup;
    @Inject
    protected UserSession userSession;
    @Named("fieldGroup.positionGroup")
    protected PickerField positionGroupField;
    @Named("fieldGroup.jobGroup")
    protected PickerField jobGroupField;
    @Inject
    protected DataSupplier dataSupplier;

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setRequisitionType(RequisitionType.STANDARD);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setCode(String.format("R%07d", uniqueNumbersService.getNextNumber("requisitionCode")));
        Long count = commonService.getCount(Requisition.class,
                "select e from tsadv$Requisition e " +
                        " where e.code = :code",
                ParamsMap.of("code", item.getCode()));
        if (count > 0) {
            item.setCode(String.format("R%07d", uniqueNumbersService.getNextNumber("requisitionCode")));
        }
        currentPersonGroup = commonService.getEntity(PersonGroupExt.class,
                "select e.personGroupId from tsadv$UserExt e where e.id = :userId",
                ParamsMap.of("userId", userSession.getUser().getId()), "personGroupExt.for.requisition.edit");
        item.setManagerPersonGroup(currentPersonGroup);
        item.setOpenedPositionsCount(1.0);
        item.setViewCount(0L);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        positionGroupField.addValueChangeListener(e -> {
            positionGroupFieldValueChangeListener((HasValue.ValueChangeEvent) e);
        });
        jobGroupField.addValueChangeListener(e -> {
            jobGroupFieldValueChangeListener((HasValue.ValueChangeEvent) e);
        });
    }

    protected void positionGroupFieldValueChangeListener(HasValue.ValueChangeEvent e) {
        changeDescription();
    }

    protected void jobGroupFieldValueChangeListener(HasValue.ValueChangeEvent e) {
        changeDescription();
    }

    protected void changeDescription() {
        String descriptionru = "";
        String descriptionkz = "";
        String descriptionen = "";
        String nameForSiteru = "";
        String nameForSitekz = "";
        String nameForSiteen = "";
        boolean updateDescription = false;
        boolean updateNameForSite = false;

        PositionGroupExt positionGroupExt = getItem().getPositionGroup();
        if (positionGroupExt != null) {
            positionGroupExt = dataSupplier.reload(positionGroupExt, "positionGroupExt.for.requisition");
        }
        if (positionGroupExt != null) {
            if (recruitmentConfig.getPositionDescription()) {
                descriptionru = concat(positionGroupExt.getPosition().getCandidateRequirementsLang1(),
                        positionGroupExt.getPosition().getJobDescriptionLang1());
                descriptionkz = concat(positionGroupExt.getPosition().getCandidateRequirementsLang2(),
                        positionGroupExt.getPosition().getJobDescriptionLang2());
                descriptionen = concat(positionGroupExt.getPosition().getCandidateRequirementsLang3(),
                        positionGroupExt.getPosition().getJobDescriptionLang3());
            }
            if (getItem().getPositionGroup() != null && getItem().getPositionGroup().getPosition() != null) {
                nameForSiteru = getItem().getPositionGroup().getPosition().getPositionNameLang1();
                nameForSitekz = getItem().getPositionGroup().getPosition().getPositionNameLang2();
                nameForSiteen = getItem().getPositionGroup().getPosition().getPositionNameLang3();
            } else {
                nameForSiteru = "";
                nameForSitekz = "";
                nameForSiteen = "";
            }
        }
        JobGroup jobGroup = getItem().getJobGroup();
        if (jobGroup != null) {
            jobGroup = dataSupplier.reload(jobGroup, "jobGroup.for.requisitions");
        }
        if (jobGroup != null) {
            if (descriptionru == null || descriptionru.length() < 1) {
                descriptionru = concat(jobGroup.getJob().getCandidateRequirementsLang1(),
                        jobGroup.getJob().getJobDescriptionLang1());
            }
            if (descriptionkz == null || descriptionkz.length() < 1) {
                descriptionkz = concat(jobGroup.getJob().getCandidateRequirementsLang2(),
                        jobGroup.getJob().getJobDescriptionLang2());
            }
            if (descriptionen == null || descriptionen.length() < 1) {
                descriptionen = concat(jobGroup.getJob().getCandidateRequirementsLang3(),
                        jobGroup.getJob().getJobDescriptionLang3());
            }
            if (jobGroup.getJob() != null) {
                if (nameForSiteru == null || nameForSiteru.length() < 1) {
                    nameForSiteru = jobGroup.getJob().getJobNameLang1();
                }
                if (nameForSitekz == null || nameForSitekz.length() < 1) {
                    nameForSitekz = jobGroup.getJob().getJobNameLang2();
                }
                if (nameForSiteen == null || nameForSiteen.length() < 1) {
                    nameForSiteen = jobGroup.getJob().getJobNameLang3();
                }
            }
        }
        if ((descriptionru != null && !descriptionru.isEmpty() && !descriptionru.equals(getItem().getDescriptionLang1()))
                || (descriptionkz != null && !descriptionkz.isEmpty() && !descriptionkz.equals(getItem().getDescriptionLang2()))
                || (descriptionen != null && !descriptionen.isEmpty() && !descriptionen.equals(getItem().getDescriptionLang3()))) {
            updateDescription = true;
        }
        if ((nameForSiteru != null && !nameForSiteru.isEmpty() && !nameForSiteru.equals(getItem().getNameForSiteLang1()))
                || (nameForSitekz != null && !nameForSitekz.isEmpty() && !nameForSitekz.equals(getItem().getNameForSiteLang2()))
                || (nameForSiteen != null && !nameForSiteen.isEmpty() && !nameForSiteen.equals(getItem().getNameForSiteLang3()))) {
            updateNameForSite = true;
        }
        Map<String, String> paramToQuestion = new HashMap<>();
        if (updateDescription) {
            paramToQuestion.put("descriptionru", descriptionru);
            paramToQuestion.put("descriptionkz", descriptionkz);
            paramToQuestion.put("descriptionen", descriptionen);
        }
        if (updateNameForSite) {
            paramToQuestion.put("nameForSiteru", nameForSiteru);
            paramToQuestion.put("nameForSitekz", nameForSitekz);
            paramToQuestion.put("nameForSiteen", nameForSiteen);
        }
        showUpdateQuestion(paramToQuestion);

    }

    protected void showUpdateQuestion(Map<String, String> paramToQuestion) {
        String messagerForUpdate = "";
        if (paramToQuestion.size() > 3) {
            messagerForUpdate = getMessage("Update.description.nameForSite.message");
        } else if (paramToQuestion.containsKey("descriptionru")) {
            messagerForUpdate = getMessage("Update.description.message");
        } else if (paramToQuestion.containsKey("nameForSiteru")) {
            messagerForUpdate = getMessage("CONFIRMATION.qwestion");
        }
        String finalMessagerForUpdate = messagerForUpdate;
        if (!paramToQuestion.isEmpty())
            showOptionDialog(getMessage("CONFIRMATION")
                    , messagerForUpdate
                    , MessageType.CONFIRMATION
                    , new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (finalMessagerForUpdate.equals(getMessage("Update.description.nameForSite.message"))) {
                                        getItem().setDescriptionLang1(paramToQuestion.get("descriptionru"));
                                        getItem().setDescriptionLang2(paramToQuestion.get("descriptionkz"));
                                        getItem().setDescriptionLang3(paramToQuestion.get("descriptionen"));
                                        getItem().setNameForSiteLang1(paramToQuestion.get("nameForSiteru"));
                                        getItem().setNameForSiteLang2(paramToQuestion.get("nameForSitekz"));
                                        getItem().setNameForSiteLang3(paramToQuestion.get("nameForSiteen"));
                                    } else if (finalMessagerForUpdate.equals(getMessage("CONFIRMATION.qwestion"))) {
                                        getItem().setNameForSiteLang1(paramToQuestion.get("nameForSiteru"));
                                        getItem().setNameForSiteLang2(paramToQuestion.get("nameForSitekz"));
                                        getItem().setNameForSiteLang3(paramToQuestion.get("nameForSiteen"));
                                    } else if (finalMessagerForUpdate.equals(getMessage("Update.description.message"))) {
                                        getItem().setDescriptionLang1(paramToQuestion.get("descriptionru"));
                                        getItem().setDescriptionLang2(paramToQuestion.get("descriptionkz"));
                                        getItem().setDescriptionLang3(paramToQuestion.get("descriptionen"));
                                    }
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
    }

    protected static String concat(String s1, String s2) {
        StringBuilder sb = new StringBuilder("");
        if (s1 != null) sb.append(s1);
        if (s1 != null && s2 != null) sb.append("<br/>");
        if (s2 != null) sb.append(s2);
        return sb.toString();
    }


    @Override
    protected boolean preCommit() {
        if (getItem().getCode() == null ||
                commonService.getCount(Requisition.class,
                        "select e from tsadv$Requisition e " +
                                " where e.code = :code",
                        ParamsMap.of("code", getItem().getCode())) > 0) {
            if (PersistenceHelper.isNew(getItem()) && recruitmentConfig.getAutogenerateRequisitionCode()) {
                long currentSequenceValue = generateNextSequenceValue();
                getItem().setCode(String.format("R%07d", currentSequenceValue));
            }
        }
        return super.preCommit();
    }

    protected long generateNextSequenceValue() {
        long currentSequenceValue = recruitmentService.getCurrentSequenceValue("requisitionCode");
        while (commonService.getEntities(Requisition.class, String.format("select e from tsadv$Requisition e" +
                " where e.code = 'R%07d'", currentSequenceValue), null, null).size() != 0) {
            currentSequenceValue = uniqueNumbersService.getNextNumber("requisitionCode");
        }
        return currentSequenceValue;
    }
}
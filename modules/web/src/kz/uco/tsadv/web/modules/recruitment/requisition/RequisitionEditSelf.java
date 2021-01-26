package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicEmploymentType;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

public class RequisitionEditSelf extends RequisitionCommonEdit {

    private static final Logger log = LoggerFactory.getLogger(RequisitionEditSelf.class);

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected CollectionDatasource<KeyValueEntity, Object> cardFilterSettingsDs;
    @Inject
    protected TabSheet tabSheet;
    @Named("fieldGroup.endDate")
    protected DateField endDateField;
    protected boolean fromLink;

    @Override
    protected boolean preCommit() {
        isCommited = true;
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        isCommited = false;
        return super.postCommit(committed, close);
    }

    @Override
    public void init(Map<String, Object> params) {
        page = "selfservice";
        codeConfig = fieldGroup.getField("code");
        startDateConfig = fieldGroup.getField("startDate");
        requisitionTypeConfig = fieldGroup.getField("requisitionType");
        requisitionTemplateConfig = fieldGroup.getField("requisitionTemplate");
        jobGroupConfig = fieldGroup.getField("jobGroup");
        locationConfig = fieldGroup.getField("location");
        recruiterPersonGroupConfig = fieldGroup.getField("recruiterPersonGroup");
        managerPersonGroupConfig = fieldGroup.getField("managerPersonGroup");
        organizationGroupConfig = fieldGroup.getField("organizationGroup");
        positionGroupConfig = fieldGroup.getField("positionGroup");
        substitutablePersonGroupConfig = fieldGroup.getField("substitutablePersonGroup");
        forSubstitutionConfig = fieldGroup.getField("forSubstitution");
        requisitionStatusConfig = fieldGroup.getField("requisitionStatus");
        endDateGroupConfig = fieldGroup.getField("endDate");
        substitutableDescription = fieldGroup.getField("substitutableDescription");

        endDateGroupConfig.addValidator(value -> {
            if (value != null) {
                Date date = (Date) value;
                if (date.before(getItem().getFinalCollectDate())) {
                    throw new ValidationException(getMessage("requisitionDateValidation"));
                }
            }
        });

        super.init(params);
        if (params.containsKey("fromLink")) {
            fromLink = true;
        }
    }

    @Override
    protected void setOptionsForJobGroupNew() {
        if (!fullDicJobforManagerConig.getEnabled()) {
            optionJobGroupDS.setQuery("select cbp.jobGroup " +
                    "                from base$PositionExt bp, tsadv$OrganizationStructure os, base$PositionExt cbp " +
                    "                where 1=1 " +
                    "                and os.path like concat('%',concat(bp.organizationGroupExt.id,'%')) " +
                    "                and cbp.organizationGroupExt.id=os.organizationGroup.id " +
                    "                and :custom$sysDate between os.startDate and os.endDate " +
                    "                and :custom$sysDate between cbp.startDate and cbp.endDate " +
                    "                and :custom$sysDate between bp.startDate and bp.endDate " +
                    "                and bp.group.id= :custom$groupId ");

            Map<String, Object> params = new HashMap<>();

            params.put("groupId", userSession.getAttribute(StaticVariable.POSITION_GROUP_ID));
//        params.put("groupId", UUID.fromString("3bfcd169-3a8e-e910-aba8-04a6e9dc41c8"));
            params.put("sysDate", CommonUtils.getSystemDate());
            optionJobGroupDS.refresh(params);
            jobGroupConfig.setOptionsDatasource(optionJobGroupDS);
        } else {
            super.setOptionsForJobGroupNew();
        }


    }

    @Override
    protected void postInit() {
        endDateField.addValueChangeListener(e -> {
            try {
                endDateField.validate();
            } catch (ValidationException e1) {
                e1.printStackTrace();
                showNotification(e1.getMessage(), NotificationType.TRAY);
            }
        });

        /*
         * RequisitionCommonEdit.postInit()
         */
        super.postInit();

        if (browseOnly) {
            tabSheet.getTabs().stream().forEachOrdered(tab -> {
                tab.setVisible(tab.getName().equalsIgnoreCase("mainInfo"));
            });
            fieldGroup.getFields().forEach(fieldConfig -> fieldConfig.setEditable(false));
        }

        if (PersistenceHelper.isNew(getItem())) {
            tabSheet.getTab("competences").setVisible(false);
            tabSheet.getTab("jobRequests").setVisible(false);
        } else {
            tabSheet.getTab("competences").setVisible(true);
            //candidates for only standard requisitions
            tabSheet.getTab("jobRequests").setVisible(getItem().getRequisitionType() != RequisitionType.TEMPLATE
                    && getItem().getRequisitionStatus() != RequisitionStatus.ON_APPROVAL
                    && getItem().getRequisitionStatus() != RequisitionStatus.DRAFT);
        }

        if (startDateConfig.isEditable()) {
            Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            DateField<Date> startDateField = (DateField) startDateConfig.getComponent();
            if (PersistenceHelper.isNew(getItem())) {
                startDateField.setRangeStart(today);
            }
//            else
//                startDateField.setRangeStart(today.before(getItem().getStartDate()) ? today : getItem().getStartDate());

            if (recruitmentConfig.getCheckRequisitionFinalDate()) {
                String dateFormatString = messages.getMainMessage(Datatypes.getFormatStrings(userSessionSource.getLocale()).getDateFormat());
                DateFormat df = new SimpleDateFormat(dateFormatString);

                startDateField.addValueChangeListener(e -> {
                    if (e.getValue() != null) {
                        Date startDate = (Date) e.getValue();
                        int finalCollectDays = getFinalCollectDays();
                        Date minFinalCollectDate = DateUtils.addDays(getItem().getStartDate(), finalCollectDays);

                        if (getItem().getFinalCollectDate() == null || getItem().getFinalCollectDate().before(minFinalCollectDate)) {
                            showNotification(messages.getMainMessage("validationFail.caption"), String.format(messages.getMainMessage("Requisition.finalCollectDate.check.err"), df.format(minFinalCollectDate)), NotificationType.TRAY);
                            /*getItem().setFinalCollectDate(minFinalCollectDate);*/
                        }
                    }
                });
            }
        }

        if (getItem().getRequisitionStatus() == RequisitionStatus.DRAFT)
            windowCommit.setCaption(messages.getMainMessage("Requisition.saveAsDraft"));

        tabSheet.addSelectedTabChangeListener(event -> {
            if ("mainInfo".equals(event.getSelectedTab().getName())) {
                Timer newTimer = componentsFactory.createTimer();
                newTimer.setRepeating(false);
                newTimer.setDelay(1);
                newTimer.addActionListener(this::adjustRichTextAreas);
                addTimer(newTimer);
                newTimer.start();
            }
        });
        if (fromLink) {
            tabSheet.setSelectedTab("jobRequests");
//            changeSlitter();
        }
    }

    @Override
    protected void initNewItem(Requisition item) {
        super.initNewItem(item);

        PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        if (personGroup != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("personGroupId", personGroup.getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            List<OrganizationGroupExt> organizationGroups = commonService.getEntities(OrganizationGroupExt.class,
                    "select e " +
                            "    from base$OrganizationGroupExt e " +
                            "   where e.id in (select ps.organizationGroup.id " +
                            "                    from base$AssignmentExt a, tsadv$PositionStructure ps " +
                            "                   where a.personGroup.id = :personGroupId " +
                            "                     and :systemDate between a.startDate and a.endDate " +
                            "                     and ps.positionGroup.id = a.positionGroup.id " +
                            "                     and :systemDate between ps.startDate and ps.endDate " +
                            "                     and :systemDate between ps.posStartDate and ps.posEndDate " +
                            "                  )",
                    params,
                    "organizationGroup.browse");
            if (organizationGroups != null && !organizationGroups.isEmpty()) {
                item.setOrganizationGroup(organizationGroups.get(0));
            }
        }

        item.setEmploymentType(commonService.getEntity(DicEmploymentType.class, "FULLTIME"));
    }

    @Inject
    protected SplitPanel splitMainInfo;

    @Override
    public void ready() {
        super.ready();
        try {
            splitMainInfo.setMinSplitPosition((int) fieldGroup.getWidth(), UNITS_PIXELS);
            splitMainInfo.setMaxSplitPosition((int) fieldGroup.getWidth(), UNITS_PIXELS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        calcCostCenter();
    }

    @Override
    public void commitAndClose() {
        /*RequisitionCommonEdit.commit()*/
        super.commit();
    }

    @Inject
    protected RichTextArea addRichTextArea1;
    @Inject
    protected RichTextArea addRichTextArea2;
    @Inject
    protected RichTextArea addRichTextArea3;
    @Inject
    protected VBoxLayout split1vbox2;
    @Inject
    protected VBoxLayout split2vbox2;
    @Inject
    protected VBoxLayout split3vbox2;

    public void adjustRichTextAreas(Timer timer) {
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        /* only for manager */
        if (recruitmentConfig.getCheckRequisitionFinalDate()) {
            int finalCollectDays = getFinalCollectDays();
            Date minFinalCollectDate = DateUtils.addDays(getItem().getStartDate(), finalCollectDays);

            if (minFinalCollectDate.after(getItem().getFinalCollectDate())) {
                String dateFormatString = messages.getMainMessage(Datatypes.getFormatStrings(userSessionSource.getLocale()).getDateFormat());
                DateFormat df = new SimpleDateFormat(dateFormatString);
                errors.add(String.format(messages.getMainMessage("Requisition.finalCollectDate.check.err"), df.format(minFinalCollectDate)));
            }
        }

        if (getItem().getStartDate().before(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)))
            errors.add(messages.getMainMessage("Requisition.startDate.err"));

        super.postValidate(errors);
    }

    protected void setOptionsForPositionStructure() {
        if (recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER")) {
            optionPositionStructureDS.setQuery("select pe.group \n" +
                    "from base$PositionExt pe\n" +
                    "where pe.organizationGroupExt.id in\n" +
                    "      (select os.organizationGroup.id\n" +
                    "       from tsadv$OrganizationStructure os\n" +
                    "       where os.path like concat('%', concat(\n" +
                    "           (select ppe.organizationGroupExt.id\n" +
                    "            from base$PositionExt ppe\n" +
                    "            where ppe.group.id = :custom$managerPositionGroupId \n" +
                    "              and :session$systemDate between ppe.startDate and ppe.endDate), '%'))\n" +
                    "         and (:session$systemDate between os.startDate and os.endDate\n" +
                    "         or (os.startDate > :session$systemDate and\n" +
                    "             os.startDate in (select min(p.startDate) from base$OrganizationExt p where p.group.id = os.organizationGroup.id))))\n" +
                    "  and (:session$systemDate between pe.startDate and pe.endDate\n" +
                    "  or (pe.startDate > :session$systemDate and\n" +
                    "      pe.startDate in (select min(p.startDate) from base$PositionExt p where p.group.id = pe.group.id)))\n" +
                    "  and pe.fte > 0\n" +
                    "  and pe.deleteTs is null");
            Map<String, Object> params = new HashMap<>();
            PositionGroupExt positionGroupExt = null;
            if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() != null) {
                PersonGroupExt personGroupExt = dataSupplier.reload(
                        ((PersonGroupExt) ((HasValue) managerPersonGroupConfig.getComponent()).getValue()),
                        "personGroupExt.for.requisition.optionDs");
                if (personGroupExt.getCurrentAssignment() != null) {
                    positionGroupExt = personGroupExt.getCurrentAssignment().getPositionGroup();
                }
            }
            params.put("managerPositionGroupId", (positionGroupExt != null) ? positionGroupExt.getId() : null);
            optionPositionStructureDS.refresh(params);
        } else if (recruitmentConfig.getOrganizationGenerationType().name().equals("FULL")) {
            optionPositionStructureDS.setQuery("select pe.group \n" +
                    "from base$PositionExt pe\n" +
                    "where pe.organizationGroupExt.id in\n" +
                    "      (select oe.group.id\n" +
                    "       from base$OrganizationExt oe\n" +
                    "       where (:session$systemDate between oe.startDate and oe.endDate\n" +
                    "         or (oe.startDate > :session$systemDate and\n" +
                    "             oe.startDate in (select min(p.startDate) from base$OrganizationExt p where p.group.id = oe.group.id))))\n" +
                    "  and (:session$systemDate between pe.startDate and pe.endDate\n" +
                    "  or (pe.startDate > :session$systemDate and\n" +
                    "      pe.startDate in (select min(p.startDate) from base$PositionExt p where p.group.id = pe.group.id)))\n" +
                    "  and pe.fte > 0\n" +
                    "  and pe.deleteTs is null");
            optionPositionStructureDS.refresh();
        }
    }

    protected void setOptionsForOrganizationGroup() {
        if (recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER")) {
            optionOrganizationGroupDS.setQuery("select os.organizationGroup from tsadv$OrganizationStructure os, " +
                    "                   base$OrganizationExt oe, base$PositionExt pe " +
                    "                   where :session$systemDate between os.startDate and os.endDate " +
                    "                   and :session$systemDate between oe.startDate and oe.endDate " +
                    "                   and os.path like concat('%',concat(pe.organizationGroupExt.id,'%')) " +
                    "                   and oe.deleteTs is null " +
                    "                   and pe.group.id=:custom$managerPositionGroupId");
            Map<String, Object> params = new HashMap<>();
            PositionGroupExt positionGroupExt = null;
            if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() != null) {

                PersonGroupExt personGroupExt = dataSupplier.reload(
                        ((PersonGroupExt) ((HasValue) managerPersonGroupConfig.getComponent()).getValue()),
                        "personGroupExt.for.requisition.optionDs");
                if (personGroupExt.getCurrentAssignment() != null) {
                    positionGroupExt = personGroupExt.getCurrentAssignment().getPositionGroup();
                }
            }
            params.put("managerPositionGroupId", (positionGroupExt != null) ? positionGroupExt.getId() : null);

            optionOrganizationGroupDS.refresh(params);
        } else if (recruitmentConfig.getOrganizationGenerationType().name().equals("FULL")) {
            optionOrganizationGroupDS.setQuery("select os.organizationGroup from tsadv$OrganizationStructure os, base$OrganizationExt oe " +
                    "                    where os.path like concat('%',concat(oe.group.id,'%')) " +
                    "                    and :session$systemDate between os.startDate and os.endDate " +
                    "                    and :session$systemDate between oe.startDate and oe.endDate " +
                    "                    and os.deleteTs is null ");
            optionOrganizationGroupDS.refresh();
        }
    }
}
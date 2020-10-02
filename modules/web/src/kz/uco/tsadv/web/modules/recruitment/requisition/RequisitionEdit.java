package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.widgets.CubaHorizontalSplitPanel;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractSplitPanel;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.FieldChangeEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.config.DisplayAllEmployees;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.modules.recruitment.model.RequisitionRequirement;
import kz.uco.tsadv.modules.recruitment.model.RequisitionTmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class RequisitionEdit extends RequisitionCommonEdit {
    private static final Logger log = LoggerFactory.getLogger(RequisitionEdit.class);
    @Inject
    protected CollectionDatasource<KeyValueEntity, Object> cardFilterSettingsDs;
    @Inject
    protected Events events;

    @WindowParam
    protected String redirectTab;

    @Inject
    protected SplitPanel vSplitter;

    @Inject
    protected FieldGroup fieldGroup1;

    @Inject
    protected TabSheet tabSheet;
    protected boolean fromLink;
    @Inject
    protected CollectionDatasource<PersonGroupExt, UUID> substitutablePersonGroupsDs;
    @Inject
    protected CollectionDatasource<FieldChangeEntity, UUID> changesDs;
    @Inject
    protected HBoxLayout actionHboxId;
    @Inject
    protected SplitPanel splitMainInfo;
    @Named("requisitionRequirementsDataGrid.create")
    protected CreateAction requisitionRequirementsDataGridCreate;
    @Named("requisitionRequirementsDataGrid.edit")
    protected EditAction requisitionRequirementsDataGridEdit;
    @Inject
    protected CollectionDatasource<RequisitionRequirement, UUID> requisitionRequirementsDs;
    protected Map<String, Object> mapForRequirement;
    /*  @Named("fieldGroup1.jobGroup")
    protected PickerField jobGroupField;

    @Named("requisitionFieldGroup.nameForSiteLang1Id")
    protected TextField nameForSiteLang1;*/

    @Override
    public void init(Map<String, Object> params) {
        page="recruter";
        codeConfig = fieldGroup1.getField("code");
        startDateConfig = fieldGroup1.getField("startDate");
        requisitionTypeConfig = fieldGroup1.getField("requisitionType");
        requisitionTemplateConfig = fieldGroup1.getField("requisitionTemplate");
        jobGroupConfig = fieldGroup1.getField("jobGroup");
        locationConfig = fieldGroup1.getField("location");
        recruiterPersonGroupConfig = fieldGroup1.getField("recruiterPersonGroup");
        managerPersonGroupConfig = fieldGroup1.getField("managerPersonGroup");
        organizationGroupConfig = fieldGroup1.getField("organizationGroup");
        positionGroupConfig = fieldGroup1.getField("positionGroup");
        substitutablePersonGroupConfig = fieldGroup1.getField("substitutablePersonGroup");
        forSubstitutionConfig = fieldGroup1.getField("forSubstitution");
        requisitionStatusConfig = fieldGroup1.getField("requisitionStatus");
        substitutableDescription = fieldGroup1.getField("substitutableDescription");
        /*
         * RequisitionCommonEdit.init(params)
         */

        super.init(params);

        if (params.containsKey("fromLink")) {
            fromLink = true;
        }
    }

    protected void setOptionsForOrganizationGroup(Boolean enableList) {
        if (organizationGroupLookupPickerField.isEditable() && enableList) {
            optionOrganizationGroupDS.setQuery("select os.organizationGroup from tsadv$OrganizationStructure os, " +
                    "                   base$OrganizationExt oe " +
                    "                    where os.path like concat('%',concat(oe.group.id,'%')) " +
                    "                    and :session$systemDate between os.startDate and os.endDate " +
                    "                    and :session$systemDate between oe.startDate and oe.endDate " +
                    "                    and oe.deleteTs is null ");
            optionOrganizationGroupDS.refresh();
            organizationGroupConfig.setOptionsDatasource(optionOrganizationGroupDS);
        }
    }
    public void hideMembers() {
        //Команда по найму доступна для RECRUITING_MANAGER
        Map<String, Object> map = new HashMap<>();
        map.put("sessionUserID", userSession.getUser().getId());
        map.put("code", "RECRUITING_MANAGER");
        map.put("systemDate", CommonUtils.getSystemDate());
        String query = "SELECT su " +
                "FROM base$UserExt su " +
                "JOIN tsadv$HrUserRole hrRule ON su.id = hrRule.user.id " +/*
                "JOIN tsadv$DicHrRole dicRule ON dicRule.id = hrRule.role.id " +*/
                "WHERE hrRule.role.code = :code " +
                "AND su.id = :sessionUserID " +
                "and :systemDate between hrRule.dateFrom and hrRule.dateTo";
        Long extList = commonService.getCount(UserExt.class, query, map);
        if (extList > 0) {
            tabSheet.getTab("members").setVisible(true);
        } else {
            tabSheet.getTab("members").setVisible(false);
        }
    }

    @Override
    protected void postInit() {
        hideMembers();
        /*
         * RequisitionCommonEdit.postInit()
         */
        super.postInit();

        if (fromLink) {
            tabSheet.setTab("jobRequests");
            changeSlitter();
        }

        resizeAllFieldProperty();

        if (PersistenceHelper.isNew(getItem())) {
            tabSheet.getTab("competences").setVisible(false);
            tabSheet.getTab("hiringSteps").setVisible(false);
            tabSheet.getTab("members").setVisible(false);
            tabSheet.getTab("postingChannels").setVisible(false);
            tabSheet.getTab("jobRequests").setVisible(false);
            tabSheet.getTab("groupInterviews").setVisible(false);
            tabSheet.getTab("questionnaires").setVisible(false);
        } else {
            tabSheet.getTab("competences").setVisible(true);
            tabSheet.getTab("hiringSteps").setVisible(true);
            tabSheet.getTab("members").setVisible(true);
            tabSheet.getTab("postingChannels").setVisible(true);
            //candidates for only standard requisitions
            tabSheet.getTab("jobRequests").setVisible(getItem().getRequisitionType() != RequisitionType.TEMPLATE);
            tabSheet.getTab("groupInterviews").setVisible(true);
            tabSheet.getTab("questionnaires").setVisible(true);
        }

        if (redirectTab != null) {
            TabSheet.Tab tab = tabSheet.getTab(redirectTab);
            if (tab != null) tabSheet.setTab(tab);
        }
        if (browseOnly) {
            tabSheet.getTabs().stream().forEachOrdered(tab -> {
                tab.setVisible(tab.getName().equalsIgnoreCase("mainInfo") ||
                        tab.getName().equalsIgnoreCase("competences"));
            });
            fieldGroup1.getFields().forEach(fieldConfig -> fieldConfig.setEditable(false));
        }

        tabSheet.addSelectedTabChangeListener(event -> {
            tabSheetSelectedTabChangeListener(event);
        });


        /* Tab CHANGES for recruiter manager */
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        boolean isRecruiterManager = userPersonGroupId != null && getItem().getOrganizationGroup() != null
                && getRmPersonGroupId(getItem().getOrganizationGroup().getId())
                .stream()
                .filter(organizationHrUser -> employeeService.getPersonGroupByUserId(organizationHrUser.getUser().getId()).getId().equals(userPersonGroupId))//TODO:personGroup need to test
                .count() > 0;

        if (this.isRecruiter && isRecruiterManager && getItem().getRequisitionStatus() == RequisitionStatus.ON_APPROVAL) {
            if (hasBeenOpened()) {
                actionHboxId.setVisible(false);
                tabSheet.getTab("changes").setVisible(true);
                fillChangesTable();
                tabSheet.setTab("changes");
            }
        }
    }

    protected void tabSheetSelectedTabChangeListener(TabSheet.SelectedTabChangeEvent event) {
        String tabName = event.getSelectedTab().getName();
        if ("jobRequests".equalsIgnoreCase(tabName)) {
            changeSlitter();
        }
    }


    protected void changeSlitter() {
        SplitPanel splitPanel = (SplitPanel) tabSheet.getComponent("vSplitter");
        GroupBoxLayout storeFilterBox = (GroupBoxLayout) tabSheet.getComponent("storeStyleFilterBox");

        CubaHorizontalSplitPanel cubaHorizontalSplitPanel = splitPanel.unwrap(CubaHorizontalSplitPanel.class);
        cubaHorizontalSplitPanel.setDockable(true);
        cubaHorizontalSplitPanel.addSplitterClickListener(new AbstractSplitPanel.SplitterClickListener() {
            @Override
            public void splitterClick(AbstractSplitPanel.SplitterClickEvent event) {
                if (cubaHorizontalSplitPanel.getSplitPosition() > 100) {
                    cubaHorizontalSplitPanel.setSplitPosition(0);
//                            splitPanel.setMaxSplitPosition(0,0);
                } else{
                    cubaHorizontalSplitPanel.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
                    cubaHorizontalSplitPanel.setMaxSplitPosition(25, Sizeable.Unit.PERCENTAGE);
//                            splitPanel.setMaxSplitPosition(350,0);
                }

            }
        });
    }

    protected void fillChangesTable() {
        List<RequisitionTmp> reqTmpList = commonService.getEntities(RequisitionTmp.class,
                "select e " +
                        "    from tsadv$RequisitionTmp e " +
                        "   where e.requisition.id = :requisitionId " +
                        "     and e.requisitionStatus = 1 " +
                        "order by e.createTs desc",
                Collections.singletonMap("requisitionId", getItem().getId()),
                "requisitionTmp.view");

        List<String> excludeProperties = Arrays.asList("requisition", "viewCount", "requisitionStatus");
        if (reqTmpList != null && reqTmpList.size() > 0) {
            MetaClass requisitionMetaClass = metadata.getClass("tsadv$Requisition");
            changesDs.clear();

            for (String field : metadata
                    .getClassNN("tsadv$RequisitionTmp")
                    .getOwnProperties()
                    .stream()
                    .map(MetaProperty::getName)
                    .filter(p -> excludeProperties.indexOf(p) < 0)
                    .collect(Collectors.toList())) {
                try {
                    Method getRequisitionMethod = new PropertyDescriptor(field, Requisition.class).getReadMethod();
                    Method getRequisitionTmpMethod = new PropertyDescriptor(field, RequisitionTmp.class).getReadMethod();
                    Object newValue = getRequisitionMethod.invoke(getItem());
                    Object oldValue = getRequisitionTmpMethod.invoke(reqTmpList.get(0));

                    if (!compare(newValue, oldValue)) {
                        FieldChangeEntity fce = metadata.create(FieldChangeEntity.class);
                        fce.setField(messages.getTools().getPropertyCaption(requisitionMetaClass, field));
                        fce.setOldValue(getCaptionProperty(field, oldValue));
                        fce.setNewValue(getCaptionProperty(field, newValue));
                        changesDs.includeItem(fce);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    protected String getCaptionProperty(String field, Object value) {
        if (value == null) return "";
        if (value instanceof Boolean) {
            return messages.getMainMessage("filter.param.boolean." + value);
        } else if (value instanceof Date) {
            String dateFormatString = messages.getMainMessage(Datatypes.getFormatStrings(userSessionSource.getLocale()).getDateFormat());
            DateFormat df = new SimpleDateFormat(dateFormatString);
            return df.format(value);
        } else if (value instanceof Double) {
            Datatype<Double> datatype = Datatypes.get(DoubleDatatype.NAME);
            return datatype.format(value, userSessionSource.getLocale());
        } else if (value instanceof AbstractDictionary) {
            return ((AbstractDictionary) value).getLangValue();
        } else if (value instanceof AbstractGroup) {
            Component component = fieldGroup1.getField(field).getComponent();
            if (component instanceof PickerField) {
                if (((PickerField) component).getCaptionMode() == CaptionMode.PROPERTY) {
                    String captionProperty = ((PickerField) component).getCaptionProperty();
                    try {
                        Method getMethod = value.getClass().getDeclaredMethod("get" + captionProperty.substring(0, 1).toUpperCase() + captionProperty.substring(1));
                        return getMethod.invoke(value) == null ? "" : getMethod.invoke(value).toString();
                    } catch (Exception e) {
                        return "";
                    }
                }
            }
        }


        return value.toString();
    }

    @Override
    public void ready() {
        super.ready();
        try {
            splitMainInfo.setMinSplitPosition((int) fieldGroup1.getWidth() + 20, UNITS_PIXELS);
            splitMainInfo.setMaxSplitPosition((int) fieldGroup1.getWidth() + 20, UNITS_PIXELS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*
         * RequisitionCommonEdit.ready()
         */

    /*    jobGroupField.addValueChangeListener(e -> {
            if (e.getValue()!=null)
            nameForSiteLang1.setValue(((JobGroup) e.getValue()).getJob().getJobName());
        });*/


        mapForRequirement = new HashMap<>();
        mapForRequirement.put("excludeRcQuestionIds", new ArrayList<UUID>());
        requisitionRequirementsDataGridCreate.setInitialValues(ParamsMap.of("critical", true));
        requisitionRequirementsDataGridCreate.setBeforeActionPerformedHandler(() -> {
            mapForRequirement.put("excludeRcQuestionIds",
                    requisitionRequirementsDs.getItems().stream().map(requisitionRequirement ->
                            requisitionRequirement.getRequirement().getId()).collect(Collectors.toList()));
            return true;
        });
        requisitionRequirementsDataGridEdit.setBeforeActionPerformedHandler(() -> {
            mapForRequirement.put("excludeRcQuestionIds",
                    requisitionRequirementsDs.getItems().stream().map(requisitionRequirement ->
                            requisitionRequirement.getRequirement().getId()).collect(Collectors.toList()));
            return true;
        });
        requisitionRequirementsDataGridEdit.setWindowParams(mapForRequirement);
        requisitionRequirementsDataGridCreate.setWindowParams(mapForRequirement);

    }

    @Override
    protected boolean preCommit() {
        /*
         * RequisitionCommonEdit.preCommit()
         */
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
         /*
         * RequisitionCommonEdit.postCommit(committed, close)
         */
        return super.postCommit(committed, close);
    }

    @Override
    protected void initNewItem(Requisition item) {
        /*
         * RequisitionCommonEdit.initNewItem(item)
         */
        super.initNewItem(item);
    }

    @Override
    public void commitAndClose() {
        /*RequisitionCommonEdit.commit()*/
        super.commit();
    }

    /*custom*/
    protected void resizeAllFieldProperty() {
        fieldGroup1.getFields().forEach(fieldConfig -> fieldConfig.setWidth("300px"));
    }

    public void managerDescriptionCopy() {
        getItem().setDescriptionLang1(concat(getItem().getDescriptionLang1(), getItem().getManagerDescriptionLang1()));
        getItem().setDescriptionLang2(concat(getItem().getDescriptionLang2(), getItem().getManagerDescriptionLang2()));
        getItem().setDescriptionLang3(concat(getItem().getDescriptionLang3(), getItem().getManagerDescriptionLang3()));
        getItem().setDescriptionLang4(concat(getItem().getDescriptionLang4(), getItem().getManagerDescriptionLang4()));
        getItem().setDescriptionLang5(concat(getItem().getDescriptionLang5(), getItem().getManagerDescriptionLang5()));
    }

    @Inject
    protected TabSheet descTabSheet;

    @Inject
    protected RichTextArea requisitionRichTextArea1;
    @Inject
    protected RichTextArea requisitionRichTextArea2;
    @Inject
    protected RichTextArea requisitionRichTextArea3;
    @Inject
    protected VBoxLayout split1vbox1;
    @Inject
    protected VBoxLayout split2vbox1;
    @Inject
    protected VBoxLayout split3vbox1;

    public void adjustRichTextAreas(Timer timer) {
//        split1vbox1.expand(requisitionRichTextArea1);
//        split2vbox1.expand(requisitionRichTextArea2);
//        split3vbox1.expand(requisitionRichTextArea3);
//        if (((HasValue)positionGroupConfig.getComponent()).getValue()!=null){
//            PositionGroupExt positionGroupExt=(PositionGroupExt)((HasValue)positionGroupConfig.getComponent()).getValue();
//            setRequisitionsDescription(positionGroupExt);
//        }else {
//            setRequisitionsDescription(null);
//        }
//        requisitionRichTextArea1.setHeight("350px");

    }

    public Component getOldValue(FieldChangeEntity entity) {
        return generateHtmlCell(entity.getOldValue());
    }

    public Component getNewValue(FieldChangeEntity entity) {
        return generateHtmlCell(entity.getNewValue());
    }

    protected Component generateHtmlCell(String value) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
//        vBoxLayout.setSizeFull();
        Label label = componentsFactory.createComponent(Label.class);
        label.setHtmlEnabled(true);
        label.setValue(value);
//        label.setSizeFull();
        vBoxLayout.add(label);
        return vBoxLayout;
    }
}
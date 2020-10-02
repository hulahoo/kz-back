package kz.uco.tsadv.web.modules.personal.frames;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.PositionConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.GradeRuleValue;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.web.modules.personal.hierarchyelement.HierarchyElementBrowse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Adilbekov Yernar
 */

//TODO поудалять всё лишнее
public class PositionFrame extends AbstractFrame {

    private static final String IMAGE_CELL_HEIGHT = "50px";
    private static final int MAX_AVAILABLE_SPACE = 30;

    private static final Logger LOG = LoggerFactory.getLogger(PositionFrame.class);

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected HBoxLayout actionsPane;

    @Inject
    protected Button editBtn;

    @Inject
    protected FieldGroup fieldGroup;


    protected HierarchyElementBrowse browse;

    @Inject
    protected ButtonsPanel competencePosButtonsPanel;

    @Inject
    protected ButtonsPanel surChargeButtons;

    @Inject
    protected TabSheet tabSheet;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected UserSession userSession;

    private boolean editMode = false;
    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;

    @Inject
    protected Datasource<GradeRuleValue> gradeGroupValueDs;
    @Inject
    protected Datasource<PositionExt> positionDs;
    @Inject
    protected HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;
    protected OrgAnalytics analytics = null;
    @Named("fieldGroup1.requisitionHiringStep")
    protected PickerField requisitionHiringStepField;
    @Named("orgAnalyticsPosition.calendar")
    protected LookupField calendar;
    @Named("orgAnalyticsPosition.offset")
    protected LookupField offset;

    @Inject
    protected Datasource<OrgAnalytics> orgAnalyticsBetweenDs;

    @Named("fieldGroup2.positionNameLang1Reducted")
    protected EntityLinkField positionNameLang1ReductedField;
    @Named("fieldGroup2.positionNameLang2Reducted")
    protected EntityLinkField positionNameLang2ReductedField;
    @Named("fieldGroup2.positionNameLang3Reducted")
    protected EntityLinkField positionNameLang3ReductedField;

    /*@Named("fieldGroup.positionFullNameLang1")
    private TextField positionFullNameLang1Field;
    @Named("fieldGroup.positionFullNameLang2")
    private TextField positionFullNameLang2Field;
    @Named("fieldGroup.positionFullNameLang3")
    private TextField positionFullNameLang3Field;
    @Named("fieldGroup.positionFullNameLang4")
    private TextField positionFullNameLang4Field;
    @Named("fieldGroup.positionFullNameLang5")
    private TextField positionFullNameLang5Field;*/

    @Named("fieldGroup.positionNameLang1")
    protected TextField positionNameLang1Field;
    @Named("fieldGroup.positionNameLang2")
    protected TextField positionNameLang2Field;
    @Named("fieldGroup.positionNameLang3")
    protected TextField positionNameLang3Field;
    @Named("fieldGroup.positionNameLang4")
    protected TextField positionNameLang4Field;
    @Named("fieldGroup.positionNameLang5")
    protected TextField positionNameLang5Field;
    @Named("fieldGroup.positionStatus")
    protected PickerField positionStatusField;
    @Named("fieldGroup.payroll")
    protected PickerField payrollField;

    @Override
    public void init(Map<String, Object> params) {
        //fieldGroup.getField("positionStatus").setRequired(true);
        LOG.info("HERE");

        positionNameLang1ReductedField.setScreenCloseListener((window, actionId) -> {
            positionDs.refresh();
            hierarchyElementsDs.refresh();
        });

        positionNameLang2ReductedField.setScreenCloseListener((window, actionId) -> {
            positionDs.refresh();
            hierarchyElementsDs.refresh();
        });

        positionNameLang3ReductedField.setScreenCloseListener((window, actionId) -> {
            positionDs.refresh();
            hierarchyElementsDs.refresh();
        });


        //this.editable(false);
        /*Table surChargeTable = (Table) getComponent("surChargeTable");
        ((CreateAction) surChargeTable.getAction("create")).setWindowId("tsadv$SurChargePosition.edit");
        ((EditAction) surChargeTable.getAction("edit")).setWindowId("tsadv$SurChargePosition.edit");*/

        /*tabSheet.addSelectedTabChangeListener(event -> {
                editBtn.setVisible(!editMode);
        });*/

        positionDs.addItemPropertyChangeListener((e) -> {
            if (e.getProperty().equals("gradeGroup") || e.getProperty().equals("gradeRule")) {
                refreshGradeRuleValue();
            }
        });
        /*positionDs.addItemChangeListener((e) -> {
            DicPositionStatus positionStatus = commonService.getEntity(DicPositionStatus.class, "ACTIVE");
            positionStatusField.setValue(positionStatus);
            positionStatusField.setRequired(true);
            //setPayroll();
            refreshGradeRuleValue();
        });*/

        /*this.changeCursor = new HashMap<TextField, Boolean>(11, 1.0f) {{
            put(positionFullNameLang1Field, Boolean.FALSE);
            put(positionFullNameLang2Field, Boolean.FALSE);
            put(positionFullNameLang3Field, Boolean.FALSE);
            put(positionFullNameLang4Field, Boolean.FALSE);
            put(positionFullNameLang5Field, Boolean.FALSE);

            put(positionNameLang1Field, Boolean.FALSE);
            put(positionNameLang2Field, Boolean.FALSE);
            put(positionNameLang3Field, Boolean.FALSE);
            put(positionNameLang4Field, Boolean.FALSE);
            put(positionNameLang5Field, Boolean.FALSE);
        }};*/
        //this.cursorToBeginListener();

    }


    private void refreshGradeRuleValue() {
        Map<String, Object> paramsMap = new HashMap<>();
        if (positionDs.getItem() != null) {
            paramsMap.put("gradeGroupId", positionDs.getItem().getGradeGroup() == null ? null : positionDs.getItem().getGradeGroup().getId());
            paramsMap.put("gradeRuleId", positionDs.getItem().getGradeRule() == null ? null : positionDs.getItem().getGradeRule().getId());
            paramsMap.put("systemDate", CommonUtils.getSystemDate());

            List<GradeRuleValue> grv = commonService.getEntities(GradeRuleValue.class,
                    "select e " +
                            "    from tsadv$GradeRuleValue e " +
                            "   where e.gradeGroup.id = :gradeGroupId " +
                            "     and e.gradeRule.id = :gradeRuleId " +
                            "     and :systemDate between e.startDate and e.endDate"
                    , paramsMap
                    , "gradeRuleValue.edit");

            if (!grv.isEmpty())
                gradeGroupValueDs.setItem(grv.get(0));
            else
                gradeGroupValueDs.setItem(null);
        } else
            gradeGroupValueDs.setItem(null);
    }

    public void redirectCard(AssignmentExt assignment, String name) {
        openEditor("person-card", assignment, WindowManager.OpenType.THIS_TAB);
    }

    public Component generateUserImageCell(AssignmentExt entity) {
        /*Image image = WebCommonUtils.setImage(entity.getPersonGroup().getPerson().getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;*/
        return null;
    }

    public void setTabVisible(String id, boolean visible) {
        if (tabSheet.getTab(id) != null) tabSheet.getTab(id).setVisible(visible);
    }

    /**
     * Создать дочернюю ШЕ
     */
//    public void createChildSHE() {
//        browse.createChildPosition();
//    }
    public void createReport() {
        browse.createReport();
    }

    public PositionFrame setTab(String id) {
        tabSheet.setTab(id);
        return this;
    }

    /*public PositionFrame showButtons(boolean isShow) {
        positionButtons.setVisible(isShow);
        return this;
    }*/

    public PositionFrame editable(boolean isEdit) {
        isEdit = true;
        editMode = isEdit;
        fieldGroup.setEditable(isEdit);
        actionsPane.setVisible(isEdit);
        editBtn.setVisible(!isEdit);
        competencePosButtonsPanel.setVisible(isEdit);
        surChargeButtons.setVisible(isEdit);
        return this;
    }

    public void save() {
        browse.save(fieldGroup);
    }

    public void edit() {
        browse.edit(false);
        editable(true);
    }

    public void cancel() {
        editable(false);
        browse.cancel();
    }

    public void setHierarchyElementBrowse(HierarchyElementBrowse browse) {
        this.browse = browse;
    }

    protected void setPayroll() {
        Configuration configuration = AppBeans.get(Configuration.NAME);
        PositionConfig config = configuration.getConfig(PositionConfig.class);
        if (!config.getFillPayrollOnPosition().equals("")) {
            UUID payrollId = UUID.fromString(config.getFillPayrollOnPosition());
            DicPayroll payroll = commonService.getEntity(DicPayroll.class, payrollId);
           /* if (payroll!=null) {
                payrollField.setValue(payroll);
            }*/
        }
    }

}
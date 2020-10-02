package kz.uco.tsadv.web.modules.learning.budgetrequest;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetStatus;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.personal.model.OrganizationStructure;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class BudgetRequestEdit extends AbstractEditor<BudgetRequest> {
    private static final Logger log = LoggerFactory.getLogger(BudgetRequestEdit.class);

    public static final String MODE = "mode";

    public static final String MODE_HR_EDIT = "hrEdit";

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Datasource<BudgetRequest> budgetRequestDs;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private CommonService commonService;

    @Named("fieldGroup.budget")
    private PickerField budgetField;
    @Named("fieldGroup.learningType")
    private PickerField learningTypeField;
    @Named("fieldGroup.providerCompany")
    private PickerField providerCompanyField;
    @Named("fieldGroup.status")
    private LookupPickerField statusField;
    @Named("fieldGroup.course")
    private PickerField<Entity> courseField;
    @Named("fieldGroup.courseName")
    private TextField<String> courseNameField;
    @Named("fieldGroup.hour")
    private TextField hourField;
    @Named("fieldGroup.day")
    private TextField<String> dayField;
    @Inject
    private MessageTools messageTools;
    private Course tempCourse;
    private boolean preCommit = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(MODE) && MODE_HR_EDIT.equals(params.get(MODE))) {
            statusField.setEditable(true);
        }

        //lookups
        Map<String, Object> budgetParams = new HashMap<>();
        budgetParams.put("filterByRequestDate", Boolean.TRUE);
//        Utils.customizeLookup(budgetField, "tsadv$Budget.browse", WindowManager.OpenType.DIALOG, budgetParams);
        Utils.customizeLookup(learningTypeField, "tsadv$DicLearningType.browse", WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(providerCompanyField, "base$PartyExt.browse", WindowManager.OpenType.THIS_TAB, null);

        //course
        PickerField.LookupAction courseLookupAction = courseField.getLookupAction();
        PickerField.ClearAction courseClearAction = courseField.getClearAction();
        courseLookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        tempCourse = metadata.create(Course.class);
        courseField.addFieldListener((String text, Object prevValue) -> {
            tempCourse.setName(text);
            courseLookupAction.setEnabled(text != null && text.length() > 0);
            courseClearAction.setEnabled(text != null && text.length() > 0);

            getItem().setCourseName(text);
        });

        courseField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                courseNameField.setValue(((Course) e.getValue()).getName());
            }
        });

//        courseField.addValidator(v -> {
//            if (v == null)
//                throw new ValidationException(getMessage("BudgetRequest.course.validatorMsg"));
//            else {
//                Course c = (Course) v;
//                if (c.getName() == null || c.getName().length() == 0) {
//                    throw new ValidationException(getMessage("BudgetRequest.course.validatorMsg"));
//                }
//            }
//        });

        dayField.addValueChangeListener(e -> {
            if (hourField.getValue() == null) {
                String days = e.getValue().toString();
                hourField.setValue(Integer.valueOf(days) * 8);
            }
        });

        //month
        DatePicker datePicker = (DatePicker) componentsFactory.createComponent(DatePicker.NAME);
        datePicker.setResolution(DatePicker.Resolution.MONTH);
        datePicker.setDatasource(budgetRequestDs, "month");
//        fieldGroup.getField("month").setComponent(datePicker);

        //organization
//        FieldGroup.FieldConfig organizationGroupConfig = fieldGroup.getField("organizationGroup");
//        PickerField organizationGroupPickerField = componentsFactory.createComponent(PickerField.class);
////        organizationGroupPickerField.setDatasource(budgetRequestDs, organizationGroupConfig.getProperty());
////        organizationGroupPickerField.addAction(new AbstractAction("customLookup") {
//            @Override
//            public void actionPerform(Component component) {
//                openHierarchyElementLookup();
//            }
//
//            @Override
//            public String getIcon() {
//                return "font-icon:ELLIPSIS_H";
//            }
//        });
//        organizationGroupPickerField.setWidth("100%");
//        organizationGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
//        organizationGroupPickerField.setCaptionProperty("organization");
//        organizationGroupConfig.setComponent(organizationGroupPickerField);
    }

    private void openHierarchyElementLookup() {
        Map<String, Object> params = new HashMap<>();
        params.put("filterByUser", Boolean.TRUE);

        openLookup("tsadv$OrganizationStructure.browse",
                (items) -> {
                    for (Object o : items) {
                        OrganizationStructure ps = (OrganizationStructure) o;
                        getItem().setOrganizationGroup(ps.getOrganizationGroup());
                    }
                },
                WindowManager.OpenType.DIALOG,
                params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (getItem().getCourse() == null) {
            getItem().setCourse(tempCourse);
            courseField.setFieldEditable(true);
        }
        tempCourse.setName(getItem().getCourseName());

        budgetRequestDs.getItem().addPropertyChangeListener(e -> {
            if ("course".equals(e.getProperty())) {
                if (e.getValue() == null && !preCommit) {
                    courseField.setValue(tempCourse);
                }
                courseField.setFieldEditable(tempCourse.equals(getItem().getCourse()));
            }
        });
    }

    @Override
    protected boolean preCommit() {
        preCommit = true;
        if (tempCourse.equals(getItem().getCourse())) {
            getItem().setCourse(null);
        }

        return super.preCommit();
    }

    @Override
    protected void initNewItem(BudgetRequest item) {
        super.initNewItem(item);
        item.setInitiatorPersonGroup(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP));

        DicBudgetStatus budgetStatus = commonService.getEntity(DicBudgetStatus.class, "DRAFT");
        if (budgetStatus == null) {
            showNotification(getMessage("draft.status.null"), NotificationType.ERROR);
        } else {
            item.setStatus(budgetStatus);
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        DicBudgetStatus budgetStatus = getItem().getStatus();
        MetaClass metaClass = metadata.getClassNN(BudgetRequest.class);
        if (budgetStatus == null) {
            errors.add(messageTools.getDefaultRequiredMessage(metaClass, "status"));
        } else {
            if ("APPROVED".equals(budgetStatus.getCode())) {
                if (getItem().getProviderCompany() == null)
                    errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messageTools.getPropertyCaption(metaClass, "providerCompany")));
                if (getItem().getLearningCosts() == null)
                    errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messageTools.getPropertyCaption(metaClass, "learningCosts")));
                if (getItem().getTripCosts() == null)
                    errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messageTools.getPropertyCaption(metaClass, "tripCosts")));

            }
        }

        super.postValidate(errors);
    }
}
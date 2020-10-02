package kz.uco.tsadv.web.modules.personal.agreement;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebFieldGroup;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;
import com.haulmont.cuba.web.gui.components.WebLabel;
import com.haulmont.cuba.web.gui.components.WebLookupField;
import com.vaadin.ui.DateField;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAgreementStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicContractsType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.Agreement;
import kz.uco.tsadv.modules.personal.model.AgreementDocument;
import kz.uco.tsadv.modules.personal.model.Job;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class AgreementEdit extends AbstractEditor<Agreement> {
    @Inject
    protected Datasource<Agreement> agreementDs;
    @Inject
    protected CollectionDatasource<Category, UUID> categoriesDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected RuntimePropertiesFrame runtimeProperties;
    @Named("fieldGroup.agreementType")
    protected LookupPickerField agreementTypeField;
    @Named("fieldGroup.suspensionDateFrom")
    protected Field suspensionDateFromField;
    @Named("fieldGroup.suspensionDateTo")
    protected Field suspensionDateToField;
    @Named("fieldGroup.status")
    protected LookupPickerField statusField;
    @Inject
    protected CommonService commonService;
    @Inject
    protected GroupBoxLayout groupBox;
    @Named("agreementDocumentsTable.create")
    protected CreateAction agreementsDocumentsTableCreate;
    @Inject
    private DataManager dataManager;
    @Inject
    private ExportDisplay exportDisplay;

    public List<Agreement> agreementList = new ArrayList<>();

    protected boolean duplicateTypeAndNumberError;

    protected boolean activeLaborAgreementsError;

    @Override
    protected void initNewItem(Agreement item) {
        super.initNewItem(item);
        item.setDateFrom(CommonUtils.getSystemDate());
        item.setDateTo(CommonUtils.getEndOfTime());
        item.setAgreementNumber("Нет данных");
        item.setStatus(getAgreementStatus());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        agreementsDocumentsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("agreementDs") != null
                        ? ParamsMap.of("agreement", getDsContext().get("agreementDs").getItem())
                        : null);
        if (params.containsKey("allValue")) {
            agreementList = ((Collection<Agreement>) params.get("allValue")).stream().collect(Collectors.toList());
        }

    }

    @Override
    protected void postInit() {
        super.postInit();
        //TODO right lookup and droupdouwn
//        runtimeProperties.remove(getComponent("categoryFieldBox"));
//        FieldGroup fieldGroup = (FieldGroup) runtimeProperties.getComponents().stream().findFirst().orElse(null);
//        if (fieldGroup != null) {
//            for (FieldGroup.FieldConfig f : fieldGroup.getFields()) {
//                if (f.getCaption().contains("Group")) {
////                    LookupPickerField lookupPickerField= (LookupPickerField) f.getComponent();
////                    lookupPickerField.setCaptionMode(CaptionMode.PROPERTY);
////                    lookupPickerField.setCaptionProperty("person");
//                }
//            }
//        }
    }

    @Override
    public void ready() {
        super.ready();
        if ((getItem().getAgreementType() != null) && getItem().getAgreementType().getCode().equals("REDEMPTIONSCHEDULE")) {
            suspensionDateFromField.setVisible(true);
            suspensionDateToField.setVisible(true);
        }
        agreementTypeField.addValueChangeListener(e -> {
            if (agreementTypeField.getValue() != null) {
                if (((DicContractsType) agreementTypeField.getValue()).getCode().equals("REDEMPTIONSCHEDULE")) {
                    suspensionDateFromField.setVisible(true);
                    suspensionDateToField.setVisible(true);
                } else {
                    suspensionDateFromField.setVisible(false);
                    suspensionDateToField.setVisible(false);
                }
            }
        });
        statusField.addValueChangeListener(e -> {
            if (statusField.getValue() != null) {
                if (((DicAgreementStatus) statusField.getValue()).getCode().equals("A-SUSPENDED")) {
                    suspensionDateFromField.setEditable(true);
                    suspensionDateToField.setEditable(true);
                } else {
                    suspensionDateFromField.setEditable(false);
                    suspensionDateToField.setEditable(false);
                }
            }
        });
    }

    @Override
    protected boolean preCommit() {
        boolean isCommit = super.preCommit();
        if (agreementList != null && !agreementList.isEmpty()) {
            for (Agreement agreement : agreementList) {
                if (agreement.getPersonGroup().equals(getItem().getPersonGroup()) &&
                        agreement.getAgreementNumber().equals(getItem().getAgreementNumber()) &&
                        agreement.getAgreementType().equals(getItem().getAgreementType()) &&
                        !agreement.getId().equals(getItem().getId())) {
                    isCommit = false;
                    //duplicateTypeAndNumberError = true;
                    showNotification(getMessage("Attention"), getMessage("AgreementEdit.duplicateMessage"), NotificationType.TRAY);
                }
            }
        }
        if (isCommit && checkForActiveLaborAgreements()) {
            showNotification(getMessage("Attention"), getMessage("AgreementEdit.moreThanOneActiveLaborAgreements"), NotificationType.TRAY);
            isCommit = false;
        }
        return isCommit;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (duplicateTypeAndNumberError) {
            errors.add(getMessage("AgreementEdit.duplicateMessage"));
        }
        if (activeLaborAgreementsError) {
            errors.add(getMessage("AgreementEdit.moreThanOneActiveLaborAgreements"));
        }
    }

    protected Category getCategory(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", code);
        Category category = commonService.getEntity(Category.class, "select e" +
                        "  from sys$Category e" +
                        "  where e.entityType = 'tsadv$Agreement'" +
                        "  and e.name =:name",
                map, View.LOCAL);
        return category;
    }

    protected String getLocaleTranslate(String caption, Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("caption", caption);
        map.put("categoryId", category.getId());
        CategoryAttribute categoryAttribute = commonService.getEntity(CategoryAttribute.class, "select e" +
                        "  from sys$CategoryAttribute e" +
                        "  where e.categoryEntityType = 'tsadv$Agreement'" +
                        "  and e.name =:caption and e.category.id = :categoryId",
                map, View.LOCAL);
        if (categoryAttribute != null) {
            return categoryAttribute.getLocaleName();
        }
        return null;
    }

    protected DicAgreementStatus getAgreementStatus() {
        DicAgreementStatus status = commonService.getEntity(DicAgreementStatus.class, "A-ACTIVE");
        return status;
    }

    /**
     * Проверяет, имеются ли у сотрудника активные трудовые договоры.
     *
     * @return true, если имеются.
     */
    private boolean checkForActiveLaborAgreements() {
        if (getItem().getAgreementType().getCode().equals("LABOR") &&
                getItem().getStatus().getCode().equals("A-ACTIVE")) {
            for (Agreement agreement : agreementList) {
                if (!agreement.getId().equals(getItem().getId()) &&
                        agreement.getPersonGroup().equals(getItem().getPersonGroup()) &&
                        agreement.getStatus().equals(getItem().getStatus()) &&
                        agreement.getAgreementType().equals(getItem().getAgreementType())) {
                    return true;
                }
            }
        }
        return false;
    }


    public void downloadAgreementDocument(AgreementDocument document, String name) {
        if (document != null) {
            LoadContext<AgreementDocument> loadContext = LoadContext.create(AgreementDocument.class);
            loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AgreementDocument e where e.id = :dId")
                    .setParameter("dId", document.getId()))
                    .setView("agreementDocument.view");
            AgreementDocument downloadDocument = dataManager.load(loadContext);
            if (downloadDocument != null) {
                exportDisplay.show(downloadDocument.getFile());
            }
        }
    }
}
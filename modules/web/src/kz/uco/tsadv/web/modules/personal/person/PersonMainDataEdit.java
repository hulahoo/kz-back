package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebFieldGroup;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;
import com.haulmont.cuba.web.gui.components.WebLabel;
import com.haulmont.cuba.web.gui.components.WebLookupField;
import com.vaadin.data.HasValue;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.PersonDataService;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueFioDateException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class PersonMainDataEdit extends AbstractHrEditor<PersonExt> {

    @Inject
    public Datasource<PersonExt> personDs;
    @Inject
    private CollectionDatasource<Category, UUID> categoriesDs;
    @Inject
    private DataManager dataManager;
    @Inject
    private RuntimePropertiesFrame runtimeProperties;
    @Inject
    public CollectionDatasource<CompetenceElement, UUID> competenceElementsDs;
    @Named("fieldGroup.type")
    private PickerField<Entity> typeField;

    @WindowParam
    private String personTypeCode;
    @Inject
    private CommonService commonService;
    @Inject
    private PersonDataService personDataService;
    @Inject
    private BusinessRuleService businessRuleService;
    @Inject
    private FieldGroup fieldGroup;

    @WindowParam
    protected Datasource<AssignmentExt> assignmentDs;

    protected Boolean hireDateError = false;

    protected boolean positionStatusError = false;

    protected Field<Date> hireDate;

    protected Date initialHireDate;

//    protected HasValue.ValueChangeListener hireDateFieldChangeListener = new HasValue.ValueChangeListener() {
//        @Override
//        public void valueChanged(ValueChangeEvent e) {
//            if (personDataService.checkPositionForErrors((Date) e.getValue(), assignmentDs.getItem())) {
//                positionStatusError = true;
//            } else {
//                positionStatusError = false;
//            }
//
//            checkForHireDateError();
//
//            if (Objects.equals(e.getValue(), initialHireDate)) {
//                positionStatusError = false;
//                hireDateError = false;
//            }
//        }
//    };


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        hireDate = (Field) fieldGroup.getFieldNN("hireDate").getComponent();

        WebHBoxLayout webHBoxLayout = (WebHBoxLayout) runtimeProperties.getComponentNN("categoryFieldBox");
        WebLookupField<Object> originalCategoryField = (WebLookupField) webHBoxLayout.getComponentNN("categoryField");
        WebLabel categoryFieldLabel = (WebLabel) webHBoxLayout.getComponentNN("categoryFieldLabel");
        originalCategoryField.setVisible(false);
        categoryFieldLabel.setVisible(false);


        typeField.addValueChangeListener(e -> {
            DicPersonType personType = (DicPersonType) e.getValue();
            Category category = getCategory(personType.getCode());/* admin must create category with same name like personType code */
            originalCategoryField.setValue(category);

        });

        originalCategoryField.addValueChangeListener(e -> {
            Category category = (Category) e.getValue();
            if (category != null) {

                WebFieldGroup webFieldGroup = (WebFieldGroup) runtimeProperties.getComponents().stream().filter(c -> c instanceof WebFieldGroup).findFirst().orElse(null);
                if (webFieldGroup != null) {
                    for (FieldGroup.FieldConfig fieldConfig : webFieldGroup.getFields()) {
                        String caption = fieldConfig.getCaption();
                        fieldConfig.setCaption(getLocaleTranslate(caption, category));
                        fieldConfig.setWidth("250");
                    }
                }
            }
            categoriesDs.setItem(category);
            originalCategoryField.setValue(category);
        });

        setShowSaveNotification(false);
    }


    @SuppressWarnings("all")
    @Override
    protected void postInit() {
        initialHireDate = personDs.getItem().getHireDate();

        if (!PersistenceHelper.isNew(getItem())) {
            setCaption(getItem().getFioWithEmployeeNumber());
        }

        boolean isStudent = false;
        if (personTypeCode != null && personTypeCode.equalsIgnoreCase("student")) {
            isStudent = true;
        }
    }


    @Override
    protected void initNewItem(PersonExt item) {
        super.initNewItem(item);
        if (personTypeCode != null) {
            item.setType(getPersonTypeByCode(personTypeCode.toUpperCase()));
        } else {
            item.setType(getPersonTypeByCode("CANDIDATE"));
        }
    }


    @Override
    public void ready() {
        super.ready();
//        hireDate.addValueChangeListener(hireDateFieldChangeListener);
    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (hireDateError) {
            errors.add(getMessage("hireDateError"));
        }
        if (positionStatusError) {
            errors.add(businessRuleService.getBusinessRuleMessage("hireDate.check.position.active"));
        }
    }


    @Override
    protected boolean preCommit() {
//        hireDate.removeValueChangeListener(hireDateFieldChangeListener);
        if (hireDateError || positionStatusError) {
            return false;
        }
        return super.preCommit();
    }


    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        return super.postCommit(committed, close);
    }


    private DicPersonType getPersonTypeByCode(String code) {
        LoadContext<DicPersonType> loadContext = LoadContext.create(DicPersonType.class)
                .setQuery(LoadContext.createQuery(
                        "select e from tsadv$DicPersonType e " +
                                "where e.code = :code")
                        .setParameter("code", code));
        return dataManager.load(loadContext);
    }


    private Category getCategory(String code) {
        return commonService.getEntity(Category.class, "select e" +
                        "  from sys$Category e" +
                        "  where e.entityType = 'base$PersonExt'" +
                        "  and e.name =:name",
                ParamsMap.of("name", code), View.LOCAL);
    }

    @Override
    public void commitAndClose() {
        if (!validateAll()) {
            return;
        }

        try {
            hasCandidate(getItem());

            commitChanges();
        } catch (UniqueFioDateException ex) {
            showOptionDialog(getMessage("msg.warning.title"),
                    getMessage(ex.getMessage()),
                    MessageType.CONFIRMATION,
                    new DialogAction[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    commitChanges();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
        }
    }


    private String getLocaleTranslate(String caption, Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("caption", caption);
        map.put("categoryId", category.getId());
        CategoryAttribute categoryAttribute = commonService.getEntity(CategoryAttribute.class, "select e" +
                        "  from sys$CategoryAttribute e" +
                        "  where e.categoryEntityType = 'base$PersonExt'" +
                        "  and e.name =:caption and e.category.id = :categoryId",
                map, View.LOCAL);
        if (categoryAttribute != null) {
            return categoryAttribute.getLocaleName();
        }
        return null;
    }


    private void hasCandidate(PersonExt person) throws Exception {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);

        boolean isCreate = PersistenceHelper.isNew(person);

        StringBuilder query = new StringBuilder("select e from base$PersonExt e where (:sysDate between e.startDate and e.endDate)");

        LoadContext.Query loadContextQuery = LoadContext.createQuery("");

        String iin = person.getNationalIdentifier();

        boolean checkIin = false;
        if (iin != null && iin.trim().length() > 0 && !iin.equals("000000000000")) {
            checkIin = true;
            query.append(" and e.nationalIdentifier = :iin");
            loadContextQuery.setParameter("iin", iin);
        }

        if (isCreate && !checkIin) {
            query.append(" and e.firstName = :p1 and e.lastName = :p2 and e.dateOfBirth = :p3");
            loadContextQuery.setParameter("p1", person.getFirstName());
            loadContextQuery.setParameter("p2", person.getLastName());
            loadContextQuery.setParameter("p3", person.getDateOfBirth());
        }

        if (!isCreate) {
            query.append(" and e.id <> :pId");
            loadContextQuery.setParameter("pId", person.getId());
        }
        loadContextQuery.setParameter("sysDate", new Date());
        loadContextQuery.setQueryString(query.toString());
        loadContext.setQuery(loadContextQuery);

        if (dataManager.getCount(loadContext) > 0) {
            //if (checkIin) throw new UniqueIinException(getMessage("candidate.exist.iin"));

            /**
             * check unique FIO and dateOfBirth only create form
             * */
            if (isCreate) {
                throw new UniqueFioDateException(getMessage("candidate.exist.fid"));
            }
        }
    }


    private void commitChanges() {
        boolean changed = false;

        if (PersistenceHelper.isNew(getItem())) {
            changed = super.commit();
        } else {
            changed = true;


            dataManager.commit(getItem());

            postInit();
        }

        if (changed) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
            close("", true);
        }
    }


    private void checkForHireDateError() {
        Long assignmentExt = commonService.getCount(AssignmentExt.class,
                "select a from base$AssignmentExt a WHERE a.personGroupId.id = :personGroupId",
                ParamsMap.of("personGroupId", personDs.getItem().getGroup().getId()));
        if (assignmentExt > 1) {
            hireDateError = true;
        } else {
            hireDateError = false;
        }
    }
}
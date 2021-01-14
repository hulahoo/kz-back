package kz.uco.tsadv.web.ordermaster;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

import javax.inject.Inject;
import java.util.*;

public class OrderBuilder extends AbstractWindow {

    private OrderMasterDictionary orderMasterDictionary = AppBeans.get(OrderMasterDictionary.class);

    @Inject
    private Metadata metadata;
    @Inject
    private LookupPickerField personsLookup;
    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private VBoxLayout dynamicContent;

    @Inject
    private Label pageLabel;
    @Inject
    private Label pageNumber;
    @Inject
    private Button next;
    @Inject
    private Button finishButton;
    @Inject
    private Button previous;
    @Inject
    private Button cancelButton;
    @Inject
    private GridLayout builderVariables;
    @Inject
    private DateField dateField;
    @Inject
    private Datasource<PersonExt> personDs;
    @Inject
    private Datasource<PersonGroupExt> personGroupDs;
    @Inject
    private Datasource<AssignmentExt> assignmentDs;
    @Inject
    private Datasource<AssignmentGroupExt> assignmentGroupDs;
    private List<OrderMasterEntity> masterEntities;

    private int currentPage = -1;

    private EditableFrame editableFrame;
    @Inject
    private DataManager dataManager;
    private OrderMaster orderMaster;
    private String orderTypeCode;

    private PersonGroupExt mainPersonGroup = null;

    private boolean isHireOrder = false;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        this.orderMaster = (OrderMaster) params.get("orderMaster");
        if (orderMaster != null) {
            this.masterEntities = new ArrayList<>();
            this.masterEntities.addAll(orderMaster.getOrderMasterEntities());

            DicOrderType orderType = orderMaster.getOrderType();
            if (orderType != null) {
                orderTypeCode = orderType.getCode();

                builderVariables.setVisible(true);

                if (orderTypeCode != null) {
                    if (orderTypeCode.equalsIgnoreCase("HIRE")) {
                        isHireOrder = true;
                        builderVariables.setVisible(false);
                        currentPage = 0;
                    } else {
                        currentPage = -1;
                    }
                }
            }

            dateField.setResolution(DateField.Resolution.DAY);

//            personsLookup.addValueChangeListener(new ValueChangeListener() {
//                @Override
//                public void valueChanged(ValueChangeEvent e) {
//                    Object personValue = e.getValue();
//                    if (personValue != null) {
//                        mainPersonGroup = (PersonGroupExt) personValue;
//                    } else {
//                        mainPersonGroup = null;
//                    }
//
//                    for (Datasource ds : getDsContext().getAll()) {
//                        ((AbstractDatasource) ds).clearCommitLists();
//                    }
//
//                    personGroupDs.setItem(mainPersonGroup);
//
//                    if (mainPersonGroup != null) {
//                        AssignmentGroupExt assignmentGroup = getAssignmentGroup(mainPersonGroup.getId());
//                        if (assignmentGroup != null) {
//                            assignmentGroupDs.setItem(assignmentGroup);
//                        }
//                    }
//                }
//            });

            try {
                renderPage();
            } catch (Exception e) {
                showNotification(getMessage("msg.error.title"), e.getMessage(), NotificationType.TRAY);
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        if (editableFrame != null) {
            if (editableFrame.validateAll()) {
                showOptionDialog(getMessage("msg.success.title"),
                        getMessage("OrderBuilder.confirm.finish"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        getDsContext().commit();

                                        close("");
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO)
                        });
            }
        } else {
            showNotification("Editable frame is null!", NotificationType.TRAY);
        }
    }

    public void cancelChanges() {
        showOptionDialog(getMessage("msg.warning.title"),
                getMessage("OrderBuilder.confirm.reject"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                for (Datasource ds : getDsContext().getAll()) {
                                    ((AbstractDatasource) ds).clearCommitLists();
                                }

                                close("", true);
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });
    }

    private void renderPage() throws Exception {
        if (!masterEntities.isEmpty()) {
            int size = masterEntities.size();
            int activePage, from = isHireOrder ? size : size + 1;

            if (currentPage > -1) {
                OrderMasterEntity masterEntity = masterEntities.get(currentPage);
                renderEntity(masterEntity);
            }

            if (currentPage == -1) {
                pageLabel.setValue(getMessage("order.builder.start.page"));
                dynamicContent.removeAll();
            }

            previous.setEnabled(currentPage > (isHireOrder ? 0 : -1));
            next.setEnabled(currentPage < size - 1);

            finishButton.setVisible(currentPage == size - 1);
            builderVariables.setVisible(currentPage == -1 && !isHireOrder);

            if (currentPage == -1) {
                activePage = 1;
            } else {
                activePage = currentPage + (isHireOrder ? 1 : 2);
            }

            String pageNumberText = String.format(getMessage("order.builder.page.number"),
                    activePage,
                    from);

            pageNumber.setValue(pageNumberText);
        } else {
            builderVariables.setVisible(false);
            previous.setVisible(false);
            next.setVisible(false);
            finishButton.setVisible(false);

            pageLabel.setValue(getMessage("msg.error.title"));

            String pageNumberText = String.format(getMessage("order.builder.page.number"), 0, 0);
            pageNumber.setValue(pageNumberText);

            Label entitiesEmpty = componentsFactory.createComponent(Label.class);
            entitiesEmpty.setValue(getMessage("order.builder.empty.entities"));
            entitiesEmpty.setAlignment(Alignment.MIDDLE_CENTER);

            dynamicContent.removeAll();
            dynamicContent.add(entitiesEmpty);
        }
    }

    public void previous() {
        currentPage--;
        try {
            renderPage();
        } catch (Exception e) {
            showNotification(getMessage("msg.error.title"), e.getMessage(), NotificationType.TRAY);
            e.printStackTrace();
        }
    }

    public void next() {
        boolean showNext = true;

        if (currentPage == -1) {
            try {
                boolean validate = false;
                if (orderTypeCode != null && !orderTypeCode.equalsIgnoreCase("HIRE")) {
                    validate = true;
                }

                if (validate) {
                    if (mainPersonGroup == null) {
                        throw new Exception("order.builder.person.null");
                    }

                    dateField.validate();
                }
            } catch (Exception ex) {
                showNext = false;
                showNotification(getMessage("msg.warning.title"),
                        getMessage(ex.getMessage()),
                        NotificationType.TRAY);
            }
        } else {
            if (editableFrame != null) {
                if (!editableFrame.validateAll()) {
                    showNext = false;
                }
            }
        }

        if (showNext) {
            currentPage++;
            try {
                renderPage();
            } catch (Exception e) {
                currentPage--;
                showNotification(getMessage("msg.error.title"), e.getMessage(), NotificationType.TRAY);
                e.printStackTrace();
            }
        }
    }

    private void renderEntity(OrderMasterEntity masterEntity) throws Exception {
        String entityName = masterEntity.getEntityName();

        if (entityName != null) {
            List<OrderMasterEntityProperty> properties = masterEntity.getProperties();
            if (properties != null && !properties.isEmpty()) {
                OrderMasterDictionary.MetaClassModel metaClassModel = orderMasterDictionary.find(entityName);
                if (metaClassModel != null) {
                    editableFrame = (EditableFrame) openFrame(null, metaClassModel.getFrameName());
                    editableFrame.setSizeFull();

                    initInitialValues(entityName, editableFrame);

                    dynamicContent.removeAll();
                    dynamicContent.add(editableFrame);

                    pageLabel.setValue(masterEntity.getEntityLangName());
                }
            }
        }
    }

    private void initInitialValues(String entityName, EditableFrame editableFrame) throws Exception {
        switch (entityName) {
            case "base$PersonExt": {
                PersonGroupExt personGroup = personGroupDs.getItem();
                if (personGroup == null) {
                    personGroup = metadata.create(PersonGroupExt.class);

                    PersonExt person = metadata.create(PersonExt.class);
                    person.setStartDate(CommonUtils.getSystemDate());
                    person.setEndDate(CommonUtils.getEndOfTime());

                    List<PersonExt> personList = new ArrayList<>();
                    personList.add(person);
                    personGroup.setList(personList);
                    personGroupDs.setItem(personGroup);

                    person.setGroup(personGroup);
                    personDs.setItem(person);

                    mainPersonGroup = personGroup;
                }
                break;
            }

            case "base$AssignmentExt": {
                AssignmentExt assignment = assignmentDs.getItem();
                if (assignment == null) {
                    assignment = metadata.create(AssignmentExt.class);
                    assignment.setStartDate(CommonUtils.getSystemDate());
                    assignment.setEndDate(CommonUtils.getEndOfTime());
                    assignment.setPersonGroup(mainPersonGroup);

                    AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
                    List<AssignmentExt> assignments = new ArrayList<>();
                    assignments.add(assignment);
                    assignmentGroup.setList(assignments);
                    assignmentGroupDs.setItem(assignmentGroup);

                    assignment.setGroup(assignmentGroup);
                    assignmentDs.setItem(assignment);
                }
                break;
            }
            case "tsadv$PersonContact": {
                checkPersonGroup();
                break;
            }
            case "tsadv$PersonDocument": {
                checkPersonGroup();
                break;
            }
            case "tsadv$Address": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("startDate", dateField.getValue()));
                break;
            }
            case "tsadv$Beneficiary": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("dateFrom", dateField.getValue()));
                break;
            }
            case "tsadv$Case": {
                checkPersonGroup();

                break;
            }
            case "tsadv$Agreement": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("dateFrom", dateField.getValue()));
                break;
            }
            case "tsadv$PersonExperience": {
                checkPersonGroup();

                break;
            }
            case "tsadv$Dismissal": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("dismissalDate", dateField.getValue()));
                break;
            }
            case "tsadv$TradeUnion": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("joingTradeUnion", dateField.getValue()));
                break;
            }
            case "tsadv$Salary": {
                checkAssignment();

                editableFrame.setInitialValues(Collections.singletonMap("startDate", dateField.getValue()));
                break;
            }
            case "tsadv$SurCharge": {
                checkAssignment();

                editableFrame.setInitialValues(Collections.singletonMap("dateFrom", dateField.getValue()));
                break;
            }
            case "tsadv$PersonEducation": {
                checkPersonGroup();

                break;
            }
            case "tsadv$Absence": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("dateFrom", dateField.getValue()));
                break;
            }
            case "tsadv$AbsenceBalance": {
                checkPersonGroup();
                break;
            }
            case "tsadv$BusinessTrip": {
                checkPersonGroup();

                editableFrame.setInitialValues(Collections.singletonMap("dateFrom", dateField.getValue()));
                break;
            }
        }
    }

    private void checkAssignment() throws Exception {
        AssignmentGroupExt assignmentGroup = assignmentGroupDs.getItem();
        if (assignmentGroup == null || assignmentGroup.getAssignment() == null) {
            throw new Exception("Assignment is null!");
        }
    }

    private void checkPersonGroup() throws Exception {
        PersonGroupExt personGroup = personGroupDs.getItem();
        if (personGroup == null || personGroup.getPerson() == null) {
            throw new Exception("PersonGroup is null!");
        }
    }

    private AssignmentGroupExt getAssignmentGroup(UUID personGroupId) {
        LoadContext<AssignmentGroupExt> loadContext = LoadContext.create(AssignmentGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentGroupExt e join e.list a " +
                        "where :sysDate between a.startDate and a.endDate " +
                        "and a.personGroupId.id = :personGroupId")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignmentGroup.master");
        return dataManager.load(loadContext);
    }
}
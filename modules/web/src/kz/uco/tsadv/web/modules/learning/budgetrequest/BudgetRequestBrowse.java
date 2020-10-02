package kz.uco.tsadv.web.modules.learning.budgetrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.datasource.BudgetDatasource;
import kz.uco.tsadv.modules.learning.model.BudgetHeader;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.BudgetRequestItem;
import kz.uco.tsadv.modules.learning.model.dto.BudgetRequestItemsRowDto;
import kz.uco.tsadv.modules.performance.enums.BudgetRequestItemEnum;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;
import kz.uco.tsadv.service.BudgetService;
import kz.uco.tsadv.service.DatesService;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;


public class BudgetRequestBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<BudgetRequestItem, UUID> budgetRequestItemsDs;
    @Inject
    protected GroupDatasource<BudgetRequest, UUID> budgetRequestsDs;
    @Inject
    protected CollectionDatasource<DicCostType, UUID> dicCostTypeDs;
    @Named("budgetRequestsTable.create")
    protected CreateAction budgetRequestsTableCreate;
    @Named("budgetRequestsTable.copy")
    protected Action budgetRequestsTableCopy;
    @Named("itemsTable.fillYear")
    protected Action itemsTableFillYear;
    @Inject
    protected BudgetService budgetService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected BudgetDatasource budgetCustomDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Table<BudgetRequestItemsRowDto> itemsTable;
    @Inject
    protected DatesService datesService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        BudgetHeader budgetHeader = (BudgetHeader) params.get("budgetHeader");
        if (budgetHeader != null) {
            budgetRequestsTableCreate.setInitialValues(ParamsMap.of("budgetHeader", budgetHeader, "budget", budgetHeader.getBudget()));
        }
        dicCostTypeDs.refresh();

        budgetRequestsDs.addItemChangeListener(e -> {
            BudgetRequest budgetRequest = e.getItem();
            if (budgetRequest != null) {
                budgetRequestsTableCopy.setEnabled(true);
                budgetCustomDs.refresh(ParamsMap.of("budgetRequest", budgetRequest, "dicCostTypeDs", dicCostTypeDs));
            } else {
                budgetCustomDs.clear();
                budgetRequestsTableCopy.setEnabled(false);
            }
        });

        generateColumns();
        budgetRequestsTableCopy.setEnabled(false);
        BudgetRequestItemEnum busTripCountEnum = BudgetRequestItemEnum.BUS_TRIP_COUNT;

        budgetCustomDs.addItemChangeListener(e -> {
            if (e.getItem() != null && !e.getItem().getName().equals(messages.getMessage(busTripCountEnum))) {
                itemsTableFillYear.setEnabled(true);
            } else {
                itemsTableFillYear.setEnabled(false);
            }
        });
        itemsTableFillYear.setEnabled(false);
    }

    protected void generateColumns() {

        itemsTable.addGeneratedColumn("name", entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            String name = entity.getName();
            label.setValue(name);
            if (entity.getName().equals(messages.getMessage(BudgetRequestItemEnum.ALL_AMOUNT)) || entity.getName().equals(messages.getMessage(BudgetRequestItemEnum.BUS_TRIP_AMOUNT))) {
                label.setHtmlEnabled(true);
                label.setValue("<b style= \"color: blue\">" + name + "</b>");
            }
            return label;
        });

        for (int i = 0; i < 12; i++) {
            int finalI = i;

            itemsTable.addGeneratedColumn("i" + finalI, entity -> {
                if (!entity.getName().equals(messages.getMessage(BudgetRequestItemEnum.ALL_AMOUNT))
                        && !entity.getName().equals(messages.getMessage(BudgetRequestItemEnum.BUS_TRIP_AMOUNT))) {

                    TextField field = componentsFactory.createComponent(TextField.class);
                    field.setDatatype(Datatypes.get(DoubleDatatype.class));
                    List<BudgetRequestItem> list = entity.getList();
                    BudgetRequestItem budgetRequestItem = list.get(finalI);
                    Double amount = budgetRequestItem.getAmount();
                    field.setValue(amount);

                    field.addValueChangeListener(getFieldValueChangeListener(finalI, entity, field, budgetRequestItem));
                    field.setWidth("100%");
                    return field;
                } else {
                    Label label = componentsFactory.createComponent(Label.class);
                    List<BudgetRequestItem> list = entity.getList();
                    BudgetRequestItem budgetRequestItem = list.get(finalI);
                    Double amount = budgetRequestItem.getAmount();
                    label.setHtmlEnabled(true);
                    label.setValue("<b style= \"color: blue\">" + (new BigDecimal(amount.toString())).toPlainString() + "</b>");
                    label.setWidth("100px");
                    return label;
                }
            });
        }
        List<Date> months = datesService.getAllMonths(new Date());

        for (int i = 0; i < 12; i++) {
            String str1 = "MMM";
            Date d = months.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat(str1, userSessionSource.getUserSession().getLocale());
            itemsTable.getColumn("i" + i).setCaption(sdf.format(d));
        }

        itemsTable.addGeneratedColumn("columnsSum", entity -> {
            BudgetRequestItemsRowDto personCountDto = budgetCustomDs.getItems().stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.PERSON_COUNT))).findFirst().orElse(null);
            BudgetRequestItemsRowDto busTripPersonCountDto = budgetCustomDs.getItems().stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.BUS_TRIP_COUNT))).findFirst().orElse(null);
            List<BudgetRequestItem> personCountDtoList = personCountDto.getList();
            List<BudgetRequestItem> busTripPersonCountDtoList = busTripPersonCountDto.getList();

            double amount = 0d;
            if (entity.getDicCostType() != null) {
                for (int i = 0; i < 12; i++) {
                    BudgetRequestItem takeCountFrom;

                    if (entity.getDicCostType().getIsBusinessTrip() == null || !entity.getDicCostType().getIsBusinessTrip()) {
                        takeCountFrom = personCountDtoList.get(i);
                    } else {
                        takeCountFrom = busTripPersonCountDtoList.get(i);
                    }

                    double itemValue = 0d;
                    if (entity.getList().get(i) != null && entity.getList().get(i).getAmount() != null) {
                        itemValue = entity.getList().get(i).getAmount();
                    }
                    if (takeCountFrom != null && takeCountFrom.getAmount() != null && takeCountFrom.getAmount() > 0) {
                        amount += (itemValue) * takeCountFrom.getAmount();
                    } else {
                        amount += itemValue;
                    }
                }
            } else {
                amount = entity.getList().stream().mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0).sum();
            }
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue((new BigDecimal(Double.valueOf(amount).toString())).toPlainString());
            label.setWidth("100px");
            return label;
        });
        itemsTable.getColumn("columnsSum").setCaption(getMessage("total"));
    }

    protected Consumer<HasValue.ValueChangeEvent<String>> getFieldValueChangeListener(int i, BudgetRequestItemsRowDto entity, TextField field, BudgetRequestItem budgetRequestItem) {
        return e -> {
            try {
                String s = String.valueOf(e.getValue());
                s = s.replace(" ", "");
                Double amount1 = Double.parseDouble(s);
                budgetRequestItem.setAmount(amount1);
                dataManager.commit(budgetRequestItem);
            } catch (NumberFormatException ex) {
                field.setValue(0d);
            }
//            budgetCustomDs.refresh();
        };
    }


    public void updateDs() {
        budgetCustomDs.refresh();
    }

    public void copy() {
        CommitContext commitContext = new CommitContext();

        BudgetRequest budgetRequest = budgetRequestsDs.getItem();
        BudgetRequest copyBR = metadata.getTools().deepCopy(budgetRequest);
        copyBR.setId(UUID.randomUUID());
        budgetRequest = dataManager.reload(budgetRequest, "budgetRequest-with-items");
        copyBR.setBudgetHeader(budgetRequest.getBudgetHeader());
        copyBR.setBudget(budgetRequest.getBudget());
        copyBR.setInitiatorPersonGroup(budgetRequest.getInitiatorPersonGroup());


        List<BudgetRequestItem> budgetRequestItems = budgetRequest.getBudgetRequestItems();
        List<BudgetRequestItem> budgetRequestItemsCopy = new ArrayList<>();
        budgetRequestItems.forEach(budgetRequestItem -> {
            BudgetRequestItem budgetRequestItemCopy = metadata.getTools().deepCopy(budgetRequestItem);
            budgetRequestItemCopy.setId(UUID.randomUUID());
            budgetRequestItemCopy.setName(budgetRequestItem.getName());
            budgetRequestItemCopy.setBudgetRequest(copyBR);
            budgetRequestItemsCopy.add(budgetRequestItemCopy);
            commitContext.addInstanceToCommit(budgetRequestItemCopy);
        });

        copyBR.setBudgetRequestItems(budgetRequestItemsCopy);

        commitContext.addInstanceToCommit(copyBR);

        dataManager.commit(commitContext);
        budgetRequestsDs.refresh();
    }

    public void fillYear() {
        BudgetRequestItemsRowDto dto = budgetCustomDs.getItem();

        BudgetRequestItem firstItem = null;
        int startPosition = 0;

        for (int i = 0; i < 12; i++) {
            firstItem = dto.getList().get(i);
            if (firstItem != null && firstItem.getAmount() != null) {
                startPosition = i;
                break;
            }

        }

        if (firstItem != null && firstItem.getAmount() != null) {
            budgetCustomDs.refresh();
            CommitContext commitContext = new CommitContext();
            for (int i = startPosition + 1; i < 12; i++) {
                BudgetRequestItem item = dto.getList().get(i);
                item.setAmount(firstItem.getAmount());
                commitContext.addInstanceToCommit(item);
            }
            dataManager.commit(commitContext);
            budgetCustomDs.refresh();
        }

    }

    public void repaint() {
        itemsTable.repaint();
    }
}
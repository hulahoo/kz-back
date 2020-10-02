package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.BudgetRequestItem;
import kz.uco.tsadv.modules.learning.model.dto.BudgetRequestItemsRowDto;
import kz.uco.tsadv.modules.performance.enums.BudgetRequestItemEnum;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service(BudgetService.NAME)
public class BudgetServiceBean implements BudgetService {
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DatesService datesService;
    @Inject
    protected Messages messages;


    @Override
    public Collection<BudgetRequestItemsRowDto> getItems(BudgetRequest budgetRequest, Collection<DicCostType> dicCostTypes) {
        ArrayList<BudgetRequestItemsRowDto> rowsDto = new ArrayList<>();
        if (budgetRequest == null) {
            return null;
        }
        List<Date> months = datesService.getAllMonths(budgetRequest.getBudget().getBudgetStartDate());

        List<BudgetRequestItemsRowDto> hardcodeRows = generateHardcodeRows(budgetRequest, months);
        rowsDto.addAll(hardcodeRows);

        if (dicCostTypes != null && !dicCostTypes.isEmpty()) {
            for (DicCostType dicCostType : dicCostTypes) {
                BudgetRequestItemsRowDto rowDto = new BudgetRequestItemsRowDto();

                ArrayList<BudgetRequestItem> list = new ArrayList<>();

                List<BudgetRequestItem> existedItems = getBudgetRequestItems(budgetRequest, dicCostType);
                for (Date month : months) {
                    BudgetRequestItem monthItem = existedItems.stream().filter(e -> e.getFirstDayOfMonth().equals(month)).findAny().orElse(null);
                    if (monthItem == null) {
                        monthItem = metadata.create(BudgetRequestItem.class);
                        monthItem.setBudgetRequest(budgetRequest);
                        monthItem.setName(dicCostType.getInstanceName());
                        monthItem.setBudgetItem(dicCostType);
                        monthItem.setFirstDayOfMonth(month);
                    }
                    list.add(monthItem);
                }
                rowDto.setList(list);
                rowDto.setName(dicCostType.getInstanceName());
                rowDto.setDicCostType(dicCostType);
                rowsDto.add(rowDto);
            }

        }
        List<BudgetRequestItemsRowDto> budgetRequestItemsRowDtos = calculateAmounts(rowsDto);

        return budgetRequestItemsRowDtos;
    }

    protected List<BudgetRequestItemsRowDto> generateHardcodeRows(BudgetRequest budgetRequest, List<Date> months) {
        ArrayList<BudgetRequestItemsRowDto> rowsDto = new ArrayList<>();

        for (BudgetRequestItemEnum rowEnum : BudgetRequestItemEnum.values()) {
            BudgetRequestItemsRowDto rowDto = new BudgetRequestItemsRowDto();
            ArrayList<BudgetRequestItem> list = new ArrayList<>();
            List<BudgetRequestItem> existedItems = getBudgetRequestItemsByEnum(budgetRequest, rowEnum);

            for (Date month : months) {
                BudgetRequestItem monthItem = existedItems.stream().filter(e -> e.getFirstDayOfMonth().equals(month)).findAny().orElse(null);
                if (monthItem == null) {
                    monthItem = metadata.create(BudgetRequestItem.class);
                    monthItem.setBudgetRequest(budgetRequest);
                    monthItem.setName(rowEnum.name());
                    monthItem.setFirstDayOfMonth(month);
                }
                list.add(monthItem);
            }
            rowDto.setList(list);
            rowDto.setName(messages.getMessage(rowEnum));
            rowsDto.add(rowDto);
        }
        return rowsDto;
    }

    protected List<BudgetRequestItemsRowDto> calculateAmounts(List<BudgetRequestItemsRowDto> dtos) {
        ArrayList<BudgetRequestItemsRowDto> list = new ArrayList<>();
        BudgetRequestItemsRowDto allAmountDto = dtos.stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.ALL_AMOUNT))).findFirst().orElseGet(null);
        BudgetRequestItemsRowDto busTripAmountDto = dtos.stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.BUS_TRIP_AMOUNT))).findFirst().orElseGet(null);
        BudgetRequestItemsRowDto personCountDto = dtos.stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.PERSON_COUNT))).findFirst().orElseGet(null);
        BudgetRequestItemsRowDto busTripPersonCountDto = dtos.stream().filter(rowDto -> rowDto.getName().equals(messages.getMessage(BudgetRequestItemEnum.BUS_TRIP_COUNT))).findFirst().orElseGet(null);
        List<BudgetRequestItemsRowDto> usualDataDtos = dtos.stream().filter(dto -> dto.getDicCostType() != null).collect(Collectors.toList());

        usualDataDtos.forEach(rowDto -> rowDto.setName(rowDto.getName() + messages.getMessage(messages.getMainMessagePack(), "currencyShort")));

        ArrayList<Double> allAmountValues = new ArrayList<>(11);
        ArrayList<Double> busTripAmountValues = new ArrayList<>(11);
        for (int i = 0; i < 12; i++) {
            allAmountValues.add(0d);
            busTripAmountValues.add(0d);
        }

        for (BudgetRequestItemsRowDto usualDataDto : usualDataDtos) {
            List<BudgetRequestItem> usualList = usualDataDto.getList();
            List<BudgetRequestItem> personCountList = personCountDto.getList();
            List<BudgetRequestItem> busTripCountList = busTripPersonCountDto.getList();
            for (int i = 0; i < 12; i++) {

                BudgetRequestItem usualItem = usualList.get(i);
                double value = usualItem != null ? (usualItem.getAmount() != null ? usualItem.getAmount() : 0) : 0;



                if (usualDataDto.getDicCostType().getIsBusinessTrip() != null && usualDataDto.getDicCostType().getIsBusinessTrip()) {
                    BudgetRequestItem busTripCountItem = busTripCountList.get(i);
                    double busTripCountValue = busTripCountItem != null ? (busTripCountItem.getAmount() != null ? busTripCountItem.getAmount() : 0) : 0;
                    busTripAmountValues.set(i, busTripAmountValues.get(i) + value * busTripCountValue);
                }else {
                    BudgetRequestItem personCountItem = personCountList.get(i);
                    double personCountValue = personCountItem != null ? (personCountItem.getAmount() != null ? personCountItem.getAmount() : 0) : 0;
                    allAmountValues.set(i, allAmountValues.get(i) + value * personCountValue);
                }

            }

        }

        List<BudgetRequestItem> allAmountDtoList = allAmountDto.getList();
        List<BudgetRequestItem> busTripAmountDtoList = busTripAmountDto.getList();

        for (int i = 0; i < 12; i++) {
            allAmountDtoList.get(i).setAmount(allAmountValues.get(i)+busTripAmountValues.get(i));
            busTripAmountDtoList.get(i).setAmount(busTripAmountValues.get(i));
        }

        list.add(allAmountDto);
        list.add(busTripAmountDto);
        list.add(personCountDto);
        list.add(busTripPersonCountDto);
        list.addAll(usualDataDtos);

        return list;
    }

    protected List<BudgetRequestItem> getBudgetRequestItems(BudgetRequest budgetRequest, DicCostType dicCostType) {
        return commonService.getEntities(BudgetRequestItem.class,
                "select e from tsadv$BudgetRequestItem e where e.budgetRequest.id =:budgetRequestId" +
                        " and e.budgetItem.id = :dicCostTypeId",
                ParamsMap.of("budgetRequestId", budgetRequest.getId(), "dicCostTypeId", dicCostType.getId()), View.LOCAL);
    }

    protected List<BudgetRequestItem> getBudgetRequestItemsByEnum(BudgetRequest budgetRequest, BudgetRequestItemEnum code) {
        return commonService.getEntities(BudgetRequestItem.class,
                "select e from tsadv$BudgetRequestItem e where e.budgetRequest.id =:budgetRequestId" +
                        " and e.name = :code",
                ParamsMap.of("budgetRequestId", budgetRequest.getId(), "code", code), View.LOCAL);
    }
}
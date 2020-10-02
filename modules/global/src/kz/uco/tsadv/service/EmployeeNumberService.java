package kz.uco.tsadv.service;


import kz.uco.tsadv.entity.generator.GeneratorEmployeeNumber;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.annotation.Nullable;
import java.util.UUID;

public interface EmployeeNumberService {
    String NAME = "tsadv_EmployeeNumberService";

    String generateEmployeeNumber(DicPersonType type);

    boolean hasEmployeeNumber(String employeeNumber);

    boolean hasEmployeeNumber(String employeeNumber, PersonExt personExt);

    String getRegularExpressionForEmployeeNumber(GeneratorEmployeeNumber generatorEmployeeNumber);

    String getRegularExpressionForEmployeeNumber(DicPersonType type);

    GeneratorEmployeeNumber getGeneratorEmployeeNumber(DicPersonType type);

    String increaseAssignmentNumber(String previousAssignmentNumber);

    String getLastAssignmentNumber(PersonGroupExt personGroup);

    Long generateNextRequestNumber();

    boolean hasRequestNumber(Long requestNumber, UUID entityId);

    String getConvertedEmployeeNumber(@Nullable String employeeNumber);
}
package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;

public interface AssignmentService {
    String NAME = "tsadv_AssignmentService";

    AssignmentExt getAssignment(UUID personGroupId, String view);

    AssignmentExt getAssignment(@Nonnull String login, @Nullable String view);

    /**
     * Проверяет является ли назначение определенного сотрудника повторным.
     *
     * @param currentAssignmentFirst - первый элемент в истории текущего назнаяения
     * @return true, если на текущую дату у сотрудника имеется увольнение и его дата раньше чем дата текущего назначения,
     * соответственно назначение является повторным.
     */
    boolean isReHire(AssignmentExt currentAssignmentFirst);

    boolean isNationalIdentifierDuplicate(String nationalIdentifier);

    boolean isTransferInFutureExists(PersonGroupExt personGroup, Date checkDate);

    /**
     * Используется при создании увольнения.
     * Проверяет существуют ли у данного сотрудника в текущем назначении увольнения в будущем.
     *
     * @param dismissal - объект создаваемого увольнения.
     * @param checkDate - дата, после которой проверяется наличие увольнений.
     * @return true если увольнения в будущем имеются.
     */
    boolean isDismissalInFutureExists(Dismissal dismissal, Date checkDate);

    void notifyTemporaryEndDate();

    /**
     * Возвращает Id штатной единицы на которую назначен заданное лицо (работник)
     * Назначение берётся основное на заданный момент времени с заданным статусом
     *
     * @param personGroupId        Id лица (работника)
     * @param moment               Момент времени (в машине времени)
     * @param assignmentStatusCode Код статуса назначения
     */
    UUID getPrimaryCurrentPositionGroupIdForPersonGroupId(
            UUID personGroupId,
            Date moment,
            String assignmentStatusCode
    );

    /**
     * Возвращает Id штатной единицы на которую назначен заданное лицо (работник)
     * Назначение берётся основное, на текущий момент времени (в машине времени) выбранный пользователем в системе
     * с Активным статусом назначения
     *
     * @param personGroupId Id лица (работника)
     */
    UUID getPrimaryCurrentPositionGroupIdForPersonGroupId(UUID personGroupId);
}
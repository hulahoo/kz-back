package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionExt;

import java.util.List;

public interface PositionService {
    String NAME = "tsadv_PositionService";


    /**
     * Проверяет на наличие и возвращает должности штатной единицы, даты закрытия которых ранее
     * даты закрытия данной штатной единицы.
     *
     * @param position - штатная единица
     * @return список должностей, если таковые существуют
     */
    List<Job> getExistingJobsInactiveInNearFuture(PositionExt position);

/*    String generateNextRequestNumber();

    boolean hasRequestNumber(String requestNumber);

    boolean hasRequestNumber(String requestNumber, UUID id);*/
}
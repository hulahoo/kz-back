package kz.uco.tsadv.service;


import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PositionExt;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface PositionService {
    String NAME = "tsadv_PositionService";

    PositionExt getPosition(@Nullable View view);

    PositionExt getPosition(UUID userId, @Nullable View view);

    /**
     * Проверяет на наличие и возвращает должности штатной единицы, даты закрытия которых ранее
     * даты закрытия данной штатной единицы.
     *
     * @param position - штатная единица
     * @return список должностей, если таковые существуют
     */
    List<Job> getExistingJobsInactiveInNearFuture(PositionExt position);

    PositionGroupExt getManager(UUID positionGroupId);
}
package kz.uco.tsadv.modules.learning.bean;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Learner;
import kz.uco.tsadv.modules.learning.model.LearnerGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Помощник по работе с Учащимися
 *
 * @author Felix Kamalov
 */
@Component(LearnerHelper.NAME)
public class LearnerHelper {
    public static final String NAME = "tsadv_LearnerHelper";

    /**
     * Добавляет учащегося в группу если его там не было
     * не комитит в БД
     */
    @Nullable
    public Learner addIfNotExists(
            @Nonnull LearnerGroup learnerGroup,
            @Nonnull PersonGroupExt personGroup
    ) {
        Learner existingLearner = search(learnerGroup, personGroup);
        if (existingLearner != null) {
        } else {
            Learner newLearner = metadata.create(Learner.class);
            newLearner.setGroup(learnerGroup);
            newLearner.setPersonGroup(personGroup);
            return newLearner;
        }
        return null;
    }


    /**
     * Ищет заданное лицо в списке учащихся группы
     */
    @Nullable
    public Learner search(
            @Nonnull LearnerGroup learnerGroup,
            @Nonnull PersonGroupExt personGroup
    ) {
        return commonService.getEntity(
                Learner.class,
                "" +
                        "select l " +
                        "  from tsadv$Learner l " +
                        " where l.group.id = :learnerGroup " +
                        "   and l.personGroup.id = :personGroup ",
                ParamsMap.of(
                        "learnerGroup", learnerGroup,
                        "personGroup", personGroup),
                "learner-edit"
        );
    }

    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;

}

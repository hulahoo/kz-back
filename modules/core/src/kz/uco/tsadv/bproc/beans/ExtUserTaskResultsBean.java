package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.core.UserTaskResultsBean;
import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.data.OutcomesContainer;

import java.util.Comparator;

/**
 * @author Alibek Berdaulet
 */
public class ExtUserTaskResultsBean extends UserTaskResultsBean {

    @Override
    public boolean containsOutcome(OutcomesContainer outcomesContainer, String outcomeId) {
        if (outcomesContainer == null || outcomesContainer.getOutcomes() == null) return false;

        return outcomesContainer.getOutcomes().stream().max(Comparator.comparing(Outcome::getDate))
                .filter(outcome -> outcomeId.equals(outcome.getOutcomeId()))
                .isPresent();
    }
}

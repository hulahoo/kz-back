package kz.uco.tsadv.web.modules.performance.goal;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.Goal;

import javax.inject.Inject;

@UiController("tsadv$Goal.browse")
@UiDescriptor("goal-browse.xml")
@LookupComponent("goalsTable")
@LoadDataBeforeShow
public class GoalBrowse extends StandardLookup<Goal> {
    @Inject
    protected CollectionLoader<Goal> goalsDl;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (event != null) {
            try {
                MapScreenOptions mapScreenOptions = (MapScreenOptions) event.getOptions();
                if (mapScreenOptions.getParams().containsKey("library")) {
                    goalsDl.setQuery("select e from tsadv$Goal e " +
                            " where e.library = :library ");
                    goalsDl.setParameter("library", mapScreenOptions.getParams().get("library"));
                    goalsDl.load();
                }
            } catch (Exception ignored) {
            }
        }
    }


}
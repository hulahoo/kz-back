package kz.uco.tsadv.web.modules.performance.goal;

import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.base.cuba.actions.EditActionExt;
import kz.uco.tsadv.modules.performance.model.Goal;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv$Goal.browse")
@UiDescriptor("goal-browse.xml")
@LookupComponent("goalsTable")
@LoadDataBeforeShow
public class GoalBrowse extends StandardLookup<Goal> {
    @Inject
    protected CollectionLoader<Goal> goalsDl;
    @Named("goalsTable.create")
    private CreateActionExt goalsTableCreate;
    @Named("goalsTable.edit")
    private EditActionExt goalsTableEdit;

    @Subscribe
    protected void onInit(InitEvent event) {
        reloadGoals(event);
        goalsTableCreate.setAfterCloseHandler(afterCloseHandler -> {
            reloadGoals(event);
        });
        goalsTableEdit.setAfterCloseHandler(afterCloseEvent -> {
            reloadGoals(event);
        });

    }

    private void reloadGoals(InitEvent event){
        if (event != null) {
            try {
                MapScreenOptions mapScreenOptions = (MapScreenOptions) event.getOptions();
                if (mapScreenOptions.getParams().containsKey("library")) {
                    goalsDl.setQuery("select e from tsadv$Goal e " +
                            " where e.library = :library ");
                    goalsDl.setParameter("library", mapScreenOptions.getParams().get("library"));
                    goalsDl.load();
                }else{
                    goalsDl.setQuery("select e from tsadv$Goal e");
                    goalsDl.load();
                }
            } catch (Exception ignored) {
            }
        }else{
            goalsDl.setQuery("select e from tsadv$Goal e");
            goalsDl.load();
        }

    }


}
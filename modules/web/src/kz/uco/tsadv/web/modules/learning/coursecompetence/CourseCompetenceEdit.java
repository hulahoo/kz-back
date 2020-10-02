package kz.uco.tsadv.web.modules.learning.coursecompetence;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.tsadv.modules.learning.model.CourseCompetence;

import javax.inject.Inject;
import java.util.UUID;

public class CourseCompetenceEdit extends AbstractEditor<CourseCompetence> {

    @Inject
    private Datasource<CourseCompetence> courseCompetenceDs;

    @Inject
    private CollectionDatasource<ScaleLevel, UUID> scaleLevelsDs;

    @Override
    protected void postInit() {
        courseCompetenceDs.addItemPropertyChangeListener((e) ->
        {
            if ("competenceGroup".equals(e.getProperty()))
                if (courseCompetenceDs.getItem().getScaleLevel() != null
                        && (courseCompetenceDs.getItem().getCompetenceGroup().getCompetence() == null
                        || courseCompetenceDs.getItem().getCompetenceGroup().getCompetence().getScale() == null
                        || !courseCompetenceDs.getItem().getCompetenceGroup().getCompetence().getScale().getId().equals(courseCompetenceDs.getItem().getScaleLevel().getScale().getId())))
                    courseCompetenceDs.getItem().setScaleLevel(null);
            scaleLevelsDs.refresh();
        });

    }
}
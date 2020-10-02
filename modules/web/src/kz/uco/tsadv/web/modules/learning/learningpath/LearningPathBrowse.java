package kz.uco.tsadv.web.modules.learning.learningpath;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import kz.uco.tsadv.modules.learning.model.LearningPath;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;

public class LearningPathBrowse extends AbstractLookup {

    @Inject
    private GroupTable<LearningPath> learningPathsTable;

    @Override
    public void ready() {
        super.ready();

        learningPathsTable.getColumn("category").setWidth(65);
    }

    public Component generateCategoryImageCell(LearningPath learningPath) {
        return Utils.getCourseCategoryImageEmbedded(learningPath.getCategory(), "50px", null);
    }
}
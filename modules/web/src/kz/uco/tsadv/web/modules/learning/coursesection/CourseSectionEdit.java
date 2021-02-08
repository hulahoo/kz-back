package kz.uco.tsadv.web.modules.learning.coursesection;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningObjectType;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.CourseSectionObject;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat.*;

public class CourseSectionEdit extends AbstractEditor<CourseSection> {

    @Inject
    protected Metadata metadata;

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected CollectionDatasource<CourseSectionSession, UUID> sessionDs;

    @Inject
    protected Table<CourseSectionSession> sectionSessionTable;
    @Inject
    protected CollectionDatasource<DicCourseFormat, UUID> formatDs;

    @Inject
    protected CollectionDatasource<DicLearningObjectType, UUID> objectTypeDs;

    @Inject
    protected LookupField objectType;

    @Inject
    protected GroupBoxLayout sessionGroupBox;

    @Inject
    protected PickerField content;

    @Inject
    protected PickerField test;

//    @Inject
//    protected Label contentLabel;

    @Inject
    protected Label testLabel;

    @Inject
    protected GridLayout onlineGrid;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        content.removeAction(PickerField.OpenAction.NAME);
        test.removeAction(PickerField.OpenAction.NAME);

        formatDs.addItemChangeListener(e -> {
            if (e.getItem() != null && e.getItem().getCode() != null) {
                visibleComponent(e.getItem().getCode());
            }
        });

        objectTypeDs.addItemChangeListener(e -> {
            if (e.getItem() != null && e.getItem().getCode() != null) {
                visibleTestContent(e.getItem().getCode().equalsIgnoreCase("test"));
            }
        });

        visibleComponent(null);
    }

    @Override
    protected boolean preCommit() {
        if (getItem().getFormat().getCode() != null &&
                !getItem().getFormat().getCode().equalsIgnoreCase(OFFLINE)) {
            List<CourseSectionSession> removeSessions = new ArrayList<>(sessionDs.getItems());
            if (!removeSessions.isEmpty()) {
                removeSessions.forEach(courseSectionSession -> sessionDs.removeItem(courseSectionSession));
            }
        }
        return super.preCommit();
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (objectTypeDs.getItem() == null) {
//            content.setVisible(false);
            test.setVisible(false);

//            contentLabel.setVisible(false);
            testLabel.setVisible(false);

//            content.setRequired(false);
            test.setRequired(false);
        }
    }

    protected void visibleTestContent(boolean showTest) {
//        content.setVisible(!showTest);
        test.setVisible(showTest);

//        contentLabel.setVisible(!showTest);
        testLabel.setVisible(showTest);

//        content.setRequired(!showTest);
        test.setRequired(showTest);
    }

    protected void visibleComponent(String formatCode) {
        onlineGrid.setVisible(formatCode != null &&
                (formatCode.equalsIgnoreCase(ONLINE) ||
                        formatCode.equalsIgnoreCase(WEBINAR)
                )
        );
        objectType.setRequired(onlineGrid.isVisible());
        sessionGroupBox.setVisible(formatCode != null && formatCode.equalsIgnoreCase(OFFLINE));
    }

    @Override
    protected void initNewItem(CourseSection item) {
        super.initNewItem(item);

        CourseSectionObject courseSectionObject = metadata.create(CourseSectionObject.class);
        item.setSectionObject(courseSectionObject);
    }
}
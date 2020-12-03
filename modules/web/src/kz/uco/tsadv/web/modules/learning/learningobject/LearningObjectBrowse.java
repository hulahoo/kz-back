package kz.uco.tsadv.web.modules.learning.learningobject;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.modules.learning.model.CourseSectionObject;
import kz.uco.tsadv.modules.learning.model.LearningObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LearningObjectBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<LearningObject, UUID> learningObjectsDs;

    @Inject
    protected GroupTable<LearningObject> learningObjectsTable;

    @Inject
    protected CommonService commonService;
    @Inject
    protected CommonConfig commonConfig;
    @Inject
    protected ComponentsFactory componentsFactory;

    @Override
    public void ready() {
        if (!commonConfig.getScormEnabled()) {
            List<Table.Column> removeColumn = new ArrayList<>();
            for (Table.Column column : learningObjectsTable.getColumns()) {
                if ("contentType".equals(column.getId().toString())
                        || "file".equals(column.getId().toString())) {
                    removeColumn.add(column);
                }
            }
            for (Table.Column column : removeColumn) {
                learningObjectsTable.removeColumn(column);
            }
        }
        super.ready();
    }

    // Для переноса значения на строки ниже
    public Component generateObjectNameCell(LearningObject entity) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(entity.getObjectName());
        label.setHeightAuto();
        label.setWidthFull();
        return label;
    }

    public void removeItem() {
        if (isInUse(learningObjectsDs.getItem())) {
            showNotification(getMessage("attention"), getMessage("section.has.this.object"), NotificationType.TRAY);
        } else {
            learningObjectsDs.removeItem(learningObjectsDs.getItem());
            learningObjectsDs.commit();
            learningObjectsDs.refresh();
        }
    }

    protected boolean isInUse(LearningObject item) {
        return commonService.getCount(CourseSectionObject.class,
                "select e.sectionObject from tsadv$CourseSection e" +
                        " where e.sectionObject.content.id = :learningObjectId " +
                        " and e.course.id is not null and e.course.deleteTs is null ",
                ParamsMap.of("learningObjectId", item.getId())) > 0;
    }

}
package kz.uco.tsadv.bproc.beans.helper;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.WindowProperty;

import javax.inject.Inject;
import java.util.Objects;

/**
 * @author Alibek Berdaulet
 */
public abstract class AbstractBprocHelper {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected MetadataTools metadataTools;

    protected <T extends AbstractBprocRequest> ActivityType getActivityFromEntity(T entity) {
        return dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = :code")
                .parameter("code", getActivityCodeFromTableName(entity))
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();
    }

    protected <T extends AbstractBprocRequest> String getActivityCodeFromTableName(T entity) {
        StringBuilder builder = new StringBuilder();
        String tableName = metadataTools.getDatabaseTable(entity.getMetaClass());
        builder.append(tableName, Objects.requireNonNull(tableName).indexOf('_') + 1, tableName.length());
        builder.append("_APPROVE");
        return builder.toString();
    }
}

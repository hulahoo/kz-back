package kz.uco.tsadv.converter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.View;
import com.haulmont.reports.converter.GsonSerializationSupport;
import com.haulmont.reports.entity.DataSet;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportTemplate;

import java.io.IOException;

public class EntityGsonSerializationSupport extends GsonSerializationSupport {

    public EntityGsonSerializationSupport() {
        exclusionPolicy = (objectClass, propertyName) ->
                Report.class.isAssignableFrom(objectClass) && "xml".equalsIgnoreCase(propertyName)
                        || ReportTemplate.class.isAssignableFrom(objectClass) && "content".equals(propertyName);
    }

    @Override
    protected void writeFields(JsonWriter out, Entity entity) throws IOException {
        super.writeFields(out, entity);
        if (entity instanceof DataSet) {
            out.name("view");
            out.value(gsonBuilder.create().toJson(((DataSet) entity).getView()));
        }
    }

    @Override
    protected void readUnresolvedProperty(Entity entity, String propertyName, JsonReader in) throws IOException {
        if (entity instanceof DataSet && "view".equals(propertyName)) {
            String viewDefinition = in.nextString();
            View view = gsonBuilder.create().fromJson(viewDefinition, View.class);
            ((DataSet) entity).setView(view);
        } else {
            super.readUnresolvedProperty(entity, propertyName, in);
        }
    }
}

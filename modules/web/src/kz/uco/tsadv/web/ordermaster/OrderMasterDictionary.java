package kz.uco.tsadv.web.ordermaster;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.PersonEducation;
import kz.uco.tsadv.modules.recruitment.model.PersonExperience;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author adilbekov.yernar
 */
@Component
public class OrderMasterDictionary {

    @Inject
    private MessageTools messageTools;

    @Inject
    private Metadata metadata;

    private Map<String, MetaClassModel> entitiesMetaClass = new HashMap<>();
    private Map<String, Object> entitiesAsOptions = new TreeMap<>();

    private <T> void put(Class<T> clazz, String frameName) {
        MetaClass metaClass = metadata.getClass(clazz);
        if (metaClass != null) {
            entitiesMetaClass.put(metaClass.getName(), new MetaClassModel(metaClass, frameName));
            entitiesAsOptions.put(messageTools.getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass);
        }
    }

    public MetaClassModel find(String entityName) {
        if (entitiesMetaClass.isEmpty()) loadData();
        return entitiesMetaClass.get(entityName);
    }

    public Map<String, MetaClassModel> getEntitiesMetaClass() {
        if (entitiesMetaClass.isEmpty()) loadData();
        return entitiesMetaClass;
    }

    public Map<String, Object> getEntitiesAsOptions() {
        if (entitiesAsOptions.isEmpty()) loadData();
        return entitiesAsOptions;
    }

    private void loadData() {
        put(PersonExt.class, "pcf-main-data");
        put(PersonContact.class, "pcf-contacts");
        put(PersonDocument.class, "pcf-documents");
        put(Address.class, "pcf-addresses");
        put(Beneficiary.class, "pcf-beneficiary");
        put(Case.class, "caseFrame");

        put(AssignmentExt.class, "pcf-assignment-master");
        put(Agreement.class, "pcf-agreement");
        put(PersonExperience.class, "pcf-experience");
        put(Dismissal.class, "pcf-dismissal");
        put(TradeUnion.class, "pcf-trade-union");

        put(Salary.class, "pcf-salary");
        put(SurCharge.class, "pcf-sur-charge");

        put(PersonEducation.class, "pcf-education");

        put(Absence.class, "tsadv$Absence.browse");
        put(AbsenceBalance.class, "tsadv$AbsenceBalance.browse");
        put(BusinessTrip.class, "pcf-trip");
    }

    public class MetaClassModel {
        private MetaClass metaClass;
        private String frameName;

        MetaClassModel(MetaClass metaClass, String frameName) {
            this.metaClass = metaClass;
            this.frameName = frameName;
        }

        public MetaClass getMetaClass() {
            return metaClass;
        }

        public void setMetaClass(MetaClass metaClass) {
            this.metaClass = metaClass;
        }

        public String getFrameName() {
            return frameName;
        }

        public void setFrameName(String frameName) {
            this.frameName = frameName;
        }
    }
}

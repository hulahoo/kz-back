package kz.uco.tsadv.web.modules.learning.certificationenrollment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.learning.enums.CertificationCalculateType;
import kz.uco.tsadv.modules.learning.enums.CertificationPeriod;
import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.tsadv.modules.learning.model.Certification;
import kz.uco.tsadv.modules.learning.model.CertificationEnrollment;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

public class CertificationEnrollmentEdit extends AbstractEditor<CertificationEnrollment> {

    @Inject
    private DataManager dataManager;

    @Named("fieldGroup.startDate")
    private DateField<Date> startDateField;

    @Named("fieldGroup.nextDate")
    private DateField nextDateField;

    @Named("fieldGroup.certification")
    private PickerField<Certification> certificationField;

//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//
//        HasValue.ValueChangeEvent valueChangeListener = e -> calculateNextDate();
//
//        startDateField.addValueChangeListener((Consumer<HasValue.ValueChangeEvent<Date>>) valueChangeListener);
//
//        certificationField.addValueChangeListener((Consumer<HasValue.ValueChangeEvent<Certification>>) valueChangeListener);
//    }

    @Override
    protected void postInit() {
        super.postInit();

        if (PersistenceHelper.isNew(getItem())) {
            getItem().setStatus(CertificationStatus.ACTIVE);
        }
    }

    @Override
    public boolean validateAll() {
        if (super.validateAll()) {
            if (hasActiveCertification()) {
                String message = String.format(getMessage("add.cert.enrollment.error"),
                        getItem().getPersonGroup().getPerson().getFullName(),
                        getItem().getCertification().getCourse().getName());

                showNotification(getMessage("msg.warning.title"), message, NotificationType.TRAY_HTML);
                return false;
            }

            if (hasActiveEnrollment()) {
                String message = String.format(getMessage("add.cert.enrollment.error.2"),
                        getItem().getPersonGroup().getPerson().getFullName(),
                        getItem().getCertification().getCourse().getName());

                showNotification(getMessage("msg.warning.title"), message, NotificationType.TRAY_HTML);
                return false;
            }

            return true;
        }
        return false;
    }

    private boolean hasActiveCertification() {
        boolean isCreate = PersistenceHelper.isNew(getItem());

        LoadContext<CertificationEnrollment> loadContext = LoadContext.create(CertificationEnrollment.class);
        LoadContext.Query query = LoadContext.createQuery(String.format(
                "select e from tsadv$CertificationEnrollment e " +
                        "where e.personGroupId.id = :pId " +
                        "and e.certification.id = :cId " +
                        "and e.status = :status %s",
                isCreate ? "" : String.format("and e.id <> '%s'", getItem().getId())));
        query.setParameter("pId", getItem().getPersonGroup().getId());
        query.setParameter("cId", getItem().getCertification().getId());
        query.setParameter("status", CertificationStatus.ACTIVE);
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    private boolean hasActiveEnrollment() {
        LoadContext<Enrollment> loadContext = LoadContext.create(Enrollment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$Enrollment e " +
                        "where e.personGroupId.id = :pId " +
                        "and e.course.id = :cId " +
                        "and e.status in (1, 2, 3, 6)");
        query.setParameter("pId", getItem().getPersonGroup().getId());
        query.setParameter("cId", getItem().getCertification().getCourse().getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    private void calculateNextDate() {
        Certification certification = certificationField.getValue();
        Date startDate = startDateField.getValue();
        Date nextDate = null;
        if (startDate != null && certification != null) {
            if (certification.getCalculateType().equals(CertificationCalculateType.FIRST_ATTEMPT)) {
                CertificationPeriod period = certification.getPeriod();
                if (period != null) {
                    nextDate = period.calculateNextDate(startDate);
                }
            }
        } else {
            nextDate = null;
        }

        nextDateField.setValue(nextDate);
    }
}
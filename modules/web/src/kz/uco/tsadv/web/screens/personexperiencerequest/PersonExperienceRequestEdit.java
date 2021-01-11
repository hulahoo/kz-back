package kz.uco.tsadv.web.screens.personexperiencerequest;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import kz.uco.tsadv.modules.recruitment.model.PersonExperienceRequest;
import kz.uco.tsadv.service.DatesService;

import javax.inject.Inject;
import java.io.File;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;

@UiController("tsadv_PersonExperienceRequest.edit")
@UiDescriptor("person-experience-request-edit.xml")
@EditedEntityContainer("personExperienceRequestDc")
@LoadDataBeforeShow
public class PersonExperienceRequestEdit extends StandardEditor<PersonExperienceRequest> {

    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected FileUploadField upload;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected InstanceContainer<PersonExperienceRequest> personExperienceRequestDc;
    @Inject
    protected CollectionPropertyContainer<FileDescriptor> attachmentsDc;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected DatesService datesService;

    @Subscribe("startMonthField")
    protected void onStartMonthFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        setTimeAttributs(event.getValue(), personExperienceRequestDc.getItem().getEndMonth());
    }

    @Subscribe("endMonthField")
    protected void onEndMonthFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        setTimeAttributs(personExperienceRequestDc.getItem().getStartMonth(), event.getValue());
    }

    protected void setTimeAttributs(Date startDate, Date endDate) {
        if (startDate != null &&
                endDate != null) {
            Period period = datesService.getPeriodFromTwoDates(startDate, endDate);
            if (period != null) {
                personExperienceRequestDc.getItem().setYears(period.getYears());
                personExperienceRequestDc.getItem().setMonths(period.getMonths());
                personExperienceRequestDc.getItem().setDays(period.getDays());
            }
        }
    }

    @Subscribe("upload")
    protected void onUploadFileUploadSucceed(FileUploadField.FileUploadSucceedEvent event) {
        File file = fileUploadingAPI.getFile(upload.getFileId());
        FileDescriptor fd = upload.getFileDescriptor();
        try {
            fileUploadingAPI.putFileIntoStorage(upload.getFileId(), fd);
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving file to FileStorage", e);
        }
        dataManager.commit(fd);
        if (personExperienceRequestDc.getItem().getAttachments() == null) {
            personExperienceRequestDc.getItem().setAttachments(new ArrayList<FileDescriptor>());
        }
        attachmentsDc.getDisconnectedItems().add(fd);
        personExperienceRequestDc.getItem().getAttachments().add(fd);
    }

    @Subscribe
    protected void onValidation(ValidationEvent event) {
        if (personExperienceRequestDc.getItem().getStartMonth() != null
                && personExperienceRequestDc.getItem().getEndMonth() != null
                && personExperienceRequestDc.getItem().getStartMonth()
                .after(personExperienceRequestDc.getItem().getEndMonth())) {
            event.getErrors().add(messageBundle.getMessage("expireDateIsOut"));
        }
    }
}
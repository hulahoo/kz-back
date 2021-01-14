package kz.uco.tsadv.web.screens.personreasonchangingjob;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonReasonChangingJob;

@UiController("tsadv_PersonReasonChangingJob.edit")
@UiDescriptor("person-reason-changing-job-edit.xml")
@EditedEntityContainer("personReasonChangingJobDc")
@LoadDataBeforeShow
public class PersonReasonChangingJobEdit extends StandardEditor<PersonReasonChangingJob> {
}
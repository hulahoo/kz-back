package kz.uco.tsadv.web.modules.recruitment.requisition.search;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Address;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class SearchCandidate extends AbstractWindow {
    @Inject
    protected DataGrid<PersonGroupExt> personGroupExtsDataGrid;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected PickerField pickerField;
    protected Map<String, Object> param;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected CollectionDatasource<PersonGroupExt, UUID> personGroupExtsDs;
    protected Set<PersonGroupExt> personGroupExtSet;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
        if (params.get("requisition") != null) {
            pickerField.setValue((Requisition) params.get("requisition"));
        }
        DataGrid.Column personContacts = personGroupExtsDataGrid.addGeneratedColumn("personContacts", new DataGrid.ColumnGenerator<PersonGroupExt, Label>() {
            @Override
            public Label getValue(DataGrid.ColumnGeneratorEvent<PersonGroupExt> event) {
                Label label = componentsFactory.createComponent(Label.class);
                List<PersonContact> personContacts = event.getItem().getPersonContacts();
                StringBuilder out = new StringBuilder();
                if (personContacts != null && !personContacts.isEmpty()) {
                    for (PersonContact personContact : personContacts) {
                        if (personContact.getStartDate() != null && personContact.getEndDate() != null
                                && !personContact.getStartDate().after(CommonUtils.getSystemDate())
                                && !personContact.getEndDate().before(CommonUtils.getSystemDate()))
                            out.append(personContact.getType().getLangValue()).append(" - ").append(personContact.getContactValue()).append("\n");
                    }
                }
                label.setValue(out.toString());
                return label;
            }

            @Override
            public Class<Label> getType() {
                return Label.class;
            }
        });
        personContacts.setRenderer(new WebComponentRenderer());
        personContacts.setCaption(getMessage("contacts"));
        personGroupExtsDataGrid.getColumn("city").setFormatter(value -> {
            List<Address> addresses = (List<Address>) value;
            for (Address address : addresses) {
                if (address != null && address.getAddressType() != null && address.getAddressType().getCode() != null
                        && address.getAddressType().getCode().equals("20") && address.getStartDate() != null
                        && address.getEndDate() != null && !address.getStartDate().after(CommonUtils.getSystemDate())
                        && !address.getEndDate().before(CommonUtils.getSystemDate())
                        && address.getCity() != null) {
                    return address.getCity();
                }
            }
            return "";
        });
        personGroupExtsDataGrid.getColumn("dateOfBirth").setFormatter(value -> {
            PersonExt personExt = (PersonExt) value;
            Date dateOfBirth = personExt.getDateOfBirth();
            if (dateOfBirth != null) {
                Date now = CommonUtils.getSystemDate();
                Integer age;
                age = now.getYear() - dateOfBirth.getYear();
                if ((now.getMonth() - dateOfBirth.getMonth()) > 0) {
                    return ((Integer) (age - 1)).toString();
                } else if (((now.getMonth() - dateOfBirth.getMonth()) == 0)
                        && ((now.getDay() - dateOfBirth.getDay()) > 0)) {
                    return ((Integer) (age - 1)).toString();
                }
                return age.toString();
            } else {
                return getMessage("noBirthDate");
            }
        });
    }

    public void onAddToJobRequest(Component source) {
        Set<PersonGroupExt> selected = personGroupExtsDataGrid.getSelected();
        List<PersonGroupExt> personGroupExts = selected.stream().collect(Collectors.toList());

        Requisition requisition = (Requisition) param.get("requisition");
        CollectionDatasource<JobRequest, UUID> jobRequestsDs = (CollectionDatasource<JobRequest, UUID>) param.get("jobRequestsDs");
        if (requisition != null) {
            List<PersonGroupExt> personGroupExtsReserved = commonService.getEntities(PersonGroupExt.class,
                    "select jr.candidatePersonGroup from tsadv$JobRequest jr " +
                            " where jr.requisition.id <> :requisitionId " +
                            " and jr.isReserved = true",
                    ParamsMap.of("requisitionId", requisition), View.LOCAL);
            for (PersonGroupExt personGroupExt : personGroupExts) {
                JobRequest jobRequest = metadata.create(JobRequest.class);
                jobRequest.setCandidatePersonGroup(personGroupExt);
                jobRequest.setRequisition(requisition);
                jobRequest.setRequestStatus(personGroupExtsReserved.stream().filter(personGroupExt1 ->
                    personGroupExt1.getId().equals(personGroupExt.getId())
                ).findFirst().orElse(null) != null ? JobRequestStatus.FROM_RESERVE : JobRequestStatus.DRAFT);
                jobRequest.setRequestDate(CommonUtils.getSystemDate());
                jobRequest.setRequisition(requisition);
                jobRequestsDs.addItem(jobRequest);
                dataManager.commit(jobRequest);
                personGroupExtsDs.excludeItem(personGroupExt);
                jobRequestsDs.refresh();
            }

        }
    }
}
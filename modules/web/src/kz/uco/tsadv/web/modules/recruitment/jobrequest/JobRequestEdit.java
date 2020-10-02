
package kz.uco.tsadv.web.modules.recruitment.jobrequest;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.recruitment.dictionary.DicSource;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobRequestEdit extends AbstractEditor<JobRequest> {

    @Named("fieldGroup.requisition")
    protected PickerField requisitionField;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Datasource<JobRequest> jobRequestDs;
    @Inject
    protected Messages messages;
    protected Map<String, Object> map = new HashMap<>();

    @Override
    protected void initNewItem(JobRequest item) {
        super.initNewItem(item);
        item.setRequestDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        map = params;
        jobRequestDs.addItemPropertyChangeListener(e -> {
            if ("source".equals(e.getProperty())) {
                if (e.getValue() != null && "OTHER".equals(((DicSource)e.getValue()).getCode())) {
                    fieldGroup.getField("otherSource").setRequired(true);
                } else {
                    fieldGroup.getField("otherSource").setRequired(false);
                }
            }
        });
        requisitionField.setEditable(!params.containsKey("fromJobrequestNew"));
    }

    @Override
    protected void postInit() {
        Map params = new HashMap();
        if (map.containsKey("parentFrameId")) {
            if ("base$Person.candidate".equals(map.get("parentFrameId"))) {
                fieldGroup.getField("candidatePersonGroup").setEditable(false);//todo candidate in frames
                params.put("candidatePersonGroupId", getItem().getCandidatePersonGroup().getId());
                params.put("requisitionType", 1);
                params.put("status", RequisitionStatus.OPEN);
                Utils.customizeLookup(fieldGroup.getField("requisition").getComponent(), "tsadv$Requisition.lookup", WindowManager.OpenType.DIALOG, params);
            } else if ("tsadv$Requisition.edit".equals(map.get("parentFrameId"))) {
                fieldGroup.getField("requisition").setEditable(false);
                params.put("requisitionId", getItem().getRequisition().getId());
                Utils.customizeLookup(fieldGroup.getField("candidatePersonGroup").getComponent(), "base$PersonGroup.candidate", WindowManager.OpenType.DIALOG, params);
            }
        }
        /*if (this.getParentDs() != null && this.getParentDs().getDsContext() != null)
            switch (this.getParentDs().getDsContext().getFrameContext().getFrame().getId()) {
                case "base$Person.candidate":
                    fieldGroup.getField("candidatePersonGroup").setEditable(false);
                    break;
                case "tsadv$Requisition.edit":
                    fieldGroup.getField("requisition").setEditable(false);
                    break;
                default:
                    break;
            }*/
        if (PersistenceHelper.isNew(getItem())) {
            fieldGroup.getField("jobRequestReason").setVisible(false);
            fieldGroup.getField("reason").setVisible(false);
            if (getItem().getRequestStatus() == null) {
                getItem().setRequestStatus(JobRequestStatus.ON_APPROVAL);
            }
        }

        if (getItem().getRequestStatus() == JobRequestStatus.FROM_RESERVE) {
            fieldGroup.getField("requestStatus").setEditable(true);
            LookupField requestStatus = (LookupField) fieldGroup.getField("requestStatus").getComponent();

            Map<String, JobRequestStatus> optionsMap = new HashMap<>();
            optionsMap.put(messages.getMessage(JobRequestStatus.FROM_RESERVE), JobRequestStatus.FROM_RESERVE);
            optionsMap.put(messages.getMessage(JobRequestStatus.ON_APPROVAL), JobRequestStatus.ON_APPROVAL);
            optionsMap.put(messages.getMessage(JobRequestStatus.SELECTED), JobRequestStatus.SELECTED);

            requestStatus.setOptionsMap(optionsMap);
        }

        super.postInit();
    }
}
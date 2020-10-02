package kz.uco.tsadv.web.attestationparticipant;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Attestation;
import kz.uco.tsadv.modules.learning.model.AttestationParticipant;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kz.uco.base.common.Null.checkNull;

public class AttestationParticipantBrowse extends AbstractLookup {

    @WindowParam(name = "attestation")
    protected Attestation attestation;

    @Named("attestationParticipantsTable.create")
    protected CreateAction attestationParticipantsTableCreate;
    @Named("attestationParticipantsTable.edit")
    protected EditAction attestationParticipantsTableEdit;
    protected Map<String,Object> param;
    @Inject
    protected GroupDatasource<AttestationParticipant, UUID> attestationParticipantsDs;
    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param=params;
        attestationParticipantsTableCreate.setWindowParams(params);
        attestationParticipantsTableEdit.setWindowParams(params);

        clearAndFillAttestationPersonsTable();
    }

    /**
     * Заполняет таблицу со всеми участниками текущей аттестации
     * https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
     */
    protected void clearAndFillAttestationPersonsTable() {
        try {
            checkNull(attestation, "Attestation is undefined in AttestationParticipantBrowse");

            clearAttestationPersonsTable();
            fillAttestationPersonsTable();

            attestationParticipantsTableCreate.setEnabled(true);
        } catch (Exception ex) {
            showNotification("Error during filling attestation persons table", NotificationType.TRAY);
        }
    }

    /**
     * Очищает таблицу со всеми участниками текущей аттестации
     * https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
     */
    protected void clearAttestationPersonsTable() {
        commonService.emNativeQueryUpdate(
                "" +
                        "delete from xxtsadv_all_persons_for_attestation_t " +
                        " where attestation_id = #attestationId",
                ParamsMap.of("attestationId", attestation.getId())

        );
    }

    /**
     * Заполняет таблицу со всеми участниками текущей аттестации
     * https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
     */
    protected void fillAttestationPersonsTable() {
        commonService.emNativeQueryUpdate(
                "" +
                        "insert into xxtsadv_all_persons_for_attestation_t ( " +
                        "   person_group_id, " +
                        "   attestation_id   " +
                        ") select person_group_id, " +
                        "         attestation_id   " +
                        "    from tsadv_all_persons_for_attestations_v " +
                        " where attestation_id = #attestationId",
                ParamsMap.of("attestationId", attestation.getId())
        );
    }

    public void onFillBtnClick() {
        Map<String,Object> mapToFill=new HashMap<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            mapToFill.put(entry.getKey(),entry.getValue());
        }
        mapToFill.put("fillPersons",null);
        AttestationParticipant participant=metadata.create(AttestationParticipant.class);
        participant.setAttestation((Attestation)param.get("attestation"));
        AbstractEditor abstractEditor = openEditor("tsadv$AttestationParticipant.edit", participant, WindowManager.OpenType.THIS_TAB,
                mapToFill);
        abstractEditor.addCloseListener(actionId -> {
           attestationParticipantsDs.refresh();
        });

    }
}
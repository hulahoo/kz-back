package kz.uco.tsadv.web.attestationparticipant;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.dictionary.DicAttestationEvent;
import kz.uco.tsadv.modules.learning.model.Attestation;
import kz.uco.tsadv.modules.learning.model.AttestationParticipant;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class AttestationParticipantEdit extends AbstractEditor<AttestationParticipant> {
    @Named("fieldGroup.personGroup")
    protected PickerField<PersonGroupExt> personGroupField;
    protected Map<String, Object> param;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected DataManager dataManager;

    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    protected Datasource<AttestationParticipant> attestationParticipantDs;

    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected ComponentsFactory componentsFactory;
    protected TextField personTypeField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;

        personTypeField = (TextField) fieldGroup.getFieldNN("personGroupPersonType").getComponentNN();

        if (params.containsKey("fillPersons")) {
            personGroupField.setVisible(false);

            removeAction("windowCommit");
            addAction(new BaseAction("windowCommit") {
                @Override
                public void actionPerform(Component component) {
                    fillAction();
                }

                @Override
                public String getCaption() {
                    return getMessage("fill");
                }
            });
        } else {
            PickerField.LookupAction personGroupLookupAction = personGroupField.getLookupAction();
            personGroupLookupAction.setLookupScreen("base$PersonGroupExt.lookup.for.attestation");
            personGroupLookupAction.setLookupScreenParamsSupplier(() -> {
                Map<String, Object> mapToPersonLookup = new HashMap<>();
                mapToPersonLookup.put("attestation", param.containsKey("attestation") ? ((Attestation) param.get("attestation")).getId() : "");
                return mapToPersonLookup;
            });
        }

        personGroupField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                PersonGroupExt personGroup = (PersonGroupExt) e.getValue();
                if (personGroup != null) {
                    PersonExt person = personGroup.getPerson();
                    if (person != null) {
                        DicPersonType type = person.getType();
                        personTypeField.setValue(type);
                    }
                }
            } else {
                personTypeField.setValue(null);
            }
        });
        FieldGroup.FieldConfig attestationEventConfig = fieldGroup.getField("event");
        PickerField attestationEventPickerField = componentsFactory.createComponent(PickerField.class);
        attestationEventPickerField.setDatasource(attestationParticipantDs, attestationEventConfig.getProperty());

        attestationEventPickerField.addAction(new BaseAction("openAttestationEvent") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Map<String, Object> map = new HashMap<>();
                if (attestationParticipantDs.getItem().getResult() != null) {
                    map.put("dicAttestationResultId", attestationParticipantDs.getItem().getResult() != null ?
                            attestationParticipantDs.getItem().getResult().getId() : null);
                    openLookup("tsadv$DicAttestationEvent.browse",
                            (items -> {
                                for (Object o : items) {
                                    getItem().setEvent((DicAttestationEvent) o);
                                }
                            }), WindowManager.OpenType.THIS_TAB, map);
                } else {
                    showNotification(getMessage("error"), NotificationType.TRAY);
                }
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        attestationEventPickerField.addClearAction();
        attestationEventConfig.setComponent(attestationEventPickerField);
    }

    @Override
    protected void initNewItem(AttestationParticipant item) {
        super.initNewItem(item);
        if (param.containsKey("attestation")) {
            item.setAttestation((Attestation) param.get("attestation"));
        }
    }

    private void fillAction() {
        /**
         * temp data for validation
         * */
        getItem().setPersonGroup(metadata.create(PersonGroupExt.class));

        if (validateAll()) {
            Map<Integer, Object> paramToqwery = new HashMap<>();
            paramToqwery.put(1, param.containsKey("attestation") ? ((Attestation) param.get("attestation")).getId() : "");
            String qwery = "With param as (select a.id " +
                    "           ,(select count(*) from tsadv_attestation_organization ao " +
                    "         where a.id = ao.attestation_id  " +
                    "          and ao.delete_ts is null) as count_org  " +
                    "        ,(select count(*) from tsadv_attestation_position ap  " +
                    "          where a.id = ap.attestation_id  " +
                    "          and ap.delete_ts is null) as count_pos  " +
                    "        ,(select count(*) from tsadv_attestation_job aj  " +
                    "          where a.id = aj.attesta41tion_id  " +
                    "          and aj.delete_ts is null) as count_job    " +
                    "           from tsadv_attestation a" +
                    "            where a.id= ?1),  " +
                    "Org As  " +
                    "(Select o.Group_Id  " +
                    ",o.Id  " +
                    ",Oo2.Organization_Name_Lang1 As Org_Name  " +
                    ",Org_Str.Organization_Group_Id  " +
                    "From Base_Organization o  " +
                    "Join Tsadv_Organization_Structure Org_Str  " +
                    "On org_str.\"path\" Like '%*' || o.Group_Id || '*%'  " +
                    "Or Org_Str.Organization_Group_Id = o.Group_Id  " +
                    "Join Base_Organization Oo2  " +
                    "On Oo2.Group_Id = Org_Str.Organization_Group_Id  " +
                    "join tsadv_attestation_organization ao  " +
                    " on ao.organization_group_id = o.Group_Id  " +
                    " and ao.delete_ts is null  " +
                    "Where 1 = 1  " +
                    "And current_date Between Org_Str.Start_Date And Org_Str.End_Date  " +
                    "And current_date Between o.Start_Date And o.End_Date  " +
                    "And o.Delete_Ts Is Null  " +
                    "And 1=  case when ao.include_child = true then 1  " +
                    "             when ao.include_child = false and o.group_Id = Org_Str.Organization_Group_Id then 1  " +
                    "             else 0  " +
                    "             end                 " +
                    ")    " +
                    "select  " +
                    "  pp.group_id as person_group_id " +
                    " FROM base_assignment aa  " +
                    " JOIN base_person pp  " +
                    "   ON pp.group_id = aa.person_group_id   " +
                    "  and aa.delete_ts is null  " +
                    " join tsadv_dic_assignment_status ast  " +
                    "  on aa.assignment_status_id = ast.id  " +
                    " JOIN base_position ps  " +
                    "   ON ps.group_id = aa.position_group_id  " +
                    " JOIN tsadv_job j  " +
                    "   ON j.group_id = aa.job_group_id   " +
                    " JOIN base_organization o  " +
                    "   ON o.group_id = aa.organization_group_id    " +
                    " join param  " +
                    "   on  1= 1  " +
                    " where  current_date BETWEEN pp.start_date AND pp.end_date  " +
                    " and  current_date BETWEEN aa.start_date AND aa.end_date  " +
                    " and aa.primary_flag = true  " +
                    " and ast.code = 'ACTIVE'  " +
                    "  and 1= (case   " +
                    "         when param.count_org = 0 then 1  " +
                    "         when param.count_org > 0 and aa.organization_group_id in (select org.organization_group_id from Org) then 1  " +
                    "       else 0 end)  " +
                    "  and 1= (case   " +
                    "         when param.count_pos = 0 then 1  " +
                    "         when param.count_pos > 0 and aa.position_group_id in (select pos.position_group_id from tsadv_attestation_position Pos where pos.delete_ts is null) then 1  " +
                    "       else 0 end)  " +
                    "  and 1= (case   " +
                    "         when param.count_job = 0 then 1  " +
                    "         when param.count_job > 0 and aa.job_group_id in (select Job.job_group_id from tsadv_attestation_job Job where job.delete_ts is null) then 1  " +
                    "       else 0 end)  " +
                    "  and aa.person_group_id not in (select atp.person_group_id from tsadv_attestation_participant atp where atp.attestation_id = ?1 and atp.delete_ts is null) ";
            List<UUID> resultList = (List) commonService.emNativeQueryResultList(qwery, paramToqwery);
            List<PersonGroupExt> personGroupExtList = commonService.getEntities(PersonGroupExt.class,
                    "select e from base$PersonGroupExt e where e.id in :list",
                    ParamsMap.of("list", resultList), "personGroupExt.lookup.for.attestation");

            if (personGroupExtList.isEmpty()) {
                close(CLOSE_ACTION_ID, true);
            }
            List<AttestationParticipant> attestationParticipantList = new ArrayList<>();

            for (PersonGroupExt personGroupExt : personGroupExtList) {
                AttestationParticipant attestationParticipant = metadata.create(AttestationParticipant.class);
                attestationParticipant = metadataTools.deepCopy(getItem());
                attestationParticipant.setId(UUID.randomUUID());
                attestationParticipant.setPersonGroup(personGroupExt);
                attestationParticipantList.add(attestationParticipant);
            }
            if (!dataManager.commit(new CommitContext(attestationParticipantList)).isEmpty()) {
                close(CLOSE_ACTION_ID, true);
            }
        }
    }

    @Override
    public boolean close(String actionId) {
        if (param.containsKey("fillPersons")) {
            return super.close(actionId, true);
        }
        return super.close(actionId);
    }
}
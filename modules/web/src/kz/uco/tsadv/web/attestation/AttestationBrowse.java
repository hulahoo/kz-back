package kz.uco.tsadv.web.attestation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.learning.model.Attestation;
import kz.uco.tsadv.modules.learning.model.AttestationParticipant;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class AttestationBrowse extends AbstractLookup {
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    @Inject
    protected GroupTable<Attestation> attestationsTable;
    @Inject
    protected Button employerBtn;
    @Inject
    protected Button removeBtn;
    @Inject
    protected GroupDatasource<Attestation, UUID> attestationsDs;
    @Named("attestationsTable.create")
    private CreateAction attestationsTableCreate;
    @Inject
    private UserSessionSource userSessionSource;

    public Component generateSubjectToAttestationCell(Attestation entity) {
        return subjectToAttestation(entity);
    }

    public Component generatePassedAttestationCell(Attestation entity) {
        return countAttestationParticipants(entity, "passedAttestation");
    }

    public Component generateNotPassedAttestationCell(Attestation entity) {
        return countAttestationParticipants(entity, "notPassedAttestation");
    }

    public Component generateNotAppearCell(Attestation entity) {
        return notAppear(entity);
    }

    protected Component notAppear(Attestation entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Long count = commonService.getCount(
                AttestationParticipant.class,
                "select e from tsadv$AttestationParticipant e " +
                        " where e.attestation.id=:attestation " +
                        " and e.notAppeared = true ",
                ParamsMap.of("attestation", entity.getId()));
        label.setValue(count.toString());
        return label;
    }

    protected Component countAttestationParticipants(Attestation entity, String code) {
        Label label = componentsFactory.createComponent(Label.class);
        Long count = commonService.getCount(
                AttestationParticipant.class,
                "select e from tsadv$AttestationParticipant e " +
                        " where e.attestation.id=:attestation " +
                        " and e.result.code=:code ",
                ParamsMap.of("attestation", entity.getId(), "code", code));
        label.setValue(count.toString());
        return label;
    }

    protected Component subjectToAttestation(Attestation entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Long count = commonService.getCount(
                AttestationParticipant.class,
                "select e from tsadv$AttestationParticipant e " +
                        " where e.attestation.id=:attestation " ,
                ParamsMap.of("attestation", entity.getId()));
        label.setValue(count.toString());
        return label;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);


        attestationsDs.addItemChangeListener(e -> {
            attestationsDsItemChangeListener(e);
        });

        employerBtn.setAction(new BaseAction("employeeAction") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                if (attestationsTable.getSingleSelected() != null) {
                    AbstractWindow attestationWindow = openWindow("tsadv$AttestationParticipant.employeer.browse",
                            WindowManager.OpenType.THIS_TAB, ParamsMap.of(
                                    "attestation", attestationsTable.getSingleSelected(),
                                    "organizationGroup", null));
                    attestationWindow.addCloseListener(actionId -> {
                        attestationsDs.refresh();
                    });
                }
            }
        });
        employerBtn.setEnabled(false);

        if (userSessionSource.getUserSession().getAttribute("parentOrganization") != null) {
            OrganizationGroupExt organizationGroupExt = commonService.getEntity(OrganizationGroupExt.class, UUID.fromString(userSessionSource.getUserSession().getAttribute("parentOrganization")));
            attestationsTableCreate.setInitialValues(ParamsMap.of("organizationGroup", organizationGroupExt));
        }
    }

    protected void attestationsDsItemChangeListener(Datasource.ItemChangeEvent<Attestation> e) {
        if (attestationsTable.getSingleSelected() != null) {
            employerBtn.setEnabled(true);
            Long attestationId = commonService.getCount(AttestationParticipant.class,
                    "select e from tsadv$AttestationParticipant e " +
                            " where e.attestation.id = :attestationId  and e.deleteTs is null",
                    ParamsMap.of("attestationId", attestationsTable.getSingleSelected().getId()));

            removeBtn.setEnabled(attestationId < 1);
        } else {
            employerBtn.setEnabled(false);
            removeBtn.setEnabled(false);
        }

    }
}
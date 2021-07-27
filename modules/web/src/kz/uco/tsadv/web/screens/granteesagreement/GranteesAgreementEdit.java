package kz.uco.tsadv.web.screens.granteesagreement;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.tsadv.modules.personal.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@UiController("tsadv_GranteesAgreement.edit")
@UiDescriptor("grantees-agreement-edit.xml")
@EditedEntityContainer("granteesAgreementDc")
public class GranteesAgreementEdit extends StandardEditor<GranteesAgreement> {
    @Inject
    protected InstanceContainer<GranteesAgreement> granteesAgreementDc;
    @Inject
    protected CollectionContainer<LearningResult> learningResultsDc;
    @Inject
    protected CollectionContainer<LearningResultPerSubject> learningResultsPerSubjectDc;
    @Inject
    protected CollectionContainer<Payments> paymentsDc;
    @Inject
    protected CollectionLoader<Scholarship> scholarshipDl;
    @Inject
    protected CollectionLoader<Payments> paymentsDl;
    @Inject
    protected CollectionLoader<LearningResultPerSubject> learningResultsPerSubjectDl;
    @Inject
    protected CollectionContainer<Scholarship> scholarshipDc;
    @Inject
    protected CollectionLoader<LearningResult> learningResultsDl;
    @Inject
    private InstanceLoader<GranteesAgreement> granteesAgreementDl;
    @Inject
    private GroupTable<LearningResult> learningResultTable;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private GroupTable<Payments> paymentsTable;
    @Inject
    private GroupTable<LearningResultPerSubject> learningResultPerSubjectTable;
    @Inject
    private GroupTable<Scholarship> scholarshipTable;
    @Inject
    private TabSheet tabSheet;
    @Named("learningResultPerSubjectTable.create")
    private CreateActionExt learningResultPerSubjectTableCreate;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        granteesAgreementDl.load();
        paymentsDl.setParameter("granteesAgreement", granteesAgreementDc.getItem());
        paymentsDl.load();
        learningResultsDl.setParameter("granteesAgreement", granteesAgreementDc.getItem());
        learningResultsDl.load();
        scholarshipDl.setParameter("granteesAgreement", granteesAgreementDc.getItem());
        scholarshipDl.load();
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (PersistenceHelper.isNew(granteesAgreementDc.getItem())) {
            tabSheet.setVisible(false);
        } else {
            tabSheet.setVisible(true);
        }
        learningResultTable.addSelectionListener(learningResultsSelectionEvent -> {
            learningResultsSelectionEvent.getSelected();
            if (!learningResultsSelectionEvent.getSelected().isEmpty()) {
                learningResultPerSubjectTableCreate.setEnabled(true);
                learningResultsPerSubjectDl.setParameter("learningResult", learningResultTable.getSingleSelected());
                learningResultsPerSubjectDl.load();
            } else {
                learningResultPerSubjectTableCreate.setEnabled(false);
                learningResultsPerSubjectDl.setParameter("learningResult", learningResultTable.getSingleSelected());
                learningResultsPerSubjectDl.load();
            }
        });
        learningResultsPerSubjectDc.addItemPropertyChangeListener(learningResultsPerSubjectItemPropertyChangeEvent -> {
            if ("score".equals(learningResultsPerSubjectItemPropertyChangeEvent.getProperty())) {
                List<LearningResultPerSubject> list = learningResultsPerSubjectDc.getItems().stream().filter(learningResultsPerSubject -> {
                    return learningResultsPerSubject.getLearningResult().equals(learningResultTable.getSingleSelected());
                }).collect(Collectors.toList());
                double averageScore = list.stream().mapToDouble(LearningResultPerSubject::getScore).sum();
                averageScore = averageScore / list.size();
                LearningResult learningResults = learningResultTable.getSingleSelected();
                learningResults.setAverageScore(averageScore);
                dataManager.commit(learningResults);
                learningResultsDl.load();
            }
        });
        learningResultsPerSubjectDc.addCollectionChangeListener(learningResultsPerSubjectCollectionChangeEvent -> {
            if (learningResultsPerSubjectCollectionChangeEvent != null &&
                    (learningResultsPerSubjectCollectionChangeEvent.getChangeType().equals(CollectionChangeType.ADD_ITEMS)
                            || learningResultsPerSubjectCollectionChangeEvent.getChangeType().equals(CollectionChangeType.REMOVE_ITEMS))) {
                List<LearningResultPerSubject> list = learningResultsPerSubjectDc.getItems().stream().filter(learningResultsPerSubject -> {
                    return learningResultsPerSubject.getLearningResult().equals(learningResultTable.getSingleSelected());
                }).collect(Collectors.toList());
                double averageScore = list.stream().mapToDouble(LearningResultPerSubject::getScore).sum();
                averageScore = averageScore / list.size();
                LearningResult learningResults = learningResultTable.getSingleSelected();
                learningResults.setAverageScore(averageScore);
                dataManager.commit(learningResults);
                learningResultsDl.load();
            }
        });
    }

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        tabSheet.setVisible(true);
    }


    @Subscribe("paymentsTable.create")
    public void onPaymentsTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(paymentsTable)
                .newEntity()
                .withInitializer(payments -> {
                    payments.setGranteesAgreement(granteesAgreementDc.getItem());
                    payments.setPersonGroup(granteesAgreementDc.getItem().getPersonGroup());
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> paymentsDl.load());
    }

    @Subscribe("learningResultTable.create")
    public void onLearningResultTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(learningResultTable)
                .newEntity()
                .withInitializer(learningResults -> {
                    learningResults.setGranteesAgreement(granteesAgreementDc.getItem());
                    learningResults.setPersonGroup(granteesAgreementDc.getItem().getPersonGroup());
                }).build().show()
                .addAfterCloseListener(afterCloseEvent -> learningResultsDl.load());
    }

    @Subscribe("learningResultPerSubjectTable.create")
    public void onLearningResultPerSubjectTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(learningResultPerSubjectTable)
                .newEntity()
                .withInitializer(learningResultsPerSubject ->
                        learningResultsPerSubject.setLearningResult(learningResultTable.getSingleSelected())).build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    learningResultsDl.load();
                    learningResultsPerSubjectDl.load();
                });
    }

    @Subscribe("scholarshipTable.create")
    public void onScholarshipTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(scholarshipTable)
                .newEntity()
                .withInitializer(scholarship ->
                        scholarship.setGranteesAgreement(granteesAgreementDc.getItem())).build().show()
                .addAfterCloseListener(afterCloseEvent -> learningResultsDl.load());
    }

    @Subscribe("learningResultPerSubjectTable.edit")
    public void onLearningResultPerSubjectTableEdit(Action.ActionPerformedEvent event) {
        screenBuilders.editor(learningResultPerSubjectTable)
                .editEntity(learningResultPerSubjectTable.getSingleSelected())
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> {
                    learningResultsDl.load();
                    learningResultsPerSubjectDl.load();
                });
    }
}
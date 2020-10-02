package kz.uco.tsadv.web.modules.learning.learnergroup;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.bean.LearnerHelper;
import kz.uco.tsadv.modules.learning.model.Learner;
import kz.uco.tsadv.modules.learning.model.LearnerGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class LearnerGroupComplexEdit extends AbstractLookup {

    protected String personLookupScreenId =
//            "person-group-browse-ro";   // -
//            "base$PersonGroup.browse.all.record";   // -
//                "base$PersonGroupAllPerson.browse"; // -
            "base$PersonGroupExt.for.enrollment.browse";    // +
//                "base$PersonGroupExt.lookup.for.attestation";   // +
//                "base$PersonGroup.browse";  // -
//                "tsadv$UserExtPersonGroup.browse";    // -


    @Inject
    protected GroupDatasource<LearnerGroup, UUID> learnerGroupsDs;
    @Inject
    protected GroupDatasource<Learner, UUID> learnersDs;

    @Named("learnersDataGrid.addLearnerAction")
    protected Action learnersTableAddLearnerAction;
    @Named("learnersDataGrid.addLearnersFromRequestAction")
    protected Action learnersTableAddLearnersFromRequestAction;

    @Inject
    protected DataGrid<Learner> learnersDataGrid;
    @Inject
    protected Label learnersBoxLabel;

    @Inject
    protected Messages messages;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected LearnerHelper learnerHelper;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setupLearnersActionsEnabledPerGroupSelection();
        setupLearnersBoxCaptionPerGroupSelection();
    }

    protected void setupLearnersBoxCaptionPerGroupSelection() {
        learnerGroupsDs.addItemChangeListener(event -> {
            String caption = messages.getMessage("kz.uco.tsadv.web.modules.learning.learner", "browseCaption");
            if (learnerGroupsDs.getItem() != null) {
                caption += ": " + learnerGroupsDs.getItem().getCode();
            }
            learnersBoxLabel.setValue(caption);
        });
    }

    protected void setupLearnersActionsEnabledPerGroupSelection() {
        learnerGroupsDs.addItemChangeListener(event -> {
            boolean isSelected = event.getItem() != null;
            learnersTableAddLearnerAction.setEnabled(isSelected);
            learnersTableAddLearnersFromRequestAction.setEnabled(isSelected);
        });
    }

    public void addLearner() {
        openLookup(
                personLookupScreenId,
                this::addLearnersAndNotifyUser,
                WindowManager.OpenType.DIALOG,
                null
        );
    }

    protected void addLearnersAndNotifyUser(@Nonnull Collection<PersonGroupExt> persons) {
        Collection<Learner> addedLearners = addLearnersAndCommit(
                learnerGroupsDs.getItem(),
                persons);
        if (addedLearners.size() > 0) {
            if (addedLearners.size() != 1) {
                showNotification(formatMessage("learnersAreAdded", addedLearners.size()), NotificationType.TRAY_HTML);
            }
            learnersDs.refresh();
            learnersDataGrid.setSelected(addedLearners);
        }
    }

    protected Collection<Learner> addLearnersAndCommit(
            @Nullable LearnerGroup learnerGroup,
            @Nonnull Collection<PersonGroupExt> persons) {
        Collection<Learner> addedLearners = new HashSet<>();
        if (learnerGroup == null) {
            showNotification("Выберите группу", NotificationType.TRAY);
            return addedLearners;
        }
        persons.forEach(personGroup -> {
            Learner learner = learnerHelper.addIfNotExists(learnerGroup, personGroup);
            if (learner != null) {
                addedLearners.add(learner);
            }
        });
        if (addedLearners.size() > 0) {
            dataManager.commit(new CommitContext(addedLearners));
        }
        return addedLearners;
    }

    public void addLearnersFromRequest() {
        showNotification("В планах по развитию");
    }

}
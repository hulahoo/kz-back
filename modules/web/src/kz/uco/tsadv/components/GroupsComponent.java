package kz.uco.tsadv.components;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.dbview.GradeSsView;
import kz.uco.tsadv.entity.dbview.JobSsView;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.uactivity.entity.Activity;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class GroupsComponent {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;

    public void setInDateGroups(AssignmentExt assignmentExt, Date inDate) {
        if (assignmentExt == null || inDate == null) return;
        Optional.ofNullable(assignmentExt.getPositionGroup()).ifPresent(positionGroupExt -> positionGroupExt.getPositionInDate(inDate));
        Optional.ofNullable(assignmentExt.getOrganizationGroup()).ifPresent(organizationGroupExt -> organizationGroupExt.getOrganizationInDate(inDate));
        Optional.ofNullable(assignmentExt.getJobGroup()).ifPresent(jobGroup -> jobGroup.getJobInDate(inDate));
        Optional.ofNullable(assignmentExt.getGradeGroup()).ifPresent(gradeGroup -> gradeGroup.getGradeInDate(inDate));
    }

    public void setInDateGroups(PositionExt position, Date inDate) {
        if (position == null || inDate == null) return;
        Optional.ofNullable(position.getOrganizationGroupExt()).ifPresent(organizationGroupExt -> organizationGroupExt.getOrganizationInDate(inDate));
        Optional.ofNullable(position.getJobGroup()).ifPresent(jobGroup -> jobGroup.getJobInDate(inDate));
        Optional.ofNullable(position.getGradeGroup()).ifPresent(gradeGroup -> gradeGroup.getGradeInDate(inDate));
    }

    public void setInDateGroups(PositionGroupExt positionGroup,
                                OrganizationGroupExt organizationGroup,
                                JobGroup jobGroup,
                                GradeGroup gradeGroup,
                                Date inDate) {
        if (inDate == null) return;
        Optional.ofNullable(positionGroup).ifPresent(p -> p.getPositionInDate(inDate));
        Optional.ofNullable(organizationGroup).ifPresent(o -> o.getOrganizationInDate(inDate));
        Optional.ofNullable(jobGroup).ifPresent(j -> j.getJobInDate(inDate));
        Optional.ofNullable(gradeGroup).ifPresent(g -> g.getGradeInDate(inDate));
    }

    public PickerField.LookupAction addLookupActionGradeGroup(PickerField newGrade, AbstractEditor abstractEditor, String property) {
        PickerField.LookupAction lookupActionGrade = new PickerField.LookupAction(newGrade) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                GradeSsView gradeSsView = (GradeSsView) valueFromLookupWindow;
                GradeGroup groupExt = gradeSsView.getGradeGroup();
                groupExt = groupExt != null ? dataManager.reload(groupExt, "gradeGroup.browse") : null;
                Date dateFrom = abstractEditor.getItem().getValue(property);
                if (Objects.nonNull(dateFrom)) {
                    Optional.ofNullable(groupExt).ifPresent(p -> p.getGradeInDate(dateFrom));
                }
                return groupExt;
            }
        };
        lookupActionGrade.setLookupScreen("tsadv$GradeSsView.browse");
        newGrade.addAction(lookupActionGrade);
        return lookupActionGrade;
    }

    public PickerField.LookupAction addLookupActionJobGroup(PickerField newJob, AbstractEditor abstractEditor, String property) {
        PickerField.LookupAction lookupActionJob = new PickerField.LookupAction(newJob) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                JobSsView jobSsView = (JobSsView) valueFromLookupWindow;
                JobGroup groupExt = jobSsView.getJobGroup();
                groupExt = groupExt != null ? dataManager.reload(groupExt, "jobGroup.browse") : null;
                Date dateFrom = abstractEditor.getItem().getValue(property);
                if (Objects.nonNull(dateFrom)) {
                    Optional.ofNullable(groupExt).ifPresent(p -> p.getJobInDate(dateFrom));
                }
                return groupExt;
            }
        };
        lookupActionJob.setLookupScreen("tsadv$JobSsView.browse");
        newJob.addAction(lookupActionJob);
        return lookupActionJob;
    }

    public PickerField.LookupAction addLookupActionPositionGroup(PickerField newPosition, AbstractEditor abstractEditor, String property) {
        PickerField.LookupAction lookupActionPosition = new PickerField.LookupAction(newPosition) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                PositionSsView positionSsView = (PositionSsView) valueFromLookupWindow;
                PositionGroupExt groupExt = positionSsView.getPositionGroup();
                groupExt = groupExt != null ? dataManager.reload(groupExt, "positionGroup.change.request") : null;
                Date dateFrom = abstractEditor.getItem().getValue(property);
                if (Objects.nonNull(dateFrom)) {
                    Optional.ofNullable(groupExt).ifPresent(p -> p.getPositionInDate(dateFrom));
                }
                return groupExt;
            }
        };
        lookupActionPosition.setLookupScreen("tsadv$PositionSsView.browse");
        newPosition.addAction(lookupActionPosition);
        return lookupActionPosition;
    }

    public String getChangedIcon(boolean isChanged) {
        return isChanged ? "images/check.png" : null;
    }

    public void addActivityBodyToWindow(@Nonnull AbstractWindow abstractWindow, @Nonnull UUID activityId) {
        Activity activity = AppBeans.get(CommonService.class).getEntity(Activity.class, activityId, View.LOCAL);
        if (activity == null) {
            throw new RuntimeException(String.format("Activity[%s] not found!", activityId));
        }
        addActivityBodyToWindow(abstractWindow, activity);
    }

    public void addActivityBodyToWindow(@Nonnull AbstractWindow abstractWindow, @Nonnull Activity activity) {
        if (abstractWindow.getComponent("activityGroupBoxLayout") != null) return;
        GroupBoxLayout groupBoxLayout = componentsFactory.createComponent(GroupBoxLayout.class);
        ScrollBoxLayout scrollBoxLayout = componentsFactory.createComponent(ScrollBoxLayout.class);
        Label label3 = componentsFactory.createComponent(Label.class);
        label3.setValue(activity.getNotificationBody());
        label3.setHtmlEnabled(true);
        label3.setStyleName("p-notify-label");
        scrollBoxLayout.add(label3);
        groupBoxLayout.add(scrollBoxLayout);
        groupBoxLayout.setId("activityGroupBoxLayout");
        abstractWindow.add(groupBoxLayout, 0);
    }
}

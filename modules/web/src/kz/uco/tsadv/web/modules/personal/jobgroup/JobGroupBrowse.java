package kz.uco.tsadv.web.modules.personal.jobgroup;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.dictionary.DicEmployeeCategory;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.web.modules.filterconfig.FilterConfig;
import kz.uco.tsadv.web.modules.personal.job.JobEdit;
import kz.uco.tsadv.web.modules.recruitment.requisition.config.FullDicJobforManagerConig;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobGroupBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<JobGroup, UUID> jobGroupsDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CollectionDatasource<Job, UUID> listDs;

    @Inject
    protected Table<Job> historyTable;

    @Inject
    protected ExportDisplay exportDisplay;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected CollectionDatasource<DicEmployeeCategory, UUID> employeeCategoriesDs;
    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;
    @Inject
    protected UserSession userSession;
    @Inject
    protected FullDicJobforManagerConig fullDicJobforManagerConig;

    @Inject
    protected GroupTable<JobGroup> jobGroupsTable;
    @Inject
    protected Filter jobGroupsFilter;
    @Inject
    private FilterConfig filterConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.get("fromRequisitionBrowseSelf") != null && params.get("fromRequisitionBrowseSelf").equals(true)) {
            if (!fullDicJobforManagerConig.getEnabled()) {
                if (params.containsKey("groupId")) {
                    if (params.get("groupId") != null) {
                        jobGroupsDs.setQuery("select cbp.jobGroup " +
                                "                from base$PositionExt bp, tsadv$PositionStructure ps, base$PositionExt cbp " +
                                "                where 1=1 " +
                                "                and ps.organizationGroupPath like concat('%',concat(bp.organizationGroupExt.id,'%')) " +
                                "                and cbp.organizationGroupExt.id=ps.organizationGroup.id " +
                                "                and :session$systemDate between ps.startDate and ps.endDate " +
                                "                and :session$systemDate between cbp.startDate and cbp.endDate " +
                                "                and :session$systemDate between bp.startDate and bp.endDate " +
                                "                and bp.group.id= :param$groupId ");
                    }
                }
            }
        }

        if (params.get("endDatePosition") != null) {
            jobGroupsDs.setQuery("select e\n" +
                    "                           from tsadv$JobGroup e\n" +
                    "                           join e.list j\n" +
                    "                          where :param$endDatePosition between j.startDate and j.endDate");
        }


        if (params.get("posOrgJob") != null && (boolean) params.get("posOrgJob")) {
            jobGroupsDs.setQuery("select e " +
                    "                           from tsadv$JobGroup e " +
                    "                           join e.list j " +
                    "                          where :session$systemDate between j.startDate and j.endDate " +
                    "                                and e.id = COALESCE(:param$jobGroupId,e.id) " +
                    "                                and ( e.id = COALESCE(:param$orgGroupId, e.id) or" +
                    "                                       e.id in ( select p.jobGroup.id from base$PositionExt p " +
                    "                                                    where p.organizationGroupExt.id = COALESCE(:param$orgGroupId, p.organizationGroupExt.id) " +
                    "                                                           and :session$systemDate between p.startDate and p.endDate )) " +
                    "                           order by e.updateTs desc");
        }
    }

    public void openJob() {
        Map<String, Object> params = new HashMap<>();
        params.put("firstRow", Boolean.TRUE);
        openJobEditor(metadata.create(Job.class), params);
    }

    public void downloadInstruction(JobGroup jobGroup, String name) {
        Job job = jobGroup.getJob();
        if (job != null) {
            LoadContext<Job> loadContext = LoadContext.create(Job.class);
            loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Job e where e.id = :jId")
                    .setParameter("jId", job.getId()))
                    .setView("job.instruction");
            Job downloadJob = dataManager.load(loadContext);
            if (downloadJob != null) {
                exportDisplay.show(new ByteArrayDataProvider(downloadJob.getInstruction()), downloadJob.getInstructionName());
            }
        }
    }

    public void edit() {
        JobGroup jobGroup = jobGroupsDs.getItem();
        if (jobGroup != null) {
            Job job = jobGroup.getJob();
            if (job != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("firstRow", Boolean.TRUE);
                openJobEditor(job, params);
            }
        }
    }

    protected void openJobEditor(Job job, Map<String, Object> params) {
        JobEdit jobEdit = (JobEdit) openEditor("tsadv$Job.edit", job, WindowManager.OpenType.THIS_TAB, params);
        jobEdit.addCloseWithCommitListener(new CloseWithCommitListener() {
            @Override
            public void windowClosedWithCommitAction() {
                JobGroup jobGroupEdited = jobEdit.getItem().getGroup();
                jobGroupsDs.refresh();
                jobGroupsTable.repaint();
                try {
                    jobGroupsTable.setSelected(jobGroupEdited);
                } catch (IllegalStateException e) {
                }
            }
        });


    }

    public void editHistory() {
//        Utils.editHistory(listDs.getItem(), jobGroupsDs.getItem().getList(), this, jobGroupsDs);

        Job item = listDs.getItem();

        List<Job> items = jobGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openJobEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        Job item = listDs.getItem();
        List<Job> items = jobGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        listDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        listDs.modifyItem(items.get(index + 1));
                                    }

                                    listDs.removeItem(item);

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        jobGroupsDs.removeItem(item.getGroup());
                                        jobGroupsDs.getDsContext().commit();
                                    }

                                    jobGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        Job item = listDs.getItem();
        List<Job> items = jobGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    items.get(index - 1).setEndDate(items.get(items.size() - 1).getEndDate());
                                    listDs.modifyItem(items.get(index - 1));

                                    while (index < items.size())
                                        listDs.removeItem(items.get(index));

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        jobGroupsDs.removeItem(item.getGroup());
                                        jobGroupsDs.getDsContext().commit();
                                    }

                                    jobGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }
}
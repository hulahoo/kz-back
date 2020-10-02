package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Address;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PersonMentor;
import kz.uco.tsadv.service.NationalIdentifierService;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueFioDateException;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.UniqueIinException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InternEditor extends AbstractEditor<PersonExt> {
    @Inject
    protected Metadata metadata;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DataSupplier dataSupplier;

    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Table<Address> addressesTable;
    @Inject
    protected Datasource<PersonExt> personExtDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected PickerField mentorField;
    protected PersonMentor mentor;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected NationalIdentifierService nationalIdentifierService;

    @Override
    protected void initNewItem(PersonExt personExt) {
        super.initNewItem(personExt);
        PersonGroupExt personGroupExt = metadata.create(PersonGroupExt.class);
        personExt.setGroup(personGroupExt);
        personExt.setStartDate(CommonUtils.getSystemDate());
        personExt.setEndDate(CommonUtils.getEndOfTime());
        personExt.setType(commonService.getEntity(DicPersonType.class, "INTERNSHIP"));
        if (personGroupExt.getList() != null) {
            personGroupExt.getList().add(personExt);
        } else {
            ArrayList<PersonExt> personExts = new ArrayList<>();
            personExts.add(personExt);
            personGroupExt.setList(personExts);
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (PersistenceHelper.isNew(getItem())) {
            for (TabSheet.Tab tab : tabSheet.getTabs()) {
                if (!tab.getName().equalsIgnoreCase("mainInfo")) {
                    tab.setVisible(false);
                }
            }
        } else {
            setCaption(getItem().getFioWithEmployeeNumber());
        }
    }

    @Override
    public void ready() {
        super.ready();
        Map addressMap = new HashMap();
        addressMap.put("personGroup", getItem().getGroup());
        ((CreateAction) addressesTable.getAction("create")).setWindowParams(addressMap);
    }

    @Override
    public void commitAndClose() {
        if (!validateAll()) {
            return;
        }
        try {
            hasCadet(getItem());
            commitChanges();

        } catch (UniqueFioDateException ex) {
            showOptionDialog(getMessage("msg.warning.title"),
                    getMessage(ex.getMessage()),
                    MessageType.CONFIRMATION,
                    new DialogAction[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    commitChanges();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });
        } catch (Exception ex) {
            showNotification(getMessage("msg.warning.title"), ex.getMessage(), NotificationType.TRAY);
        }
        if (!PersistenceHelper.isNew(getItem())){
            personExtDs.refresh();
        }
    }

    protected void commitChanges() {
        boolean changed = false;

        if (PersistenceHelper.isNew(getItem())) {
            changed = super.commit();
        } else {
            changed = true;
            if (isModified()) {
                commit();
            } else {
                dataManager.commit(getItem());
                personExtDs.refresh();
                postInit();
            }
        }

        if (changed) {
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        }
    }

    protected void hasCadet(PersonExt person) throws Exception {
        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class);

        boolean isCreate = PersistenceHelper.isNew(person);

        StringBuilder query = new StringBuilder("select e from base$PersonExt e where (:sysDate between e.startDate and e.endDate)");

        LoadContext.Query loadContextQuery = LoadContext.createQuery("");

        String iin = person.getNationalIdentifier();

        boolean checkIin = false;
        if (iin != null && iin.trim().length() > 0 && !iin.equals("000000000000")) {
            checkIin = true;
            query.append(" and e.nationalIdentifier = :iin");
            loadContextQuery.setParameter("iin", iin);
        }

        if (isCreate && !checkIin) {
            query.append(" and e.firstName = :p1 and e.lastName = :p2 and e.dateOfBirth = :p3");
            loadContextQuery.setParameter("p1", person.getFirstName());
            loadContextQuery.setParameter("p2", person.getLastName());
            loadContextQuery.setParameter("p3", person.getDateOfBirth());
        }

        if (!isCreate) {
            query.append(" and e.id <> :pId");
            loadContextQuery.setParameter("pId", person.getId());
        }
        loadContextQuery.setParameter("sysDate", new Date());
        loadContextQuery.setQueryString(query.toString());
        loadContext.setQuery(loadContextQuery);

        if (dataManager.getCount(loadContext) > 0) {
            //if (checkIin) throw new UniqueIinException(getMessage("candidate.exist.iin"));

            /**
             * check unique FIO and dateOfBirth only create form
             * */
            if (isCreate) {
                throw new UniqueFioDateException(getMessage("candidate.exist.fid"));
            }
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            for (TabSheet.Tab tab : tabSheet.getTabs()) {
                if (!tab.isVisible()) {
                    tab.setVisible(true);
                }
            }
        }
        return true;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        nationalIdentifierValidate(errors);
    }

    protected void nationalIdentifierValidate(ValidationErrors validationErrors) {
        if (getItem() != null) {
            if (!nationalIdentifierService.checkNationalIdentifier(getItem().getNationalIdentifier(), getItem().getDateOfBirth(), getItem().getSex())) {
                showNotification(getMessage("incorrectlyID"), NotificationType.TRAY);
            }
            if (nationalIdentifierService.hasDuplicate(getItem())) {
                showNotification(getMessage("duplicate"), NotificationType.TRAY);
            }
        }
    }
}
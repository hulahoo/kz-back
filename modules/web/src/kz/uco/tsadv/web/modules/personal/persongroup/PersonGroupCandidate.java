package kz.uco.tsadv.web.modules.personal.persongroup;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonContact;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.model.PersonAttachment;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.person.PersonCandidate;

import javax.inject.Inject;
import java.util.*;

public class PersonGroupCandidate extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "50px";

    @Inject
    protected GroupDatasource<PersonGroupExt, UUID> personGroupsDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Button editBtn;

    @Inject
    protected GroupTable<PersonGroupExt> personGroupsTable;

    @Inject
    protected TextField personNameId;
    @Inject
    protected TextField personNameEn;
    @Inject
    protected ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("excludedPersons")) {
            personGroupsDs.setQuery(personGroupsDs.getQuery() + " and e.id not in :param$excludedPersons");
        }

        personNameId.addValueChangeListener((e) -> {
            personGroupsDs.refresh();
//            transliterationForSearch(e);
        });

        editBtn.setEnabled(personGroupsDs.getItem() != null);

        personGroupsDs.addItemChangeListener(new Datasource.ItemChangeListener<PersonGroupExt>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<PersonGroupExt> e) {
                editBtn.setEnabled(e.getItem() != null);
            }
        });

        personGroupsTable.addGeneratedColumn("jobRequestCount", entity -> {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
            int jobRequestCount = entity.getJobRequests().size();
            if (jobRequestCount > 0) {
                linkButton.setCaption(String.valueOf(jobRequestCount));
                linkButton.setAction(new BaseAction("openJobrequests") {
                    @Override
                    public void actionPerform(Component component) {
                        super.actionPerform(component);
                        PersonExt person = entity.getPerson();
                        openEditor("base$Person.candidate", person, WindowManager.OpenType.NEW_TAB,
                                ParamsMap.of("openTab", "jobRequests"));
                    }
                });
            }
            return linkButton;
        });

        personGroupsTable.addGeneratedColumn("resume", entity -> {
            List<PersonAttachment> attachments = entity.getPersonAttachment();
            if (!attachments.isEmpty()) {
                PersonAttachment personAttachment = attachments.stream()
                        .filter(a -> {
                            String s = Optional.ofNullable(a)
                                    .map(PersonAttachment::getCategory)
                                    .map(AbstractDictionary::getCode)
                                    .orElse(null);

                            return s != null ? s.equalsIgnoreCase("cv") : false;
//                                    a.getCategory().getCode().equalsIgnoreCase("cv");

                        })
                        .sorted((e1, e2) -> e2.getAttachment().getCreateDate().compareTo(e1.getAttachment().getCreateDate()))
                        .findFirst().orElse(null);
                if (personAttachment != null) {
                    return Utils.getFileDownload(personAttachment.getAttachment(), PersonGroupCandidate.this);
                }
            }
            return null;
        });

        personGroupsTable.addGeneratedColumn("contacts", entity -> {
            List<PersonContact> contacts = entity.getPersonContacts();
            if (!contacts.isEmpty()) {
                VBoxLayout vbox = componentsFactory.createComponent(VBoxLayout.class);
                contacts.forEach(e -> {
                    Link label = componentsFactory.createComponent(Link.class);
                    label.setCaption(e.getContactValue());
//                    label.setTarget(e.getContactValue());
                    if(e.getContactValue().contains("@")){
                        label.setUrl("mailto:"+e.getContactValue());
                    }else{
                        label.setTarget(e.getContactValue());
                    }
                    vbox.add(label);
                });
                return vbox;
            }
            return null;
        });
    }

    @Override
    public void ready() {
        super.ready();
        personGroupsTable.getColumn("userImage").setWidth(65);
    }

    public void openCandidate() {
        openCandidateEditor(metadata.create(PersonExt.class), "CANDIDATE", false);
    }

    public void openStudent() {
        openCandidateEditor(metadata.create(PersonExt.class), "STUDENT", false);
    }

    public void openCandidateEdit() {
        PersonGroupExt personGroup = personGroupsDs.getItem();
        if (personGroup != null) {
            PersonExt person = personGroup.getPerson();
            if (person != null) {
                openCandidateEditor(person, null, true);
            }
        }
    }

    private void openCandidateEditor(PersonExt person, String personTypeCode, boolean editAction) {
        Map<String, Object> params = new HashMap<>();
        params.put("editAction", editAction);
        if (personTypeCode == null) {
            DicPersonType dicPersonType = person.getType();
            if (dicPersonType != null) {
                String code = dicPersonType.getCode();
                if (code != null) {
                    params.put(StaticVariable.PERSON_TYPE_CODE, code);
                }
            }
        } else {
            params.put(StaticVariable.PERSON_TYPE_CODE, personTypeCode);
        }

        PersonCandidate candidateEdit = (PersonCandidate) openEditor("base$Person.candidate", person, WindowManager.OpenType.THIS_TAB, params);
        candidateEdit.addCloseListener(actionId -> personGroupsDs.refresh());
    }

    public Component generateUserImageCell(PersonGroupExt entity) {
        Image image = WebCommonUtils.setImage(entity.getPerson().getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }

//    public void transliterationForSearch(ValueChangeEvent e) {
//        if (e.getValue() != null) {
//            personNameEn.setValue(Transliteration.toTranslit(e.getValue().toString()));
//        }
//    }
}
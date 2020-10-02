package kz.uco.tsadv.web.modules.personal.competencegroup;

import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.Scale;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.*;

public class CompetenceGroupLookup extends AbstractLookup {
    @Inject
    protected GroupDatasource<CompetenceGroup, UUID> competenceGroupsDs;
    protected Map<String, Object> param;
    //
//    @Inject
//    private VBoxLayout filterBox;
//    @Inject
//    private CollectionDatasource<Scale, UUID> scalesDs;
//    private Map<String, CustomFilter.Element> filterMap;
//    private CustomFilter customFilter;
//    @Inject
//    private GroupDatasource<CompetenceGroup, UUID> competenceGroupsDs;
//
//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//        if (params.get("alreadyExistCompetence") != null) {
//            competenceGroupsDs.setQuery("select e\n" +
//                    "                           from tsadv$CompetenceGroup e\n" +
//                    "                           join e.list c\n" +
//                    "                          where :session$systemDate between c.startDate and c.endDate\n" +
//                    "                            and (coalesce(:param$rcAvailableFilter, FALSE) = FALSE OR c.isRcAvailable = TRUE) and e.id not in :param$alreadyExistCompetence");
//        }
//        initFilterMap();
//
//        customFilter = CustomFilter.init(competenceGroupsDs, competenceGroupsDs.getQuery(), filterMap);
//        filterBox.add(customFilter.getFilterComponent());
//
//        customFilter.selectFilter("competenceName");
//    }
//
//    private void initFilterMap() {
//        filterMap = new LinkedHashMap<>();
//        filterMap.put("competenceName",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Competence.competenceName"))
//                        .setComponentClass(TextField.class)
//                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
//                        .setQueryFilter("lower(c.competenceName) ?")
//        );
//
//        filterMap.put("scale",
//                CustomFilter.Element
//                        .initElement()
//                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Scale"))
//                        .setComponentClass(LookupPickerField.class)
//                        .addComponentAttribute("optionsDatasource", scalesDs)
//                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
//                        .setQueryFilter("c.scale.id ?")
//        );
//
//    }
//


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param=params;
    }

    @Override
    public void ready() {
        super.ready();
        if(param.containsKey("excludeCompetence")){
            List<UUID> uuids = (List<UUID>) param.get("excludeCompetence");
            List<CompetenceGroup> exludingCompetenceGroups = new ArrayList<>();
            Collection<CompetenceGroup> items = competenceGroupsDs.getItems();
            for (CompetenceGroup item : items) {
                for (UUID uuid : uuids) {
                    if (item.getId().equals(uuid)){
                        exludingCompetenceGroups.add(item);
                    }
                }
            }
            for (CompetenceGroup exludingCompetenceGroup : exludingCompetenceGroups) {
                competenceGroupsDs.excludeItem(exludingCompetenceGroup);
            }
        }
    }

    public Component getCompetenceGroupDownloadBtn(CompetenceGroup entity) {
        return Utils.getFileDownload(entity.getCompetence().getAttachment(), CompetenceGroupLookup.this);
    }
}
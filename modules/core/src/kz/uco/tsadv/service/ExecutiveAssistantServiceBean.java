package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewRepository;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dto.PersonDto;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(ExecutiveAssistantService.NAME)
public class ExecutiveAssistantServiceBean implements ExecutiveAssistantService {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected ViewRepository viewRepository;

    @Override
    public List<PersonDto> getManagerList(UUID positionGroupId) {
        return dataManager.load(PersonGroupExt.class)
                .query("select e.personGroup from base$AssignmentExt e " +
                        "   join e.assignmentStatus s " +
                        "where s.code in ('ACTIVE','SUSPENDED') " +
                        "   and e.primaryFlag = true" +
                        "   and current_date between e.startDate and e.endDate" +
                        "   and e.positionGroup.id in (" +
                        "       select e.managerPositionGroup.id from tsadv_ExecutiveAssistants e " +
                        "           where e.assistancePositionGroup.id = :assistancePositionGroupId" +
                        "               and current_date between e.startDate and e.endDate" + ")")
                .parameter("assistancePositionGroupId", positionGroupId)
                .view(new View(PersonGroupExt.class)
                        .addProperty("list", viewRepository.getView(PersonExt.class, View.LOCAL))
                        .addProperty("assignments", viewRepository.getView(AssignmentExt.class, View.LOCAL)
                                .addProperty("positionGroup")
                                .addProperty("assignmentStatus", new View(DicAssignmentStatus.class).addProperty("code")))
                )
                .list()
                .stream()
                .map(this::parseToPersonProfileDto)
                .collect(Collectors.toList());
    }

    protected PersonDto parseToPersonProfileDto(PersonGroupExt personGroupExt) {
        PersonDto dto = new PersonDto();
        PersonExt person = personGroupExt.getPerson();
        AssignmentExt currentAssignment = personGroupExt.getCurrentAssignment();
        dto.setFullName(person.getFullName());
        dto.setPositionGroupId(currentAssignment.getPositionGroup().getId());
        dto.setGroupId(personGroupExt.getId());
        dto.setId(person.getId());
        return dto;
    }
}
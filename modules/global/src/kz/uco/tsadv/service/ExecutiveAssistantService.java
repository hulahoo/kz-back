package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dto.PersonDto;

import java.util.List;
import java.util.UUID;

public interface ExecutiveAssistantService {
    String NAME = "tsadv_ExecutiveAssistantService";

    List<PersonDto> getManagerList(UUID positionGroupId);
}
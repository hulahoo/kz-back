package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dto.LinkedinProfileDTO;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.net.URISyntaxException;
import java.util.UUID;

public interface LinkedinService {
    String NAME = "tsadv_LinkedinService";

    LinkedinProfileDTO getFilledProfileDTO(PersonGroupExt personGroup) throws Exception;

    String buildOauthLink(UUID personGroupId) throws URISyntaxException;

    void fetchAccessToken(UUID personGroupId, String code) throws Exception;
}
package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dto.LinkedinProfileDTO;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.app.LinkedinRequestsHandler;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.UUID;

@Service(LinkedinService.NAME)
public class LinkedinServiceBean implements LinkedinService {

    @Inject
    LinkedinRequestsHandler linkedinRequestsHandler;

    @Override
    public LinkedinProfileDTO getFilledProfileDTO(PersonGroupExt personGroup) throws Exception {
        return linkedinRequestsHandler.linkUrlToProfileAndMaybeGetProfileDTO(personGroup, true);
    }

    @Override
    public String buildOauthLink(UUID personGroupId) throws URISyntaxException {
        return linkedinRequestsHandler.buildOauthLink(personGroupId);
    }

    @Override
    public void fetchAccessToken(UUID personGroupId, String code) throws Exception {
        linkedinRequestsHandler.retrieveAndSetAccessToken(personGroupId, code);
    }
}
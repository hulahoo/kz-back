package kz.uco.tsadv.web.jobrequest;

import kz.uco.tsadv.modules.recruitment.model.Interview;

import java.util.List;
import java.util.UUID;

public interface DataGridDetailsGeneratorService {
    String NAME = "sampler_DataGridDetailsGeneratorService";

    List<Interview> loadInterviewsById(UUID jobRequestId);
}


package kz.uco.tsadv.controllers.tdc;


import kz.uco.tsadv.lms.pojo.LearningHistoryPojo;
import kz.uco.tsadv.service.LearningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("learning-history")
public class LearningController {

    @Inject
    private LearningService learningService;

    @GetMapping
    public List<LearningHistoryPojo> getLearningHistory(String personGroupId) {
        return learningService.learningHistory(UUID.fromString(personGroupId));
    }
}

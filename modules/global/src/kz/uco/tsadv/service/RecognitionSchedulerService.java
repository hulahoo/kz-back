package kz.uco.tsadv.service;


import java.util.UUID;

public interface RecognitionSchedulerService {
    String NAME = "tsadv_RecognitionSchedulerService";

    void distributionOfPoints();

    void checkMedals();

    /**
     * Don't call this method without active transaction
     */
    void checkMedals(UUID personGroupId);
}
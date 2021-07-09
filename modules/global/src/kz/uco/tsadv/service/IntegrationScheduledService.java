package kz.uco.tsadv.service;

public interface IntegrationScheduledService {
    String NAME = "tsadv_IntegrationScheduledService";

    void sendVacationScheduleRequestToOracle();
}
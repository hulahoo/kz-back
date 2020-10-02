package kz.uco.tsadv.service;


import java.util.Date;

public interface RestIntegrationLogService {
    String NAME = "tsadv_RestIntegrationLogService";

    void log(String requestId, String login, String methodName, String params, String message, Boolean success, Date dateTime);

    void log(String login, String methodName, String params, String message, Boolean success, Date dateTime);
}
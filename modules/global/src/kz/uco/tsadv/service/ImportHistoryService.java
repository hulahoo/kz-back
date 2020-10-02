package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.administration.importer.ImportHistory;

import java.util.Date;

public interface ImportHistoryService {
    String NAME = "tsadv_ImportHistoryService";

    void log(ImportHistory importHistory, String params, String message, int entitiesProcessed);

    void log(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed);

    void log(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed, Boolean success, Date dateTime);

    void error(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed, Throwable t);
    
    void error(ImportHistory importHistory,String message, Throwable t);

    void error(ImportHistory importHistory,String params, String message, int entitiesProcessed, Throwable t);

    void error(ImportHistory importHistory,String params, String message, Throwable t);

    void error(ImportHistory importHistory,String login, String params, String message, int entitiesProcessed, Throwable t, Date dateTime);

    void info(ImportHistory importHistory, String params, String message, int entitiesProcessed);

    void info(ImportHistory importHistory, String login, String params, String message, int entitiesProcessed);

    void info(ImportHistory importHistory, String login, String params, String message, int entitiesProcessed, Boolean success, Date dateTime);
}
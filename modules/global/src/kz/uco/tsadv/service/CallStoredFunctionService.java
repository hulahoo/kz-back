package kz.uco.tsadv.service;


import java.util.List;

public interface CallStoredFunctionService {
    String NAME = "tsadv_CallStoredFunctionService";

    void execSqlCallFunction(String sql);

    void execSqlCallProcedure(String sql);

    List<Object> execSqlCallProcedureList(String sql);

    Object execCallSqlFunction(String sql);
}
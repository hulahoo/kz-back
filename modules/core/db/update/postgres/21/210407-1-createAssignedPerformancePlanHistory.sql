create table TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    ORGANIZATION_BIN varchar(255),
    INTEGRATION_USER_LOGIN varchar(255),
    --
    STAGE_ID uuid,
    STATUS_ID uuid,
    ASSIGNED_PERFORMANCE_PLAN_ID uuid,
    --
    primary key (ID)
);
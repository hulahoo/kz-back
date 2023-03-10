create table TSADV_LEAVING_VACATION_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_REQUEST_ID uuid,
    REQUEST_DATE date,
    REQUEST_TYPE_ID uuid,
    VACATION_ID uuid,
    START_DATE date,
    END_DATA date,
    PLANNED_START_DATE date,
    COMMENT_ varchar(2500),
    --
    primary key (ID)
);
create table TSADV_ABS_PURPOSE_SETTING (
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
    ABSENCE_TYPE_ID uuid,
    ABSENCE_PURPOSE_ID uuid,
    ORDER_NUMBER integer,
    --
    primary key (ID)
);
create table TSADV_REST_INTEGRATION_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_ID text,
    LOGIN varchar(1000),
    METHOD_NAME text,
    PARAMS text,
    MESSAGE text,
    SUCCESS boolean not null,
    DATE_TIME date,
    --
    primary key (ID)
);

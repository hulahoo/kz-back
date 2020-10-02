create table TSADV_IMPORT_HISTORY_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(500),
    STACKTRACE varchar(1000),
    LOGIN varchar(255),
    PARAMS varchar(500),
    SUCCESS boolean not null,
    DATE_TIME timestamp,
    FILE_ID uuid,
    ENTITIES_PROCESSED integer,
    LEVEL_ varchar(50),
    --
    primary key (ID)
);

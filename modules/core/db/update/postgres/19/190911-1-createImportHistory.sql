create table TSADV_IMPORT_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID uuid,
    IMPORT_SCENARIO_ID uuid,
    STARTED timestamp,
    FINISHED timestamp,
    --
    primary key (ID)
);

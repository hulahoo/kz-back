create table TSADV_IMPORT_LEARNING_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROCESSED integer,
    LOADING_DATE timestamp not null,
    SUCCESS boolean not null,
    FILE_ID uuid not null,
    ERROR_MESSAGE text,
    --
    primary key (ID)
);

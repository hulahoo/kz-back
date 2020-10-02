create table TSADV_RECOGNITION_LOGIN_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LOGIN varchar(255) not null,
    SESSION_ID uuid not null,
    DATE_TIME timestamp not null,
    --
    primary key (ID)
);

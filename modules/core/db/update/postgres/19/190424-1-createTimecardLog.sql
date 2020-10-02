create table TSADV_TIMECARD_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    INITIATOR_LOGIN varchar(255) not null,
    START_DATE timestamp not null,
    END_DATE timestamp not null,
    SUCCESS boolean not null,
    ERROR_TEXT varchar(5000),
    --
    primary key (ID)
);

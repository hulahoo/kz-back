create table TSADV_TIMECARD_CORRECTION (
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
    ASSIGNMENT_GROUP_ID uuid not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
);

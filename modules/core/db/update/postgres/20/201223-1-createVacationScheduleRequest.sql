create table TSADV_VACATION_SCHEDULE_REQUEST (
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
    REQUEST_NUMBER integer not null,
    REQUEST_DATE date not null,
    PERSON_GROUP_ID uuid not null,
    STATUS_ID uuid not null,
    START_DATE date,
    END_DATE date,
    ABSENCE_DAYS integer,
    --
    primary key (ID)
);
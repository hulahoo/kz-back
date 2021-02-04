create table TSADV_SCHEDULE_OFFSETS_REQUEST (
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
    REQUEST_NUMBER bigint,
    STATUS_ID uuid,
    REQUEST_DATE date,
    --
    PERSON_GROUP_ID uuid,
    PURPOSE_ID uuid,
    CURRENT_SCHEDULE_ID uuid,
    NEW_SCHEDULE_ID uuid,
    DATE_OF_NEW_SCHEDULE date,
    DATE_OF_START_NEW_SCHEDULE date,
    DETAILS_OF_ACTUAL_WORK varchar(2000),
    AGREE boolean,
    ACQUAINTED boolean,
    --
    primary key (ID)
);
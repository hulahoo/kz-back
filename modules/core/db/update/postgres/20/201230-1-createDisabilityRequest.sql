create table TSADV_DISABILITY_REQUEST (
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
    DISABILITY_TYPE_ID uuid,
    ATTACHMENT_NAME varchar(255),
    ATTACHMENT bytea,
    DURATION_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    HAVE_DISABILITY varchar(50),
    GROUP_ varchar(2000),
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    DISABILITY_ID uuid,
    --
    primary key (ID)
);
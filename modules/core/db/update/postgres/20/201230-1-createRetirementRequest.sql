create table TSADV_RETIREMENT_REQUEST (
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
    RETIREMENT_TYPE_ID uuid,
    ISSEU_DOC_DATE date,
    DOCUMENT_NUMBER varchar(255),
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    RETIREMENT_ID uuid,
    FILE_ID uuid,
    REQUEST_STATUS_ID uuid,
    --
    primary key (ID)
);
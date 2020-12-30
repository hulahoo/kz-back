create table TSADV_PERSON_DOCUMENT_REQUEST (
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
    ISSUE_DATE date not null,
    EXPIRED_DATE date not null,
    ISSUED_BY varchar(500),
    ISSUING_AUTHORITY_ID uuid not null,
    DESCRIPTION varchar(2000),
    DOCUMENT_TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid,
    DOCUMENT_NUMBER varchar(255) not null,
    SERIES varchar(255),
    STATUS_ID uuid not null,
    FILE_ID uuid,
    REQUEST_STATUS_ID uuid not null,
    EDITED_PERSON_DOCUMENT_ID uuid,
    --
    primary key (ID)
);
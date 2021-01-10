create table TSADV_PERSON_QUALIFICATION_REQUEST (
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
    TYPE_ID uuid,
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    ASSIGN_VALIDATION_DATE date,
    ATTACHMENT_ID uuid,
    NOTE varchar(3000),
    EDUCATIONAL_INSTITUTION_NAME varchar(2000),
    DIPLOMA varchar(2000),
    TYPE_NAME varchar(2000),
    ISSUED_DATE date,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
);
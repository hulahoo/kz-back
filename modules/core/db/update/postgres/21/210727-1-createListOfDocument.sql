create table TSADV_LIST_OF_DOCUMENT (
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
    PERSON_GROUP_ID uuid not null,
    TYPE_ID uuid not null,
    FILE_ID uuid,
    DATE date not null,
    --
    primary key (ID)
);
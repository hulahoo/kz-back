create table TSADV_GUARDIAN (
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
    GUARDIAN_TYPE_ID uuid not null,
    FIELD_OF_ACTIVITY_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);
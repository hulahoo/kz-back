create table TSADV_PERSON_RELATIVES_HAVE_PROPERTY (
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
    HAVE_OR_NOT varchar(50),
    PROPERTY varchar(2000),
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
);
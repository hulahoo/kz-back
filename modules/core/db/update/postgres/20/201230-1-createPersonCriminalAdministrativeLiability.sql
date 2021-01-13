create table TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY (
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
    PERSON_GROUP_ID uuid,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    HAVE_LIABILITY varchar(50),
    REASON_PERIOD varchar(2000),
    --
    primary key (ID)
);
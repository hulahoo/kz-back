create table TSADV_PERSON_CLINIC_DISPANCER (
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
    HAVE_CLINIC_DISPANCER varchar(50),
    PERIOD_FROM varchar(2000),
    --
    primary key (ID)
);
create table TSADV_GRANTEES_AGREEMENT (
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
    CONTRACT_NUMBER varchar(255) not null,
    CONTRACT_DATE date not null,
    UNIVERSITY varchar(255) not null,
    AGREEMENT_NUMBER varchar(255) not null,
    AGREEMENT_DATE date not null,
    START_YEAR integer not null,
    STATUS_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);
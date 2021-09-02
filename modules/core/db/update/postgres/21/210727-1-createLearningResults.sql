create table TSADV_LEARNING_RESULTS (
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
    GRANTEES_AGREEMENT_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    STUDY_YEAR integer not null,
    SEMESTER varchar(255) not null,
    AVERAGE_SCORE double precision,
    SCHOLARSHIP decimal(19, 2) not null,
    --
    primary key (ID)
);
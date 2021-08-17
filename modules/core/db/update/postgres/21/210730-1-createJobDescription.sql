create table TSADV_JOB_DESCRIPTION (
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
    POSITION_GROUP_ID uuid not null,
    POSITION_DUTIES text not null,
    GENERAL_ADDITIONAL_REQUIREMENTS text,
    COMPULSORY_QUALIFICATION_REQUIREMENTS text,
    FILE_ID uuid,
    REQUEST_ID uuid,
    --
    primary key (ID)
);
create table TSADV_PERSON_REASON_CHANGING_JOB (
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
    REASON text,
    TEL_FULL_NAME_HR varchar(2000),
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
);
create table TSADV_DIC_INCENTIVE_RESULT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
);
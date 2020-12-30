create table TSADV_PERSON_EXPERIENCE_REQUEST (
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
    UNTIL_NOW boolean,
    COMPANY varchar(255) not null,
    JOB varchar(255) not null,
    START_MONTH date not null,
    END_MONTH date,
    DESCRIPTION varchar(4000),
    LOCATION varchar(2000),
    PART_TIME boolean,
    MINING_EXPERIENCE boolean,
    GROUP_EXPERIENCE boolean,
    INDUSTRY_ID uuid,
    REQUEST_STATUS_ID uuid,
    --
    primary key (ID)
);
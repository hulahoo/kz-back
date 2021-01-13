create table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST (
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    START_DATE date,
    END_DATE date,
    SPECIALTY varchar(2000),
    DIPLOMA varchar(2000),
    ISSUE_DATE date,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
);
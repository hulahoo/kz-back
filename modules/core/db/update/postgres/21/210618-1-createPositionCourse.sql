create table TSADV_POSITION_COURSE (
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
    COURSE_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
);
create table TSADV_COURSE_SCHEDULE (
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
    COURSE_ID uuid,
    NAME varchar(255),
    START_DATE timestamp,
    END_DATE timestamp,
    LEARNING_CENTER_ID uuid,
    ADDRESS varchar(255),
    MAX_NUMBER_OF_PEOPLE integer,
    --
    primary key (ID)
);
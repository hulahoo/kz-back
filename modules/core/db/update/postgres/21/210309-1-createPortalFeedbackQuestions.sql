create table TSADV_PORTAL_FEEDBACK_QUESTIONS (
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
    USER_ID uuid,
    PORTAL_FEEDBACK_ID uuid,
    TOPIC varchar(255),
    TEXT text,
    --
    primary key (ID)
);
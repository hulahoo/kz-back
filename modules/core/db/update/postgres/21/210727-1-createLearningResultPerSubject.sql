create table TSADV_LEARNING_RESULT_PER_SUBJECT (
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
    LEARNING_RESULT_ID uuid not null,
    SUBJECT varchar(255) not null,
    SCORE decimal(19, 2) not null,
    --
    primary key (ID)
);
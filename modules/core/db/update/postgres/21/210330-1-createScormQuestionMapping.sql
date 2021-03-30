create table TSADV_SCORM_QUESTION_MAPPING (
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
    LEARNING_OBJECT_ID uuid not null,
    CODE varchar(255) not null,
    QUESTION varchar(255) not null,
    --
    primary key (ID)
);
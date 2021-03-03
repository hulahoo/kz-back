create table TSADV_STUDENT_HOMEWORK (
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
    HOMEWORK_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    ANSWER text,
    ANSWER_FILE_ID uuid,
    IS_DONE boolean not null,
    TRAINER_COMMENT text,
    TRAINER_ID uuid,
    --
    primary key (ID)
);
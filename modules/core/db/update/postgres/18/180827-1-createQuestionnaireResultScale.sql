create table TSADV_QUESTIONNAIRE_RESULT_SCALE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    QUESTIONNAIRE_ID uuid not null,
    RESULT_LANG1 varchar(255) not null,
    RESULT_LANG2 varchar(255),
    RESULT_LANG3 varchar(255),
    RESULT_LANG4 varchar(255),
    RESULT_LANG5 varchar(255),
    MIN_ integer not null,
    MAX_ integer not null,
    --
    primary key (ID)
);

create table TSADV_LEARNING_FEEDBACK_ANSWER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_ integer not null,
    SCORE integer not null,
    ANSWER_LANG_VALUE1 varchar(2000) not null,
    ANSWER_LANG_VALUE2 varchar(2000),
    ANSWER_LANG_VALUE3 varchar(2000),
    ANSWER_LANG_VALUE4 varchar(2000),
    ANSWER_LANG_VALUE5 varchar(2000),
    FEEDBACK_QUESTION_ID uuid not null,
    --
    primary key (ID)
);

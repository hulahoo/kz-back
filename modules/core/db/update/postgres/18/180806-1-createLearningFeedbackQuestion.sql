create table TSADV_LEARNING_FEEDBACK_QUESTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    QUESTION_LANG_VALUE1 varchar(2000) not null,
    QUESTION_LANG_VALUE2 varchar(2000),
    QUESTION_LANG_VALUE3 varchar(2000),
    QUESTION_LANG_VALUE4 varchar(2000),
    QUESTION_LANG_VALUE5 varchar(2000),
    QUESTION_TYPE varchar(50) not null,
    --
    primary key (ID)
);

create table TSADV_LEARNING_FEEDBACK_TEMPLATE_QUESTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FEEDBACK_TEMPLATE_ID uuid not null,
    FEEDBACK_QUESTION_ID uuid not null,
    ORDER_ integer not null,
    --
    primary key (ID)
);

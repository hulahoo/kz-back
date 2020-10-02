create table TSADV_PERSON_QUESTION_ANSWER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid not null,
    QUESTION_ID uuid not null,
    ANSWER_ID uuid not null,
    DATE_ date not null,
    --
    primary key (ID)
);

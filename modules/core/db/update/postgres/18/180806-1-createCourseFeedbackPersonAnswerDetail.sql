create table TSADV_COURSE_FEEDBACK_PERSON_ANSWER_DETAIL (
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
    COURSE_ID uuid,
    COURSE_SECTION_SESSION_ID uuid,
    PERSON_GROUP_ID uuid not null,
    QUESTION_ID uuid not null,
    ANSWER_ID uuid,
    TEXT_ANSWER varchar(2000),
    SCORE integer not null,
    COURSE_FEEDBACK_PERSON_ANSWER_ID uuid not null,
    --
    primary key (ID)
);

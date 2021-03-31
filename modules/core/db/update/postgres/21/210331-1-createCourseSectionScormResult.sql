create table TSADV_COURSE_SECTION_SCORM_RESULT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COURSE_SECTION_ATTEMPT_ID uuid,
    QUESTION_ID uuid,
    ANSWER_TIME_STAMP timestamp,
    ANSWER text,
    IS_CORRECT boolean,
    SCORE integer,
    MAX_SCORE integer,
    MIN_SCORE integer,
    --
    primary key (ID)
);
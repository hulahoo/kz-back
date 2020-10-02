create table TSADV_COURSE_FEEDBACK_PERSON_ANSWER (
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
    COMPLETE_DATE date not null,
    RESPONSIBLE_ROLE varchar(50) not null,
    PERSON_GROUP_ID uuid not null,
    SUM_SCORE bigint,
    AVG_SCORE double precision,
    --
    primary key (ID)
);

create table TSADV_COURSE_TRAINER_ASSESSMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TRAINER_ID uuid not null,
    COURSE_ID uuid not null,
    ASSESSMENT_DATE date not null,
    ASSESSOR_ID uuid,
    SCORE double precision not null,
    --
    primary key (ID)
);

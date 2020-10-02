create table TSADV_COURSE_SESSION_ENROLLMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENROLLMENT_ID uuid not null,
    COURSE_SESSION_ID uuid not null,
    ENROLLMENT_DATE date not null,
    STATUS integer,
    COMMENT_ varchar(1000),
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);

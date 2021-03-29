create table TSADV_COURSE_PERSON_NOTE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COURSE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    NOTE varchar(2000) not null,
    --
    primary key (ID)
);
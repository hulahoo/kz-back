create table TSADV_SCORM_SUSPEND_DATA (
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
    COURSE_SECTION_ID uuid not null,
    SUSPEND_DATA text,
    --
    primary key (ID)
);
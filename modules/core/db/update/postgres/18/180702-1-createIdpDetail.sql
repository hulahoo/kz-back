create table TSADV_IDP_DETAIL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    IDP_ID uuid,
    COMPETENCE_ID uuid not null,
    SCALE_LEVEL_ID uuid,
    COURSE_ID uuid,
    EDUCATION_TYPE_ID uuid,
    DESCRIPTION text,
    TARGET_DATE date not null,
    DONE boolean not null,
    COMMENT_ text,
    REASON varchar(255),
    --
    primary key (ID)
);

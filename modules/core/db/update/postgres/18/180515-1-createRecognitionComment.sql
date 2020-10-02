create table TSADV_RECOGNITION_COMMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT varchar(2000) not null,
    PARENT_COMMENT_ID uuid,
    AUTHOR_ID uuid not null,
    RECOGNITION_ID uuid,
    --
    primary key (ID)
);

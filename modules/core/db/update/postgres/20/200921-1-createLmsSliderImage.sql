create table TSADV_LMS_SLIDER_IMAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    IMAGE_ID uuid,
    ORDER_ integer,
    SLIDER_ID uuid,
    --
    primary key (ID)
);

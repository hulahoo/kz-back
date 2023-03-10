create table TSADV_RESIZED_IMAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SIZE_ID uuid not null,
    ORIGINAL_IMAGE_ID uuid not null,
    RESIZED_IMAGE_ID uuid not null,
    --
    primary key (ID)
);
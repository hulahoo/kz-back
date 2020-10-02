create table TSADV_VIDEO_FILE_FOR_PLAY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    SOURCE_ID uuid not null,
    OUTPUT_FILE_ID uuid,
    STATUS varchar(50) not null,
    --
    primary key (ID)
);
